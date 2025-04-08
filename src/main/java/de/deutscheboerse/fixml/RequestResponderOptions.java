/********************************************************************************
 *
 * DESCRIPTION:  FIXML Connection Test Tool - tool for receiving and sending AMQP
 *                                            messages via SSL broker interface
 *
 ********************************************************************************
 */
package de.deutscheboerse.fixml;

import org.apache.commons.cli.ParseException;

public class RequestResponderOptions extends CommonOptions
{
    protected static final String MSG_CONTENT = "msg-content";
    protected static final String MSG_CONTENT_FILE = "msg-content-file";

    private String msgContent = "FIXML Connection Test Tool testing message.";
    private String msgContentFileName;

    public RequestResponderOptions()
    {
        super();
        addOption(MSG_CONTENT, "Content of the message to be sent. Default: '" + getMsgContent() + "'", "message content");
        addOption(MSG_CONTENT_FILE, "File name the content of the message should be read from. To read from standard input use STDIN name.", "message content file");
    }

    public void parse(String[] args) throws ParseException
    {
        super.parse(args);
        if (line.hasOption(MSG_CONTENT))
        {
            setMsgContent(line.getOptionValue(MSG_CONTENT));
        }
        if (line.hasOption(MSG_CONTENT_FILE))
        {
            setMsgContentFileName(line.getOptionValue(MSG_CONTENT_FILE));
        }
    }

    public String getMsgContent()
    {
        return msgContent;
    }

    public void setMsgContent(String msgContent)
    {
        this.msgContent = msgContent;
    }

    public String getMsgContentFileName()
    {
        return msgContentFileName;
    }

    public void setMsgContentFileName(String msgContentFileName)
    {
        this.msgContentFileName = msgContentFileName;
    }
}
