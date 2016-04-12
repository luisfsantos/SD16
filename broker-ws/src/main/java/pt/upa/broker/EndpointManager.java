package pt.upa.broker;


import javax.xml.registry.JAXRException;
import javax.xml.ws.Endpoint;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPort;

public class EndpointManager {
	private String uddiURL;
	private UDDINaming uddiNaming;
	private String name;
	private String url;
	private Endpoint endpoint;

	public EndpointManager(String _uddiURL, String _name, String _url) {
		uddiURL = _uddiURL;
		name = _name;
		url = _url;
	}
	
	public void start() throws JAXRException {
		BrokerPort port = new BrokerPort(uddiURL);
		endpoint = Endpoint.create(port);
		endpoint.publish(url);
		uddiNaming = new UDDINaming(uddiURL);
		uddiNaming.rebind(name, url);
	}
	
	public void stop() throws JAXRException {
		if (endpoint != null) {
			// stop endpoint
			endpoint.stop();
		}
		if (uddiNaming != null) {
			// delete from UDDI
			uddiNaming.unbind(name);
		}
	}

}

