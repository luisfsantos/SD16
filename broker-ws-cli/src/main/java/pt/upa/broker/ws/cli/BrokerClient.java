package pt.upa.broker.ws.cli;

import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;

import java.util.List;
import java.util.Map;

import javax.xml.registry.JAXRException;
import javax.xml.ws.BindingProvider;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.ws.BrokerPortType;
import pt.upa.broker.ws.BrokerService;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.UnavailableTransportFault_Exception;
import pt.upa.broker.ws.UnavailableTransportPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.broker.ws.UnknownTransportFault_Exception;



public class BrokerClient implements BrokerPortType {
	
	private BrokerPortType port;
	
	public BrokerClient(String uddiURL, String name) throws JAXRException {
		UDDINaming uddiNaming = new UDDINaming(uddiURL);
		String endpointAddress = uddiNaming.lookup(name);
		
		BrokerService service = new BrokerService();
		port = service.getBrokerPort();
		BindingProvider bindingProvider = (BindingProvider) port;
		Map<String, Object> requestContext = bindingProvider.getRequestContext();
		requestContext.put(ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
	}

	@Override
	public String ping(String name) {
		// TODO Auto-generated method stub
		return port.ping(name);
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		// TODO Auto-generated method stub
		return port.requestTransport(origin, destination, price);
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		// TODO Auto-generated method stub
		return port.viewTransport(id);
	}

	@Override
	public List<TransportView> listTransports() {
		// TODO Auto-generated method stub
		return port.listTransports();
	}

	@Override
	public void clearTransports() {
		// TODO Auto-generated method stub
		port.clearTransports();
	}


}
