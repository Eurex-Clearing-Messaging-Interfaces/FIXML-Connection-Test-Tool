/********************************************************************************
 *
 * DESCRIPTION:  FIXML Connection Test Tool - tool for receiving and sending AMQP
 *                                            messages via SSL broker interface
 *
 ********************************************************************************
 */
package de.deutscheboerse.fixml;

import org.apache.commons.cli.ParseException;

public class BroadcastReceiverOptions extends CommonOptions
{
    protected static final String STREAM = "stream";

    private String streamID = "TradeConfirmation";

    BroadcastReceiverOptions()
    {
        super();
        addOption(STREAM, "Broadcast stream to read from (default: " + getStreamID() + ")", "stream name");
    }

    public void parse(String[] args) throws ParseException
    {
        super.parse(args);
        if (line.hasOption(STREAM))
        {
            setStreamID(line.getOptionValue(STREAM));
        }
    }

    public String getStreamID()
    {
        return streamID;
    }

    public void setStreamID(String streamID)
    {
        this.streamID = streamID;
    }
}
