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

public class BroadcastReceiverOptions extends CommonOptions
{
    protected static final String STREAM = "stream";
    protected static final String CONSUME = "consume";

    public String streamID = "TradeConfirmation";
    public boolean consume = false;

    BroadcastReceiverOptions()
    {
        super();
        Option opt;

        // stream name
        OptionBuilder.hasArg(true);
        OptionBuilder.isRequired(false);
        OptionBuilder.withLongOpt(STREAM);
        OptionBuilder.withArgName("stream name");
        OptionBuilder.withDescription("Broadcast stream to read from (default: TradeConfirmation)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);
        
        // consume instead of browse?
        OptionBuilder.hasArg(false);
        OptionBuilder.isRequired(false);
        OptionBuilder.withLongOpt(CONSUME);
        OptionBuilder.withDescription("Consume the message instead of just peeking at it (default: off)");
        opt = OptionBuilder.create();
        this.cliOptions.addOption(opt);
    }
    
    public void parse(final CommandLineParser parser, String[] args) throws ParseException
    {
        super.parse(parser, args);
        if (this.line.hasOption(STREAM))
        {
            this.streamID = line.getOptionValue(STREAM);
        }
        if (this.line.hasOption(CONSUME))
        {
            this.consume = true;
        }
    }
}
