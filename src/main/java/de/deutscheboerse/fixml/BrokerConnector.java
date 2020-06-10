/********************************************************************************
 *
 * DESCRIPTION:  FIXML Connection Test Tool - tool for receiving and sending AMQP
 *                                            messages via SSL broker interface
 *
 ********************************************************************************
 */
package de.deutscheboerse.fixml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Properties;

public abstract class BrokerConnector implements Closeable
{
    protected enum StoreType
    {keystore, truststore}

    protected final Properties properties;
    protected InitialContext context;
    protected Logger logger;
    protected final CommonOptions options;
    protected Connection connection;
    protected Session session;

    public BrokerConnector(final CommonOptions options)
    {
        // create logger
        logger = LoggerFactory.getLogger(BrokerConnector.class);
        this.options = options;
        properties = new Properties();
        properties.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        String brokerConnStr = String.format("amqps://%s:%d?transport.keyStoreLocation=%s&transport.keyStorePassword=%s&" +
                        "transport.trustStoreLocation=%s&transport.trustStorePassword=%s&transport.keyAlias=%s&transport.verifyHost=%s",
                options.hostname, options.port, System.getProperty("javax.net.ssl.keyStore"),
                System.getProperty("javax.net.ssl.keyStorePassword"), System.getProperty("javax.net.ssl.trustStore"),
                System.getProperty("javax.net.ssl.trustStorePassword"), options.privateKeyAlias,
                options.verifyHostname);
        properties.setProperty("connectionfactory.connection", brokerConnStr);
    }

    protected void checkCertStores() throws HandledException
    {
        logger.info("Checking truststore and keystore.");
        logger.info("Checking truststore:");
        checkCertStore(System.getProperty("javax.net.ssl.trustStore"), System.getProperty("javax.net.ssl.trustStorePassword"), StoreType.truststore);
        logger.info("Truststore check passed.");
        logger.info("Checking truststore:");
        checkCertStore(System.getProperty("javax.net.ssl.keyStore"), System.getProperty("javax.net.ssl.keyStorePassword"), StoreType.keystore);
        logger.info("Keystore check passed.");
        logger.info("Truststore and keystore check passed.");
    }

    protected void checkCertStore(String storePath, String storePassword, StoreType storeType) throws HandledException
    {
        try (InputStream inputStream = new FileInputStream(storePath))
        {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, storePassword.toCharArray());
            if (storeType == StoreType.keystore)
            {
                if (!keyStore.isKeyEntry(options.privateKeyAlias))
                {
                    throw new HandledException("Alias '" + options.privateKeyAlias + "' missing in store '" + storePath + "'");
                }
            }
            logger.info("Printing out " + storeType + " file '" + storePath + "':");
            boolean checkHostnames = (storeType == StoreType.truststore) && options.verifyHostname;
            boolean hostnameFoundInCertificate = false;
            // print out store certificates
            Enumeration<String> enumeration = keyStore.aliases();
            while (enumeration.hasMoreElements())
            {
                String alias = enumeration.nextElement();
                if (checkHostnames && alias.equals(options.hostname))
                {
                    hostnameFoundInCertificate = true;
                }
                System.out.println("Alias name: " + alias);
                Certificate certificate = keyStore.getCertificate(alias);
                System.out.println(certificate.toString());
            }
            if (checkHostnames && !hostnameFoundInCertificate)
            {
                throw new HandledException("Hostname '" + options.hostname + "' missing in store '" + storePath + "'");
            }
        }
        catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException e)
        {
            throw new HandledException("Error during checking '" + storePath + "', exception: " + e);
        }
    }

    protected void connect() throws JMSException, HandledException, NamingException
    {
        ConnectionFactory connectionFactory;
        try
        {
            context = new InitialContext(properties);
            connectionFactory = (ConnectionFactory) context.lookup("connection");
            try
            {
                connection = connectionFactory.createConnection();
                session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                connection.start();
                logger.info("Connected");
            }
            catch (JMSException e)
            {
                if ("Error creating connection: Connection refused".equals(e.getMessage()))
                {
                    throw new HandledException("Broker is not running on '" + options.hostname + "' port '" + options.port + "'");
                }
                logger.error("Failed to connect or create a session");
                throw e;
            }
        }
        catch (NamingException e)
        {
            logger.error("Failed to prepare the connection factory");
            throw e;
        }
    }

    protected void consumeMessage(final MessageConsumer messageConsumer, boolean errorOnNoMsg) throws JMSException
    {
        try
        {
            Message msg = messageConsumer.receive(options.timeout);

            if (msg == null)
            {
                if (errorOnNoMsg)
                {
                    logger.error("No message received");
                }
                else
                {
                    logger.info("No message received");
                }

            }
            else if (msg instanceof TextMessage)
            {
                TextMessage textMessage = (TextMessage) msg;
                String messageBody = textMessage.getText();
                msg.acknowledge();
                logger.info("Text message received, length = " + messageBody.length() + ", content:\n" + messageBody);
            }
            else if (msg instanceof BytesMessage)
            {
                // convert byte buffer to a String first
                BytesMessage bytesMessage = (BytesMessage) msg;
                StringBuilder builder = new StringBuilder();

                for (int i = 0; i < bytesMessage.getBodyLength(); i++)
                {
                    builder.append((char) bytesMessage.readByte());
                }

                msg.acknowledge();
                logger.info("Byte message received, length = " + bytesMessage.getBodyLength() + ", content:\n" + builder.toString());
            }
            else
            {
                logger.error("Message of unexpected type received");
                msg.acknowledge();
            }
        }
        catch (JMSException e)
        {
            logger.error("Failed to receive a message");
            throw e;
        }
    }

    public void close()
    {
        try
        {
            if (connection != null)
            {
                connection.close();
                logger.info("Disconnected");
            }
        }
        catch (JMSException e)
        {
            logger.error("Failed to disconnect properly", e);
            throw new RuntimeException("Failed to disconnect properly", e);
        }
    }
}
