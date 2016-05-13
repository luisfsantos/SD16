package pt.upa.ws.handler;


import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Set;

public class MaliciousHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public Set<QName> getHeaders() {
		return null;
	}

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {

		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		String operationName = null;
		if (outboundElement){ //for requests only
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
			try {
				operationName = body.getChildNodes().item(0).getLocalName();
				System.out.print(operationName);
			} catch (Exception e ) {
				
			}
			
		}

		try {
			if (outboundElement && operationName.equals("requestJob")) {
				return handleInBound(smc);
			}
		} catch (Exception e) {
			System.out.print("Caughtexception in handleMessage: " + e);
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

	public boolean handleInBound(SOAPMessageContext smc) throws SOAPException {
		SOAPEnvelope msg = smc.getMessage().getSOAPPart().getEnvelope();

		Name jobPrice = msg.createName("price");
		SOAPBody body = msg.getBody();

		//if(body.getChildNodes().item(0).getChildNodes().item(2)) {
		SOAPElement priceEle = (SOAPElement) body.getChildNodes().item(0).getChildNodes().item(2);//(SOAPElement) body.getChildElements(jobPrice).next();
		if (priceEle.getValue().equals("66")) {
			priceEle.removeContents();
			priceEle.addTextNode("666");
		}

		return true;
	}

}
