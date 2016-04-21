/******************************************************************************** 
 *
 * DESCRIPTION:  FIXML Connection Test Tool - tool for receiving and sending AMQP
 *                                            messages via SSL broker interface
 *
 ********************************************************************************
 */

package de.deutscheboerse.fixml;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.ParseException;

public class RequestResponderOptions extends CommonOptions
{
    protected static final String MSG_CONTENT = "msg-content";
    protected static final String MSG_CONTENT_FILE = "msg-content-file";

    public String msgContent = "FIXML Connection Test Tool testing message.";
    public String msgContentFileName;
    
    public RequestResponderOptions()
    {
        super();
        Option opt;

        // message content directly provided
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(false);
        OptionBuilder.withLongOpt(MSG_CONTENT);
        OptionBuilder.withArgName("message content");
        OptionBuilder.withDescription("Content of the message to be sent. Default: '" + this.msgContent + "'");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);
        
        // message content provided through filename
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(false);
        OptionBuilder.withLongOpt(MSG_CONTENT_FILE);
        OptionBuilder.withArgName("message content file");
        OptionBuilder.withDescription("File name the content of the message should be read from. To read from standard input use STDIN name.");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);
    }
    
    public void parse(final CommandLineParser parser, String[] args) throws ParseException
    {
        super.parse(parser, args);
        if (this.line.hasOption(MSG_CONTENT))
        {
            this.msgContent = line.getOptionValue(MSG_CONTENT);
        }
        if (this.line.hasOption(MSG_CONTENT_FILE))
        {
            this.msgContentFileName = line.getOptionValue(MSG_CONTENT_FILE);
        }
    }
}
