package pt.upa.broker;


import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.net.SocketTimeoutException;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.MessageContext;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPort;
import pt.upa.ws.handler.AuthenticationHandler;

import java.util.Map;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;

public class EndpointManager {
	private String uddiURL;
	private UDDINaming uddiNaming;
	private String name;
	private String url;
	private Endpoint endpoint;
	private boolean isPrimary;

	public EndpointManager(String _uddiURL, String _name, String _url, boolean _isPrimary) {
		uddiURL = _uddiURL;
		name = _name;
		url = _url;
		isPrimary = _isPrimary;
	}
	
	public void start() throws JAXRException {
		uddiNaming = new UDDINaming(uddiURL);
		if (isPrimary) {
			BrokerService service = new BrokerService();
			BrokerPortType backBroker = service.getBrokerPort();
			BindingProvider bindingProvider = (BindingProvider) backBroker;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8091/broker-ws/endpoint");

			BrokerPort port = new BrokerPort(uddiURL, true, backBroker);
			endpoint = Endpoint.create(port);
			endpoint.publish(url);

			uddiNaming.rebind(name, url);
		} else {
			BrokerPort port = new BrokerPort(uddiURL, false);
			endpoint = Endpoint.create(port);
			endpoint.publish(url);

			BrokerService service = new BrokerService();
			BrokerPortType primBroker = service.getBrokerPort();
			BindingProvider bindingProvider = (BindingProvider) primBroker;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8090/broker-ws/endpoint");
			this.setTimeouts(requestContext, 1000, 3000);
			this.observePrimaryBroker(primBroker);

			System.out.println("Replcace URL of broker server");
			port.setPrimary(true);
			uddiNaming.rebind(name, url);
		}
	}


	private void observePrimaryBroker(BrokerPortType primBroker) {
		try {
			boolean primaryIsAlive = true;
			while (primaryIsAlive) {
				primaryIsAlive = primBroker.alive();
				System.out.println("Primary broker server is alive!");
				Thread.sleep(5000);
			}
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		} catch (WebServiceException e) {
			System.out.println("Primary broker server died!");
		}
	}

	private void setTimeouts(Map<String, Object> requestContext, int connectTimeout, int responseTimeout ) {
		requestContext.put("com.sun.xml.ws.connect.timeout", connectTimeout);
		requestContext.put("com.sun.xml.internal.ws.connect.timeout", connectTimeout);
		requestContext.put("javax.xml.ws.client.connectionTimeout", connectTimeout);

		requestContext.put("com.sun.xml.ws.request.timeout", responseTimeout);
		requestContext.put("com.sun.xml.internal.ws.request.timeout", responseTimeout);
		requestContext.put("javax.xml.ws.client.receiveTimeout", responseTimeout);
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

