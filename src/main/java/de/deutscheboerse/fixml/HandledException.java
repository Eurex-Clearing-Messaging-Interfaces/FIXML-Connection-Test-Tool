/******************************************************************************** 
 *
 * DESCRIPTION:  FIXML Connection Test Tool - tool for receiving and sending AMQP
 *                                            messages via SSL broker interface
 *
 ********************************************************************************
 */

package de.deutscheboerse.fixml;

public class HandledException extends Exception
{

    private static final long serialVersionUID = 1L;
    
    public HandledException(String string)
    {
        super(string);
    }

}
