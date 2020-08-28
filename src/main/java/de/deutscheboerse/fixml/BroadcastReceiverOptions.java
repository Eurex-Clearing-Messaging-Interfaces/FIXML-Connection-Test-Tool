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

    public String streamID = "TradeConfirmation";

    BroadcastReceiverOptions()
    {
        super();
        addOption(STREAM, "Broadcast stream to read from (default: " + streamID + ")", "stream name");
    }

    public void parse(String[] args) throws ParseException
    {
        super.parse(args);
        if (line.hasOption(STREAM))
        {
            streamID = line.getOptionValue(STREAM);
        }
    }
}
