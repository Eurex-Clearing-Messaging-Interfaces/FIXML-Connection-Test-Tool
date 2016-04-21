/******************************************************************************** 
 *
 * DESCRIPTION:  FIXML Connection Test Tool - tool for receiving and sending AMQP
 *                                            messages via SSL broker interface
 *
 ********************************************************************************
 */

package de.deutscheboerse.fixml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.UUID;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.naming.NamingException;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.LoggerFactory;

/**
 * RequestResponder creates temporary response queue, sends request message and
 * receives the response message from the response queue. 
 */
public class RequestResponder extends BrokerConnector
{
    private MessageConsumer responseConsumer;
    private MessageProducer requestProducer;
    private Destination replyDestination;
    private String msgContent;
    
    public RequestResponder(final RequestResponderOptions options) throws FileNotFoundException
    {
        super(options);
        this.logger = LoggerFactory.getLogger(RequestResponder.class);
        if (options.msgContentFileName == null)
        {
            this.msgContent = options.msgContent;
        }
        else
        {
            String contentOrigin = "STDIN".equals(options.msgContentFileName) ? "from the STDIN ... " : "of the file " + options.msgContentFileName;
            try (Scanner scanner = new Scanner(
                 "STDIN".equals(options.msgContentFileName) ? new InputStreamReader(System.in) : new FileReader(options.msgContentFileName)))
            {
                this.logger.info("Reading content " + contentOrigin);
                scanner.useDelimiter("\\z");
                this.msgContent = scanner.next();
            }
            catch (FileNotFoundException ex)
            {
                this.logger.error("Unable to open file " + options.msgContentFileName);
                throw ex;
            }
        }
        String requestString;
        String replyString;
        String responseString;
        switch (this.options.amqpVersion)
        {
        case AMQP_0_10:
            requestString = String.format("request.%s; { node: { type: topic }, create: never }", options.accountID);
            replyString = String.format("response/response.%s.fixml-connection-test-tool; { create: receiver, node: { type: topic } }", 
                    options.accountID);
            responseString = String.format("response.%s.fixml-connection-test-tool; {create: receiver, assert: never, node: " +
                    "{ type: queue, x-declare: { auto-delete: true, exclusive: false, arguments:" +
                    "{'qpid.auto_delete_timeout': 60, 'qpid.policy_type': ring, 'qpid.max_count': 1000," +
                    "'qpid.max_size': 1000000}}, x-bindings: [{exchange: 'response', queue: " +
                    "'response.%s.fixml-connection-test-tool', key: 'response.%s.fixml-connection-test-tool'}]}}",
                    options.accountID, options.accountID, options.accountID);
            this.properties.setProperty("destination.requestAddress", requestString);
            this.properties.setProperty("destination.replyAddress", replyString);
            this.properties.setProperty("destination.responseAddress", responseString);
            break;
        case AMQP_1_0:
            requestString = String.format("request.%s", options.accountID);
            replyString = String.format("response/response.%s", options.accountID);
            responseString = String.format("response.%s",options.accountID);
            this.properties.setProperty("topic.requestAddress", requestString);
            this.properties.setProperty("topic.replyAddress", replyString);
            this.properties.setProperty("queue.responseAddress", responseString);
            break;
        default:
            this.logger.error("Unknown AMQP version '" + this.options.amqpVersion + "', requestAddress, replyAddress or responseAddress are not set");
            break;
        }
    }

    public void connect() throws NamingException, JMSException, HandledException
    {
        Destination responseDestination;
        Destination requestDestination;
        
        super.connect();
        try
        {
            responseDestination = (Destination) ctx.lookup("responseAddress");
            requestDestination = (Destination) ctx.lookup("requestAddress");
            this.replyDestination = (Destination) ctx.lookup("replyAddress");
            this.responseConsumer = this.session.createConsumer(responseDestination);
            this.logger.info("Response consumer created");
            this.requestProducer = this.session.createProducer(requestDestination);
            this.logger.info("Request producer created");
        }
        catch (JMSException ex)
        {
            this.logger.error("Failed to create sender or consumer");
            throw ex;
        }
        catch (NamingException ex)
        {
            this.logger.error("Failed to prepare the destinations");
            throw ex;
        }
    }
    
    public void produceMessage() throws JMSException
    {
        TextMessage message;
        try
        {
            message = session.createTextMessage(this.msgContent);
            message.setJMSReplyTo(this.replyDestination);
            message.setJMSCorrelationID(UUID.randomUUID().toString());
            requestProducer.send(message);
            this.logger.info("Request message sent");
        }
        catch (JMSException ex)
        {
            this.logger.error("Failed to send message");
            throw ex;
        }
    }
    
    public void consumeMessage() throws JMSException
    {
        super.consumeMessage(this.responseConsumer, true);
    }

    public static void main(String[] args) throws FileNotFoundException, JMSException, NamingException
    {
        RequestResponderOptions options = new RequestResponderOptions();
        try
        {
            options.parse(new GnuParser(), args);
            options.printReceivedOptions();
            try (RequestResponder obj = new RequestResponder(options))
            {
                obj.checkCertStores();
                obj.connect();
                obj.produceMessage();
                obj.consumeMessage();
            }
            catch (HandledException e)
            {
                LoggerFactory.getLogger(RequestResponder.class).error(e.getMessage());
            }
        }
        catch (ParseException e)
        {
            System.out.println(e.getMessage());
            options.printHelp();
        }
    }
}
