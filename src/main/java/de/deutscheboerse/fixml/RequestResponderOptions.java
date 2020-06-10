/********************************************************************************
 *
 * DESCRIPTION:  FIXML Connection Test Tool - tool for receiving and sending AMQP
 *                                            messages via SSL broker interface
 *
 ********************************************************************************
 */
package de.deutscheboerse.fixml;

import org.apache.commons.cli.CommandLineParser;
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
        addOption(MSG_CONTENT, "Content of the message to be sent. Default: '" + msgContent + "'", "message content");
        addOption(MSG_CONTENT_FILE, "File name the content of the message should be read from. To read from standard input use STDIN name.", "message content file");
    }

    public void parse(final CommandLineParser parser, String[] args) throws ParseException
    {
        super.parse(parser, args);
        if (line.hasOption(MSG_CONTENT))
        {
            msgContent = line.getOptionValue(MSG_CONTENT);
        }
        if (line.hasOption(MSG_CONTENT_FILE))
        {
            msgContentFileName = line.getOptionValue(MSG_CONTENT_FILE);
        }
    }
}
