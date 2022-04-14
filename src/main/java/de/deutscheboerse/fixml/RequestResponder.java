/********************************************************************************
 *
 * DESCRIPTION:  FIXML Connection Test Tool - tool for receiving and sending AMQP
 *                                            messages via SSL broker interface
 *
 ********************************************************************************
 */
package de.deutscheboerse.fixml;

import org.apache.commons.cli.ParseException;
import org.slf4j.LoggerFactory;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.naming.NamingException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.UUID;

/**
 * RequestResponder creates temporary response queue, sends request message and
 * receives the response message from the response queue.
 */
public class RequestResponder extends BrokerConnector
{
    private MessageConsumer responseConsumer;
    private MessageProducer requestProducer;
    private Destination replyDestination;
    private final String msgContent;

    public RequestResponder(final RequestResponderOptions options) throws FileNotFoundException
    {
        super(options);
        logger = LoggerFactory.getLogger(RequestResponder.class);
        final String input = options.msgContentFileName;
        if (input == null)
        {
            msgContent = options.msgContent;
        }
        else
        {
            final String contentOrigin = "STDIN".equals(input) ? "from the STDIN ... " : "of the file " + input;
            try (final Scanner scanner = new Scanner(new InputStreamReader("STDIN".equals(input) ? System.in : new FileInputStream(input), StandardCharsets.UTF_8)))
            {
                logger.info("Reading content " + contentOrigin);
                scanner.useDelimiter("\\z");
                msgContent = scanner.next();
            }
            catch (FileNotFoundException e)
            {
                logger.error("Unable to open file " + input);
                throw e;
            }
        }
        properties.setProperty("topic.requestAddress", "request." + options.accountID);
        properties.setProperty("topic.replyAddress", "response/response." + options.accountID);
        properties.setProperty("queue.responseAddress", "response." + options.accountID);
    }

    public void connect() throws NamingException, JMSException, HandledException
    {
        super.connect();
        try
        {
            final Destination responseDestination = (Destination) context.lookup("responseAddress");
            final Destination requestDestination = (Destination) context.lookup("requestAddress");
            replyDestination = (Destination) context.lookup("replyAddress");
            responseConsumer = session.createConsumer(responseDestination);
            logger.info("Response consumer created");
            requestProducer = session.createProducer(requestDestination);
            logger.info("Request producer created");
        }
        catch (JMSException e)
        {
            logger.error("Failed to create sender or consumer");
            throw e;
        }
        catch (NamingException e)
        {
            logger.error("Failed to prepare the destinations");
            throw e;
        }
    }

    public void produceMessage() throws JMSException
    {
        try
        {
            final TextMessage message = session.createTextMessage(msgContent);
            message.setJMSReplyTo(replyDestination);
            message.setJMSCorrelationID(UUID.randomUUID().toString());
            requestProducer.send(message);
            logger.info("Request message sent");
        }
        catch (JMSException e)
        {
            logger.error("Failed to send message");
            throw e;
        }
    }

    public void consumeMessage() throws JMSException
    {
        super.consumeMessage(responseConsumer, true);
    }

    public static void main(String[] args) throws FileNotFoundException, JMSException, NamingException
    {
        final RequestResponderOptions options = new RequestResponderOptions();
        try
        {
            options.parse(args);
            options.printReceivedOptions();
            try (final RequestResponder requestResponder = new RequestResponder(options))
            {
                requestResponder.printInfo();
                requestResponder.checkCertificateStores();
                requestResponder.checkConnection();
                requestResponder.connect();
                for (int i = 0; i < options.messageCount; i++)
                {
                    requestResponder.produceMessage();
                    requestResponder.consumeMessage();
                }
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
