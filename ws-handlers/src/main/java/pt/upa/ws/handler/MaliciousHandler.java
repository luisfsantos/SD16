package pt.upa.ws.handler;


import javax.xml.bind.DatatypeConverter;
import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

public class MaliciousHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		Boolean outboundElement = (Boolean) smc
				.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		String operationName = null;
		if ((boolean) !outboundElement){ //for requests only
			SOAPEnvelope msg = null; //get the SOAP Message envelope
			try {
				msg = smc.getMessage().getSOAPPart().getEnvelope();
			} catch (SOAPException e) {
				e.printStackTrace();
			}
			SOAPBody body = null;
			try {
				body = msg.getBody();
			} catch (SOAPException e) {
				e.printStackTrace();
			}
			operationName = body.getChildNodes().item(1).getLocalName();
		}


		try {
			if (outboundElement.booleanValue() && operationName.equals("RequestJob")) { //FIXME
				return handleInBound(smc);
			}
		} catch (Exception e) {
			if (e instanceof RuntimeException) {
				throw new RuntimeException(e.getMessage());
			} else {
				System.out.print("Caughtexception in handleMessage: " + e);
			}
		}

		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		return false;
	}

	@Override
	public void close(MessageContext context) {

	}

	public boolean handleInBound(SOAPMessageContext smc) throws SOAPException { //FIXME
		SOAPEnvelope msg = null; //get the SOAP Message envelope
		try {
			msg = smc.getMessage().getSOAPPart().getEnvelope();
		} catch (SOAPException e) {
			e.printStackTrace();
		}

		SOAPBody body = null;
		try {
			body = msg.getBody();
		} catch (SOAPException e) {
			e.printStackTrace();
		}
		Name name = smc.getMessage().getSOAPPart().getEnvelope().createName("Ataque");
		body.addChildElement(name);
		return true;
	}

}
