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

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import javax.naming.NamingException;

/**
 * Broadcast Receiver receives broadcasts from a persistent broadcast queue
 */
public class BroadcastReceiver extends BrokerConnector
{
    private MessageConsumer broadcastConsumer;

    public BroadcastReceiver(final BroadcastReceiverOptions options)
    {
        super(options);
        logger = LoggerFactory.getLogger(BroadcastReceiver.class);
        properties.setProperty("queue.broadcastAddress", String.format("broadcast.%s.%s", options.getAccountID(), options.getStreamID()));
    }

    public void connect() throws JMSException, HandledException, NamingException
    {
        super.connect();
        try
        {
            final Destination broadcastDestination = (Destination) context.lookup("broadcastAddress");
            broadcastConsumer = session.createConsumer(broadcastDestination);
            logger.info("Broadcast consumer created");
        }
        catch (JMSException e)
        {
            logger.error("Failed to create consumer");
            throw e;
        }
        catch (NamingException e)
        {
            logger.error("Failed to prepare the destinations");
            throw e;
        }
    }

    public void consumeMessage() throws JMSException
    {
        super.consumeMessage(broadcastConsumer, false);
    }

    public static void main(String[] args) throws JMSException, NamingException
    {
        final BroadcastReceiverOptions options = new BroadcastReceiverOptions();
        try
        {
            options.parse(args);
            options.printReceivedOptions();
            try (final BroadcastReceiver broadcastReceiver = new BroadcastReceiver(options))
            {
                broadcastReceiver.printInfo();
                broadcastReceiver.checkCertificateStores();
                broadcastReceiver.checkConnection();
                broadcastReceiver.connect();
                for (int i = 0; i < options.getMessageCount(); i++)
                {
                    broadcastReceiver.consumeMessage();
                }
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
