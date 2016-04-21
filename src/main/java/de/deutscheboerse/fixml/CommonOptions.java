/******************************************************************************** 
 *
 * DESCRIPTION:  FIXML Connection Test Tool - tool for receiving and sending AMQP
 *                                            messages via SSL broker interface
 *
 ********************************************************************************
 */

package de.deutscheboerse.fixml;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.LoggerFactory;

public abstract class CommonOptions
{
    protected static final String DEFAULT_LOGLEVEL = "info";
    protected static final String HOST = "host";
    protected static final String PORT = "port";
    protected static final String ACCOUNT = "account";
    protected static final String TRUSTSTORE = "truststore";
    protected static final String TRUSTSTOREPASS = "truststore-password";
    protected static final String KEYSTORE = "keystore";
    protected static final String KEYSTOREPASS = "keystore-password";
    protected static final String KEYALIAS = "key-alias";
    protected static final String VERIFYHOST = "verify-hostname";
    protected static final String SSLDEBUG = "ssl-debug";
    protected static final String LOGLEVEL = "log-level";
    protected static final String RECVTIMEOUT = "timeout";
    protected static final String AMQPVERSION = "amqp-version";
    
    protected Options cliOptions;
    protected CommandLineParser parser;
    protected CommandLine line;
    
    public static enum AmqpVersion
    {
        AMQP_0_10, AMQP_1_0;
        
        public static AmqpVersion fromString(String amqpVersion) throws ParseException
        {
            if (amqpVersion.equals("0-10"))
            {
                return AMQP_0_10;
            }
            else if (amqpVersion.equals("1.0"))
            {
                return AMQP_1_0;
            }
            else
            {
                throw new ParseException("Unknown AMQP version '" + amqpVersion + "'");
            }
        }
        
        @Override
        public String toString()
        {
            if (this == AMQP_0_10)
            {
                return "0-10";
            }
            else if (this == AMQP_1_0)
            {
                return "1.0";
            }
            else
            {
                return "n/a";
            }
        }
    }
    
    public int timeout = 1000;
    public String hostname = "127.0.0.1";
    public int port = 5671;
    public String accountID = "ABCFR_ABCFRALMMACC1";
    public String privateKeyAlias;
    public boolean verifyHostname = false;
    public AmqpVersion amqpVersion = AmqpVersion.AMQP_0_10;

    private String logLevel = DEFAULT_LOGLEVEL;

    public CommonOptions()
    {
        this.cliOptions = new Options();
        Option opt;

        // AMQP host
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(true);
        OptionBuilder.withLongOpt(HOST);
        OptionBuilder.withArgName("host name/IP addr");
        OptionBuilder.withDescription("AMQP broker hostname or IP address (mandatory)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);

        // AMQP port
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(true);
        OptionBuilder.withLongOpt(PORT);
        OptionBuilder.withArgName("port number");
        OptionBuilder.withDescription("AMQP broker port number (mandatory)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);

        // account
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(true);
        OptionBuilder.withLongOpt(ACCOUNT);
        OptionBuilder.withArgName("account name");
        OptionBuilder.withDescription("Member account ID (mandatory)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);

        // trust store
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(true);
        OptionBuilder.withLongOpt(TRUSTSTORE);
        OptionBuilder.withArgName("store file");
        OptionBuilder.withDescription("JKS store file with AMQP broker public keys(s) (mandatory)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);

        // trust store password
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(true);
        OptionBuilder.withLongOpt(TRUSTSTOREPASS);
        OptionBuilder.withArgName("password");
        OptionBuilder.withDescription("Password protecting the truststore (mandatory)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);

        // client private key store
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(true);
        OptionBuilder.withLongOpt(KEYSTORE);
        OptionBuilder.withArgName("store file");
        OptionBuilder.withDescription("JKS store file with member account private key(s) (mandatory)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);

        // trust store password
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(true);
        OptionBuilder.withLongOpt(KEYSTOREPASS);
        OptionBuilder.withArgName("password");
        OptionBuilder.withDescription("Password protecting the keystore (mandatory)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);

        // account private key alias
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(false);
        OptionBuilder.withLongOpt(KEYALIAS);
        OptionBuilder.withArgName("alias");
        OptionBuilder.withDescription("Alias of the private key to be used (default: same as account name in lower-case)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);

        // verify remote host identity?
        OptionBuilder.hasArg(false);
        OptionBuilder.isRequired(false);
        OptionBuilder.withLongOpt(VERIFYHOST);
        OptionBuilder.withDescription("Verify remote host identity (default: " + (this.verifyHostname ? "on)" : "off)"));
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);

        // SSL debugging?
        OptionBuilder.hasArg(false);
        OptionBuilder.isRequired(false);
        OptionBuilder.withLongOpt(SSLDEBUG);
        OptionBuilder.withDescription("Detailed SSL logging (default: off)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);

        // account private key alias
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(false);
        OptionBuilder.withLongOpt(LOGLEVEL);
        OptionBuilder.withArgName("level");
        OptionBuilder.withDescription("Logging level (default: INFO; other possibilities: ERROR, WARNING, DEBUG, TRACE)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);

        // message receive time-out
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(false);
        OptionBuilder.withLongOpt(RECVTIMEOUT);
        OptionBuilder.withArgName("time-out in ms");
        OptionBuilder.withDescription("How long to wait for a message (default: " + this.timeout + ")");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);
        
        // amqp version
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(false);
        OptionBuilder.withLongOpt(AMQPVERSION);
        OptionBuilder.withArgName("AMQP protocol version");
        OptionBuilder.withDescription("AMQP protocol version (default: " + this.amqpVersion + "; other possibilities: " + AmqpVersion.AMQP_1_0 + ")");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);
    }
    
    public void printReceivedOptions()
    {
        final StringBuffer buffer = new StringBuffer();
        for (Option option : this.line.getOptions())
        {
            buffer.append(" --" + option.getLongOpt());
            buffer.append(option.hasArg() ? "=" + option.getValue() : "");
        }
        if (!buffer.toString().contains("--amqp-version="))
        {
            buffer.append(" --amqp-version=" + amqpVersion);
        }
        LoggerFactory.getLogger(CommonOptions.class).info("Received options:" + buffer);
    }
    
    public void parse(final CommandLineParser parser, String[] args) throws ParseException
    {
        this.parser = parser;
        this.line = parser.parse(cliOptions, args, false);
        if (this.line.hasOption(LOGLEVEL))
        {
            final String custLevel = line.getOptionValue(LOGLEVEL).toLowerCase();
            final String[] validLevels = new String[] {"error", "warn", "debug", "trace", "info"};
            if (Arrays.asList(validLevels).contains(custLevel))
            {
                this.logLevel = custLevel;
            }
        }
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", this.logLevel);
        System.setProperty("org.slf4j.simpleLogger.log.org.apache.qpid", "error");
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "yyyy-MM-dd HH:mm:ss Z");

        if (line.hasOption(RECVTIMEOUT))
        {
            this.timeout = Integer.parseInt(line.getOptionValue(RECVTIMEOUT));
        }

        if (line.hasOption(PORT))
        {
            this.port = Integer.parseInt(line.getOptionValue(PORT));
        }

        if (line.hasOption(HOST))
        {
            this.hostname = line.getOptionValue(HOST);
        }

        if (line.hasOption(ACCOUNT))
        {
            this.accountID = line.getOptionValue(ACCOUNT);
        }

        if (line.hasOption(KEYALIAS))
        {
            this.privateKeyAlias = line.getOptionValue(KEYALIAS);
        }
        else
        {
            this.privateKeyAlias = this.accountID.toLowerCase();
        }

        if (line.hasOption(VERIFYHOST))
        {
            this.verifyHostname = true;
        }

        if (line.hasOption(SSLDEBUG))
        {
            System.setProperty("javax.net.debug", "ssl");
        }

        if (line.hasOption(TRUSTSTORE))
        {
            System.setProperty("javax.net.ssl.trustStore", line.getOptionValue(TRUSTSTORE));
        }

        if (line.hasOption(TRUSTSTOREPASS))
        {
            System.setProperty("javax.net.ssl.trustStorePassword", line.getOptionValue(TRUSTSTOREPASS));
        }

        if (line.hasOption(KEYSTORE))
        {
            System.setProperty("javax.net.ssl.keyStore", line.getOptionValue(KEYSTORE));
        }

        if (line.hasOption(KEYSTOREPASS))
        {
            System.setProperty("javax.net.ssl.keyStorePassword", line.getOptionValue(KEYSTOREPASS));
        }
        
        if (line.hasOption(AMQPVERSION))
        {
            this.amqpVersion = AmqpVersion.fromString(line.getOptionValue(AMQPVERSION));
        }
    }
    
    public void printHelp()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("~this~ <param1> [param2] ...", this.cliOptions);
    }
}
