package pt.upa.broker.ws;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebService;
import javax.xml.registry.JAXRException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.broker.domain.Transport;
import pt.upa.broker.domain.TransportState;
import pt.upa.broker.domain.TransporterJob;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
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
	protected int transportId;
	protected Map<String, Transport> transports = new HashMap<String, Transport>();
	protected Map<String, TransporterClient> transporterCompanies = new HashMap<String, TransporterClient>();
	
	
	public BrokerPort (String uddiURL) throws JAXRException {
		UDDINaming uddiNaming = new UDDINaming(uddiURL);
		Collection<String> endpointAddresses = uddiNaming.list("UpaTransporter%");
		
		for(String endpointAddress: endpointAddresses){
			// FIXME (remove print)
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
		
		String transportId = String.valueOf(getNextTransportId());
		
		transports.put(transportId, new Transport(origin, destination, price, transportId));
		
		List<TransporterJob> jobs = new ArrayList<TransporterJob>();
		for(Entry <String, TransporterClient> company: transporterCompanies.entrySet()) {
			try {
				JobView job = company.getValue().requestJob(origin, destination, price);
				if (job != null ) {
					jobs.add(new TransporterJob(job, company.getValue()));
				}
			} catch (BadLocationFault_Exception e) {
				UnknownLocationFault unknownLocation = new UnknownLocationFault();
				unknownLocation.setLocation(e.getFaultInfo().getLocation());
				throw new UnknownLocationFault_Exception("Unknown location", unknownLocation);
			} catch (BadPriceFault_Exception e) {
				InvalidPriceFault invalidPrice = new InvalidPriceFault();
				invalidPrice.setPrice(e.getFaultInfo().getPrice());
				throw new InvalidPriceFault_Exception("Price cannot be below zero", invalidPrice);
			}
			}
		
		if(jobs.isEmpty()){
			UnavailableTransportFault transportFault = new UnavailableTransportFault();
			transportFault.setDestination(destination);
			transportFault.setOrigin(origin);
			throw new UnavailableTransportFault_Exception("Transport not found", transportFault);
		}
		
		int bestPrice = price;
		for (TransporterJob transpJob:jobs) {
			if(transpJob.getJobPrice() < bestPrice){
				bestPrice = transpJob.getJobPrice();
				transports.get(transportId).bindTransporter(price, transpJob.getJob().getJobIdentifier(),
						TransportState.BUDGETED, transpJob.getCompanyName(), transpJob.getCompany());
			}
		}
		if (bestPrice == price){
			transports.get(transportId).setState(TransportState.FAILED);
			UnavailableTransportPriceFault priceFault = new UnavailableTransportPriceFault();
			priceFault.setBestPriceFound(bestPrice);
			throw new UnavailableTransportPriceFault_Exception("Cannot find transporter to current price", priceFault);
		}
		
		try {
			String jobId = transports.get(transportId).getJobIdentifier();
			transports.get(transportId).getTransporterEndpoint().decideJob(jobId, true);
			transports.get(transportId).setState(TransportState.BOOKED);
			for (TransporterJob transpJob:jobs){
				if(!transpJob.getCompany().equals(transports.get(transportId).getTransporterEndpoint())){
						transpJob.getCompany().decideJob(transpJob.getJob().getJobIdentifier(), false);
				}
			}
		} catch (BadJobFault_Exception e) {
			// FIXME
		}
		
		return transportId;
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		Transport transport = transports.get(id);
		if (transport == null) {
			UnknownTransportFault transportFault = new UnknownTransportFault();
			transportFault.setId(id);
			throw new UnknownTransportFault_Exception("Cannot find transport", transportFault);
		}
		if(transport.getState() == TransportState.REQUESTED
				|| transport.getState() == TransportState.BUDGETED
				|| transport.getState() == TransportState.FAILED){
			return createTransportView(transport);
		}
		
		JobView job = transport.getTransporterEndpoint().jobStatus(transport.getJobIdentifier());
		if (job == null) {
			UnknownTransportFault transportFault = new UnknownTransportFault();
			transportFault.setId(transport.getJobIdentifier());
			throw new UnknownTransportFault_Exception("Cannot find job", transportFault);
		}
		transport.setState(job.getJobState());
		return createTransportView(transport);
	}

	@Override
	public List<TransportView> listTransports() {
		List<TransportView> transportViews = new ArrayList<TransportView>();
		for(Entry <String, Transport> transportEntry: transports.entrySet()){
			Transport transport = transportEntry.getValue();
			if(transport.getState() == TransportState.REQUESTED
			|| transport.getState() == TransportState.BUDGETED
			|| transport.getState() == TransportState.FAILED){
				transportViews.add(createTransportView(transport));
			}
			else {
				JobView job = transport.getTransporterEndpoint().jobStatus(transport.getJobIdentifier());
				transport.setState(job.getJobState());
				transportViews.add(createTransportView(transport));
			}
		}
		return transportViews;
	}

	@Override
	public void clearTransports() {
		// TODO Auto-generated method stub
		
	}

	private int getNextTransportId(){
		this.transportId++;
		return this.transportId; 
	}
	
	private TransportView createTransportView(Transport transport){
		TransportView transportView = new TransportView();
		transportView.setId(transport.getId());
		transportView.setOrigin(transport.getOrigin());
		transportView.setDestination(transport.getDestination());
		transportView.setPrice(transport.getPrice());
		transportView.setState(TransportStateView.fromValue(transport.getState().value()));
		transportView.setTransporterCompany(transport.getTransporterCompany());
		return transportView;
	}

}
