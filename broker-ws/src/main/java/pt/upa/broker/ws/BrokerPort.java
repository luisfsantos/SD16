package pt.upa.broker.ws;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.domain.Transport;
import pt.upa.transporter.ws.cli.TransporterClient;

@WebService(
	    endpointInterface="pt.upa.broker.ws.BrokerPortType",
	    wsdlLocation="broker.1_0.wsdl",
	    name="BrokerWebService",
	    portName="BrokerPort",
	    targetNamespace="http://ws.broker.upa.pt/",
	    serviceName="BrokerService"
	)
public class BrokerPort implements BrokerPortType {
	
	protected Map<String, Transport> transports = new HashMap<String, Transport>();
	protected Map<String, TransporterClient> transporterCompanies = new HashMap<String, TransporterClient>();
	
	
	public BrokerPort (String uddiURL) throws JAXRException {
		UDDINaming uddiNaming = new UDDINaming(uddiURL);
		Collection<String> endpointAddresses = uddiNaming.list("UpaTransporter%");
		
		for(String endpointAddress: endpointAddresses){
			// FIXME 
			System.out.println(endpointAddress); 
			TransporterClient company = new TransporterClient(endpointAddress);
			transporterCompanies.put(endpointAddress, company);
		}
		
	}
	
	@Override
	public String ping(String name) {
		String ping = "";
		for(Entry <String, TransporterClient> company: transporterCompanies.entrySet() ){
			ping += company.getValue().ping(name) + "\n";
		}
		return ping;
	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TransportView> listTransports() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void clearTransports() {
		// TODO Auto-generated method stub
		
	}

	// TODO

}
