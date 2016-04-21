/******************************************************************************** 
 *
 * DESCRIPTION:  FIXML Connection Test Tool - tool for receiving and sending AMQP
 *                                            messages via SSL broker interface
 *
 ********************************************************************************
 */

package de.deutscheboerse.fixml;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.naming.NamingException;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;
import org.slf4j.LoggerFactory;

/**
 * Broadcast Receiver receives broadcasts from a persistent broadcast queue
 */
public class BroadcastReceiver extends BrokerConnector
{
    private MessageConsumer broadcastConsumer;

    public BroadcastReceiver(final BroadcastReceiverOptions options)
    {
        super(options);
        this.logger = LoggerFactory.getLogger(BroadcastReceiver.class);
        String bcastString;
        switch (this.options.amqpVersion)
        {
        case AMQP_0_10:
            bcastString = String.format("broadcast.%s.%s; { node: { type: queue }, create: never," +
                    "assert: never, mode: %s }", options.accountID, options.streamID,
                    options.consume ? "consume" : "browse");
            this.properties.setProperty("destination.broadcastAddress", bcastString);            
            break;
        case AMQP_1_0:
            bcastString = String.format("broadcast.%s.%s", options.accountID, options.streamID);
            this.properties.setProperty("queue.broadcastAddress", bcastString);   
            break;
        default:
            this.logger.error("Unknown AMQP version '" + this.options.amqpVersion + "', broadcastAddress is not set");
            break;
        }
    }

    public void connect() throws JMSException, HandledException, NamingException
    {
        Destination broadcastDestination;

        super.connect();
        try
        {
            broadcastDestination = (Destination) ctx.lookup("broadcastAddress");
            this.broadcastConsumer = this.session.createConsumer(broadcastDestination);
            this.logger.info("Broadcast consumer created");
        }
        catch (JMSException ex)
        {
            this.logger.error("Failed to create consumer");
            throw ex;
        }
        catch (NamingException ex)
        {
            this.logger.error("Failed to prepare the destinations");
            throw ex;
        }
    }

    public void consumeMessage() throws JMSException
    {
        super.consumeMessage(this.broadcastConsumer, false);
    }

    public static void main(String[] args) throws JMSException, NamingException
    {
        BroadcastReceiverOptions options = new BroadcastReceiverOptions();
        try
        {
            options.parse(new GnuParser(), args);
            options.printReceivedOptions();
            try (BroadcastReceiver obj = new BroadcastReceiver(options);)
            {
                obj.checkCertStores();
                obj.connect();
                obj.consumeMessage();
            }
            catch (HandledException e)
            {
                LoggerFactory.getLogger(BroadcastReceiver.class).error(e.getMessage());
            }
        }
        catch (ParseException e)
        {
            System.out.println(e.getMessage());
            options.printHelp();
        }
    }
}
