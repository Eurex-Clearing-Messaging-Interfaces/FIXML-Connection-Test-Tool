/********************************************************************************
 *
 * DESCRIPTION:  FIXML Connection Test Tool - tool for receiving and sending AMQP
 *                                            messages via SSL broker interface
 *
 ********************************************************************************
 */
package de.deutscheboerse.fixml;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public abstract class CommonOptions
{
    protected static final String DEFAULT_LOG_LEVEL = "info";
    protected static final String HOST = "host";
    protected static final String PORT = "port";
    protected static final String ACCOUNT = "account";
    protected static final String TRUSTSTORE = "truststore";
    protected static final String TRUSTSTORE_PASS = "truststore-password";
    protected static final String KEYSTORE = "keystore";
    protected static final String KEYSTORE_PASS = "keystore-password";
    protected static final String KEY_ALIAS = "key-alias";
    protected static final String VERIFY_HOSTNAME = "verify-hostname";
    protected static final String SSL_DEBUG = "ssl-debug";
    protected static final String LOG_LEVEL = "log-level";
    protected static final String TIMEOUT = "timeout";
    protected static final String CONNECTION_CHECK_TIMEOUT = "connection-check-timeout";
    protected static final String MESSAGE_COUNT = "message-count";

    protected final Options options = new Options();
    protected final CommandLineParser parser = new DefaultParser();
    protected CommandLine line;

    public int timeout = 1000;
    public int connectionCheckTimeout = 10000;
    public int messageCount = 1;
    public String hostname = "127.0.0.1";
    public int port = 5671;
    public String accountID = "ABCFR_ABCFRALMMACC1";
    public String privateKeyAlias;
    public boolean verifyHostname = false;

    private String logLevel = DEFAULT_LOG_LEVEL;

    public CommonOptions()
    {
        addMandatoryOption(HOST, "AMQP broker hostname or IP address", "host name/IP addr");
        addMandatoryOption(PORT, "AMQP broker port number", "port number");
        addMandatoryOption(ACCOUNT, "Member account ID", "account name");
        addMandatoryOption(TRUSTSTORE, "JKS store file with AMQP broker public keys(s)", "store file");
        addMandatoryOption(TRUSTSTORE_PASS, "Password protecting the truststore", "password");
        addMandatoryOption(KEYSTORE, "JKS store file with member account private key(s)", "store file");
        addMandatoryOption(KEYSTORE_PASS, "Password protecting the keystore", "password");
        addOption(KEY_ALIAS, "Alias of the private key to be used (default: same as account name in lower-case)", "alias");
        addOption(VERIFY_HOSTNAME, "Verify remote host identity (default: " + (verifyHostname ? "on)" : "off)"));
        addOption(SSL_DEBUG, "Detailed SSL logging (default: off)");
        addOption(LOG_LEVEL, "Logging level (default: INFO; other possibilities: ERROR, WARN, DEBUG, TRACE)", "level");
        addOption(TIMEOUT, "How long to wait for a message (default: " + timeout + " ms)", "time-out in ms");
        addOption(CONNECTION_CHECK_TIMEOUT, "How long to wait for a connection check (default: " + connectionCheckTimeout + " ms)", "time-out in ms");
        addOption(MESSAGE_COUNT, "How many messages will be processed (default: " + messageCount + ")", "message count");
    }

    public void printReceivedOptions()
    {
        final StringBuilder sb = new StringBuilder();
        for (final Option option : line.getOptions())
        {
            sb.append(" --").append(option.getLongOpt());
            sb.append(option.hasArg() ? "=" + option.getValue() : "");
        }
        LoggerFactory.getLogger(CommonOptions.class).info("Received options:" + sb);
    }

    public void parse(String[] args) throws ParseException
    {
        line = parser.parse(options, args, false);
        if (line.hasOption(LOG_LEVEL))
        {
            final String logLevel = line.getOptionValue(LOG_LEVEL).toLowerCase();
            final String[] validLevels = new String[] { "error", "warn", "debug", "trace", "info" };
            if (Arrays.asList(validLevels).contains(logLevel))
            {
                this.logLevel = logLevel;
            }
        }
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", this.logLevel);
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.qpid", "error");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss Z");

        if (line.hasOption(TIMEOUT))
        {
            timeout = Integer.parseInt(line.getOptionValue(TIMEOUT));
        }

        if (line.hasOption(CONNECTION_CHECK_TIMEOUT))
        {
            connectionCheckTimeout = Integer.parseInt(line.getOptionValue(CONNECTION_CHECK_TIMEOUT));
        }

        if (line.hasOption(PORT))
        {
            port = Integer.parseInt(line.getOptionValue(PORT));
        }

        if (line.hasOption(HOST))
        {
            hostname = line.getOptionValue(HOST);
        }

        if (line.hasOption(ACCOUNT))
        {
            accountID = line.getOptionValue(ACCOUNT);
        }

        if (line.hasOption(KEY_ALIAS))
        {
            privateKeyAlias = line.getOptionValue(KEY_ALIAS);
        }
        else
        {
            privateKeyAlias = accountID.toLowerCase();
        }

        if (line.hasOption(VERIFY_HOSTNAME))
        {
            verifyHostname = true;
        }

        if (line.hasOption(SSL_DEBUG))
        {
            System.setProperty("javax.net.debug", "ssl");
        }

        if (line.hasOption(TRUSTSTORE))
        {
            final String truststore = line.getOptionValue(TRUSTSTORE);
            if (!Files.exists(Paths.get(truststore)))
            {
                throw new IllegalArgumentException("Truststore path " + truststore + " does not exist.");
            }
            System.setProperty("javax.net.ssl.trustStore", truststore);
        }

        if (line.hasOption(TRUSTSTORE_PASS))
        {
            System.setProperty("javax.net.ssl.trustStorePassword", line.getOptionValue(TRUSTSTORE_PASS));
        }

        if (line.hasOption(KEYSTORE))
        {
            final String keystore = line.getOptionValue(KEYSTORE);
            if (!Files.exists(Paths.get(keystore)))
            {
                throw new IllegalArgumentException("Keystore path " + keystore + " does not exist.");
            }
            System.setProperty("javax.net.ssl.keyStore", keystore);
        }

        if (line.hasOption(KEYSTORE_PASS))
        {
            System.setProperty("javax.net.ssl.keyStorePassword", line.getOptionValue(KEYSTORE_PASS));
        }

        if (line.hasOption(MESSAGE_COUNT))
        {
            messageCount = Integer.parseInt(line.getOptionValue(MESSAGE_COUNT));
        }
    }

    public void printHelp()
    {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(180);
        formatter.printHelp("~this~ <param1> [param2] ...", options);
    }

    protected void addOption(String name, String description)
    {
        options.addOption(Option.builder()
                .longOpt(name)
                .desc(description)
                .build());
    }

    protected void addOption(String name, String description, String argument)
    {
        options.addOption(Option.builder()
                .longOpt(name)
                .desc(description)
                .hasArg()
                .argName(argument)
                .build());
    }

    protected void addMandatoryOption(String name, String description, String argument)
    {
        options.addOption(Option.builder()
                .longOpt(name)
                .desc(description + " (mandatory)")
                .hasArg()
                .argName(argument)
                .required(true)
                .build());
    }
}
