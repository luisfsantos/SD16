package pt.upa.ca.ws.cli;


import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.security.KeyStoreException;
import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.ca.ws.CAPortType;
import pt.upa.ca.ws.CAService;
import pt.upa.ca.ws.UnavailableCertificateException_Exception;

public class CAClient implements CAPortType {
	private CAPortType port;
	
	public CAClient(String endpointAddress) {
		CAService service = new CAService();
		port = service.getCAPort();
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
	}
	
	public CAClient(String uddiURL, String companyName) throws JAXRException {
		UDDINaming uddiNaming = new UDDINaming(uddiURL);
		String endpointAddress = uddiNaming.lookup(companyName);
		
		CAService service = new CAService();
		port = service.getCAPort();
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
	}
	
	@Override
	public String ping(String name) {return port.ping(name);}

	@Override
	public byte [] requestCertificate(String name) throws UnavailableCertificateException_Exception {
		return port.requestCertificate(name);
	}


}
