package pt.upa.broker;


import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.net.SocketTimeoutException;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Endpoint;
import javax.xml.ws.WebServiceException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPort;
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
		if (isPrimary) {
			BrokerService service = new BrokerService();
			BrokerPortType backBroker = service.getBrokerPort();
			BindingProvider bindingProvider = (BindingProvider) backBroker;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8091/broker-ws/endpoint");
			
			BrokerPort port = new BrokerPort(uddiURL, true, backBroker);
			endpoint = Endpoint.create(port);
			endpoint.publish(url);
			
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, url);
		} else {
			BrokerPort port = new BrokerPort(uddiURL, false);
			endpoint = Endpoint.create(port);
			endpoint.publish(url);
			
			BrokerService service = new BrokerService();
			BrokerPortType prt = service.getBrokerPort();
			BindingProvider bindingProvider = (BindingProvider) prt;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8090/broker-ws/endpoint");
			
			int connectTimeout = 1000;
			int responseTimeout = 3000;
			requestContext.put("com.sun.xml.ws.connect.timeout", connectTimeout);
			requestContext.put("com.sun.xml.internal.ws.connect.timeout", connectTimeout);
			requestContext.put("javax.xml.ws.client.connectionTimeout", connectTimeout);
			
			requestContext.put("com.sun.xml.ws.request.timeout", responseTimeout);
			requestContext.put("com.sun.xml.internal.ws.request.timeout", responseTimeout);
			requestContext.put("javax.xml.ws.client.receiveTimeout", responseTimeout);
			
			try {
				boolean primaryIsAlive = true;
				while (primaryIsAlive) {
					primaryIsAlive = prt.alive();
					if (primaryIsAlive) {
						System.out.println("PRIMARY is ALIVE!");			
						}
					Thread.sleep(3000);                 
				}
			    
			} catch(InterruptedException ex) {
			    Thread.currentThread().interrupt();
			} catch (WebServiceException e) {
				System.out.println("Caught: " + e);
                Throwable cause = e.getCause();
                if (cause != null && cause instanceof SocketTimeoutException) {
                    System.out.println("The cause was a timeout exception: " + cause);
                }
			}
			
		}
		
		/*
		BrokerPort port = new BrokerPort(uddiURL, isPrimary);
		endpoint = Endpoint.create(port);
		endpoint.publish(url);
		if (isPrimary) {
			uddiNaming = new UDDINaming(uddiURL);
			uddiNaming.rebind(name, url);					
		} else {
			
			BrokerService service = new BrokerService();
			BrokerPortType prt = service.getBrokerPort();
			BindingProvider bindingProvider = (BindingProvider) prt;
			Map<String, Object> requestContext = bindingProvider.getRequestContext();
			requestContext.put(ENDPOINT_ADDRESS_PROPERTY, "http://localhost:8090/broker-ws/endpoint");
			
			int connectTimeout = 1000;
			int responseTimeout = 3000;
			requestContext.put("com.sun.xml.ws.connect.timeout", connectTimeout);
			requestContext.put("com.sun.xml.internal.ws.connect.timeout", connectTimeout);
			requestContext.put("javax.xml.ws.client.connectionTimeout", connectTimeout);
			
			requestContext.put("com.sun.xml.ws.request.timeout", responseTimeout);
			requestContext.put("com.sun.xml.internal.ws.request.timeout", responseTimeout);
			requestContext.put("javax.xml.ws.client.receiveTimeout", responseTimeout);
			
			try {
				if (prt.alive()) {
					System.out.println("PRIMARY is ALIVE!");
				}
			} catch (WebServiceException e) {
				System.out.println("Caught: " + e);
                Throwable cause = e.getCause();
                if (cause != null && cause instanceof SocketTimeoutException) {
                    System.out.println("The cause was a timeout exception: " + cause);
                }
			}

		}
		*/
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

