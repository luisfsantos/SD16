package pt.upa.ws.handler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

/**
 * This SOAPHandler outputs the contents of inbound and outbound messages.
 */
public class LoggingHandler implements SOAPHandler<SOAPMessageContext> {

    public Set<QName> getHeaders() {
        return null;
    }

    public boolean handleMessage(SOAPMessageContext smc) {

    	PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("log.txt", true));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (out != null) {
			out.println("\n");
	        out.println("------------------------------");
	        out.println("LOGGING MESSAGE: \n");
	        logToSystemOut(smc, out);
	        out.println("\nFINISHED LOGGING!! \n");
	        out.println("------------------------------");
	        out.println("\n");
		}    
        return true;
    }

    public boolean handleFault(SOAPMessageContext smc) {
    	PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("log.txt", true));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (out != null) {
			 logToSystemOut(smc, out);
		}
       
        return true;
    }

    // nothing to clean up
    public void close(MessageContext messageContext) {
    }

    /**
     * Check the MESSAGE_OUTBOUND_PROPERTY in the context to see if this is an
     * outgoing or incoming message. Write a brief message to the print stream
     * and output the message. The writeTo() method can throw SOAPException or
     * IOException
     */
    private void logToSystemOut(SOAPMessageContext smc, PrintStream out) {
        Boolean outbound = (Boolean) smc
                .get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (outbound) {
            out.println("Outbound SOAP message:");
        } else {
            out.println("Inbound SOAP message:");
        }

        SOAPMessage message = smc.getMessage();
        try {
            message.writeTo(out);
            out.println(); // just to add a newline to output
        } catch (Exception e) {
            out.printf("Exception in handler: %s%n", e);
        }
    }

}
