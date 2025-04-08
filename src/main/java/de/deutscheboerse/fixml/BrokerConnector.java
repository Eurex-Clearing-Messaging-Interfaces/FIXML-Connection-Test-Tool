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

import jakarta.jms.BytesMessage;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;
import java.util.Properties;

public abstract class BrokerConnector implements Closeable
{
    protected static final Charset ENCODING = StandardCharsets.UTF_8;
    protected final Properties properties = new Properties();
    private final String hostname;
    private final int port;
    private final int connectionCheckTimeout;
    private final String privateKeyAlias;
    private final int timeout;
    protected InitialContext context;
    protected Logger logger;
    protected Connection connection;
    protected Session session;
    public BrokerConnector(final CommonOptions options)
    {
        // create logger
        logger = LoggerFactory.getLogger(BrokerConnector.class);
        hostname = options.getHostname();
        port = options.getPort();
        connectionCheckTimeout = options.getConnectionCheckTimeout();
        privateKeyAlias = options.getPrivateKeyAlias();
        timeout = options.getTimeout();

        properties.setProperty("java.naming.factory.initial", "org.apache.qpid.jms.jndi.JmsInitialContextFactory");
        final String brokerConnectionString = String.format("amqps://%s:%d?transport.keyStoreLocation=%s&transport.keyStorePassword=%s&" +
                        "transport.trustStoreLocation=%s&transport.trustStorePassword=%s&transport.keyAlias=%s&transport.verifyHost=%s",
                hostname,
                port,
                System.getProperty("javax.net.ssl.keyStore"),
                URLEncoder.encode(System.getProperty("javax.net.ssl.keyStorePassword"), ENCODING),
                System.getProperty("javax.net.ssl.trustStore"),
                URLEncoder.encode(System.getProperty("javax.net.ssl.trustStorePassword"), ENCODING),
                privateKeyAlias,
                options.isVerifyHostname());
        properties.setProperty("connectionfactory.connection", brokerConnectionString);
    }

    protected void printInfo()
    {
        logger.info("Printing FIXML Connection Test Tool information:"
                + "\n\nVersion: " + ApplicationInfo.getVersion()
                + "\nBuild timestamp: " + ApplicationInfo.getTimestamp()
                + "\nSupported TLS versions: " + ApplicationInfo.getSupportedTlsVersions()
                + "\nHost/IP: " + ApplicationInfo.getAddress()
                + "\nList of dependencies: " + ApplicationInfo.getDependencies()
                + "\n");
    }

    protected void checkConnection() throws HandledException
    {
        logger.info("Checking connection " + hostname + ":" + port + " (timeout limit = " + connectionCheckTimeout + " ms).");
        try (Socket socket = new Socket())
        {
            socket.connect(new InetSocketAddress(hostname, port), connectionCheckTimeout);
        }
        catch (IOException e)
        {
            throw new HandledException("Connection check failed: " + e);
        }
        logger.info("Connection OK.");
    }

    protected void checkCertificateStores() throws HandledException
    {
        logger.info("Checking truststore and keystore.");
        logger.info("Checking truststore:");
        checkCertificateStore(System.getProperty("javax.net.ssl.trustStore"), System.getProperty("javax.net.ssl.trustStorePassword"), StoreType.truststore);
        logger.info("Truststore check passed.");
        logger.info("Checking truststore:");
        checkCertificateStore(System.getProperty("javax.net.ssl.keyStore"), System.getProperty("javax.net.ssl.keyStorePassword"), StoreType.keystore);
        logger.info("Keystore check passed.");
        logger.info("Truststore and keystore check passed.");
    }

    protected void checkCertificateStore(String storePath, String storePassword, StoreType storeType) throws HandledException
    {
        try (final InputStream inputStream = new FileInputStream(storePath))
        {
            final KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(inputStream, storePassword.toCharArray());
            if (storeType == StoreType.keystore)
            {
                if (!keyStore.isKeyEntry(privateKeyAlias))
                {
                    throw new HandledException("Alias '" + privateKeyAlias + "' missing in store '" + storePath + "'");
                }
            }
            logger.info("Printing out " + storeType + " file '" + storePath + "':");
            // print out store certificates
            final Enumeration<String> enumeration = keyStore.aliases();
            while (enumeration.hasMoreElements())
            {
                String alias = enumeration.nextElement();
                System.out.println("Alias name: " + alias);
                Certificate certificate = keyStore.getCertificate(alias);
                System.out.println(certificate.toString());
            }
        }
        catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException e)
        {
            throw new HandledException("Error during checking '" + storePath + "', exception: " + e);
        }
    }

    protected void connect() throws JMSException, HandledException, NamingException
    {
        try
        {
            context = new InitialContext(properties);
            final ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("connection");
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
                    throw new HandledException("Broker is not running on '" + hostname + "' port '" + port + "'");
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

    protected void consumeMessage(final MessageConsumer messageConsumer, boolean errorOnNoMessage) throws JMSException
    {
        try
        {
            final Message message = messageConsumer.receive(timeout);

            if (message == null)
            {
                if (errorOnNoMessage)
                {
                    logger.error("No message received");
                }
                else
                {
                    logger.info("No message received");
                }

            }
            else if (message instanceof TextMessage)
            {
                final TextMessage textMessage = (TextMessage) message;
                final String messageBody = textMessage.getText();
                message.acknowledge();
                logger.info("Text message received, length = " + messageBody.length() + ", content:\n" + messageBody);
            }
            else if (message instanceof BytesMessage)
            {
                // convert byte buffer to a String first
                final BytesMessage bytesMessage = (BytesMessage) message;
                final StringBuilder builder = new StringBuilder();

                for (int i = 0; i < bytesMessage.getBodyLength(); i++)
                {
                    builder.append((char) bytesMessage.readByte());
                }

                message.acknowledge();
                logger.info("Byte message received, length = " + bytesMessage.getBodyLength() + ", content:\n" + builder);
            }
            else
            {
                logger.error("Message of unexpected type received");
                message.acknowledge();
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

    protected enum StoreType
    {keystore, truststore}
}
