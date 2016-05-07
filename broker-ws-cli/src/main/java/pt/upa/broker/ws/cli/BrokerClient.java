package pt.upa.broker.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportData;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;



public class BrokerClient implements BrokerPortType {
	
	private BrokerPortType port;
	private UDDINaming uddiNaming;
	private String serviceName;
	
	public BrokerClient(String uddiURL, String name) throws JAXRException {
		this.uddiNaming =  new UDDINaming(uddiURL);
		this.serviceName = name;
		this.discoverPort();
		}
	
	private boolean discoverPort() throws JAXRException {
		String endpointAddress = uddiNaming.lookup(serviceName);
		if (endpointAddress == null) {
			System.out.println("Cannot find service " + serviceName);
			return false;
		}
		System.out.println("Founded service = " + endpointAddress);
		BrokerService service = new BrokerService();
		port = service.getBrokerPort();
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
		this.setTimeouts(requestContext, 1000, 4000);
		return true;
	}
	
	private void setTimeouts(Map<String, Object> requestContext, int connectTimeout, int responseTimeout ) {
		requestContext.put("com.sun.xml.ws.connect.timeout", connectTimeout);
		requestContext.put("com.sun.xml.internal.ws.connect.timeout", connectTimeout);
		requestContext.put("javax.xml.ws.client.connectionTimeout", connectTimeout);
		
		requestContext.put("com.sun.xml.ws.request.timeout", responseTimeout);
		requestContext.put("com.sun.xml.internal.ws.request.timeout", responseTimeout);
		requestContext.put("javax.xml.ws.client.receiveTimeout", responseTimeout);
	}

	private boolean foundNewAddress(){				// FIXME ?? 
		boolean found = false;
		try {
			int counter = 0;
			while (!found && counter < 5) {
				found = this.discoverPort();
				if(!found) {
					Thread.sleep(1000);
				} else {
					System.out.println("Founded new URL of service");
				}
				counter++;
			} 
		} catch (JAXRException e) {
			System.err.println("Caught JAX-R exception!");
            e.printStackTrace(System.err);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			e.printStackTrace();
		}
		return found;
	}
	
	
	@Override
	public String ping(String name) {
		try {
			return port.ping(name);
		} catch (WebServiceException e) {
			this.foundNewAddress();
			return port.ping(name);
		}
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {	
		try {
			return port.requestTransport(origin, destination, price);
		} catch (WebServiceException e) {
			this.foundNewAddress();
			return port.requestTransport(origin, destination, price);
		}
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		try {
			return port.viewTransport(id);
		} catch (WebServiceException e) {
			this.foundNewAddress();
			return port.viewTransport(id);
		}
	}

	@Override
	public List<TransportView> listTransports() {
		try {
			return port.listTransports();
		} catch (WebServiceException e) {
			this.foundNewAddress();
			return port.listTransports();
		}
	}

	@Override
	public void clearTransports() {
		try {
			port.clearTransports();
		} catch (WebServiceException e) {
			this.foundNewAddress();
			port.clearTransports();
		}
		
	}

		
	@Override
	public boolean alive() {
		System.out.println("Access denied");
		return false;
	}
	
	@Override
	public void updateTransport(TransportData transport) {
		System.out.println("Access denied");
		return;		
	}

}
