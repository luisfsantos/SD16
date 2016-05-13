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
	    wsdlLocation="broker.2_0.wsdl",
	    name="BrokerWebService",
	    portName="BrokerPort",
	    targetNamespace="http://ws.broker.upa.pt/",
	    serviceName="BrokerService"
	)
public class BrokerPort implements BrokerPortType {
	protected int transportId = 0;
	protected boolean isPrimary;
	protected BrokerPortType backBroker;
	protected UDDINaming uddiNaming;
	private static final String name = "UpaBroker";
	protected Map<String, Transport> transports = new HashMap<String, Transport>();
	protected Map<String, TransporterClient> transporterCompanies = new HashMap<String, TransporterClient>();
	
	

	public BrokerPort (String uddiURL, boolean isPrimary) throws JAXRException {
		this(uddiURL, isPrimary, null);
	}

	public BrokerPort (String uddiURL, boolean isPrimary, BrokerPortType backBroker) throws JAXRException {
		this.isPrimary = isPrimary;
		this.backBroker = backBroker;
		uddiNaming = new UDDINaming(uddiURL);
		Collection<String> endpointAddresses = uddiNaming.list("UpaTransporter%");
		
		for(String endpointAddress: endpointAddresses){ 
			TransporterClient company = new TransporterClient(endpointAddress, name);
			transporterCompanies.put(endpointAddress, company);
		}
	}
	
	@Override
	public String ping(String name) {
		if (isPrimary) {
			String ping = "";
			for(Entry <String, TransporterClient> company: transporterCompanies.entrySet() ){
				ping += company.getValue().ping(name) + "\n";
			}
			if (ping.isEmpty()) {
				ping = "No one is there!";
			}
			return ping;
		} else {
			return name;
		}

	}

	@Override
	public String requestTransport(String origin, String destination, int price)
			throws InvalidPriceFault_Exception, UnavailableTransportFault_Exception,
			UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception {
		
		if (isPrimary) {
			String transportId = String.valueOf(getNextTransportId());
			Transport transport = new Transport(origin, destination, price, transportId);
			transports.put(transportId, transport);

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

			boolean jobBooked = false;
			while (!jobBooked) {
				TransporterJob betterJob = transport.selectBetterJob(jobs, price);
				transport.bindTransporter(betterJob);
				try {
					String jobId = betterJob.getJob().getJobIdentifier();
					betterJob.getCompanyEndpoint().decideJob(jobId, true);
					jobBooked = true;
					jobs.remove(betterJob);
				} catch (BadJobFault_Exception e) {
					jobs.remove(betterJob);
				}
			}
			transport.setState(TransportState.BOOKED);
			for (TransporterJob transpJob:jobs){
				try {
					transpJob.getCompanyEndpoint().decideJob(transpJob.getJob().getJobIdentifier(), false);
				} catch (BadJobFault_Exception e) { }
			}

			if (backBroker != null) {
				TransportData td = this.createTransportData( transport);
				System.out.println("Send transport with id " + transport.getId());
				backBroker.updateTransport(td);
			}

			return transportId;

		} else {
			return null;
		}
	}

	@Override
	public TransportView viewTransport(String id) throws UnknownTransportFault_Exception {
		if (isPrimary) {
			Transport transport = transports.get(id);
			if (transport == null) {
				UnknownTransportFault transportFault = new UnknownTransportFault();
				transportFault.setId(id);
				throw new UnknownTransportFault_Exception("Cannot find transport", transportFault);
			}
			if (!transport.needToBeUpdated()){
				return createTransportView(transport);
			}

			JobView job = transport.getTransporterEndpoint().jobStatus(transport.getJobIdentifier());
			if (job == null) {
				UnknownTransportFault transportFault = new UnknownTransportFault();
				transportFault.setId(transport.getJobIdentifier());
				throw new UnknownTransportFault_Exception("Cannot find job", transportFault);
			}
			transport.setState(job.getJobState());

			if (backBroker != null) {
				TransportData td = this.createTransportData( transport);
				System.out.println("Send transport with id " + transport.getId());
				backBroker.updateTransport(td);
			}

			return createTransportView(transport);

		} else {
			return null;
		}

	}

	@Override
	public List<TransportView> listTransports() {
		if (isPrimary) {
			List<TransportView> transportViews = new ArrayList<TransportView>();
			for(Entry <String, Transport> transportEntry: transports.entrySet()){
				Transport transport = transportEntry.getValue();
				if(!transport.needToBeUpdated()){
					transportViews.add(createTransportView(transport));
				}
				else {
					JobView job = transport.getTransporterEndpoint().jobStatus(transport.getJobIdentifier());
					transport.setState(job.getJobState());
					transportViews.add(createTransportView(transport));

					if (backBroker != null) {
						TransportData td = this.createTransportData( transport);
						System.out.println("Send transport with id " + transport.getId());
						backBroker.updateTransport(td);
					}

				}
			}
			return transportViews;
		} else {
			return null;
		}

	}

	@Override
	public void clearTransports() {
		if (isPrimary) {
			for(Entry <String, TransporterClient> transportEntry: transporterCompanies.entrySet()){
				transportEntry.getValue().clearJobs();
			}
			if (backBroker != null) {
				backBroker.clearTransports();
			}
		}
		transports.clear();
	}

	public void setTransportId(int transportId) {
		this.transportId = transportId;
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


	private TransportData createTransportData( Transport transport) {
		TransportData transportData = new TransportData();
		transportData.setId(transport.getId());
		transportData.setJobId(transport.getJobIdentifier());
		transportData.setOrigin(transport.getOrigin());
		transportData.setDestination(transport.getDestination());
		transportData.setPrice(transport.getPrice());
		transportData.setTransporterCompany(transport.getTransporterCompany());
		transportData.setState(TransportStateView.fromValue(transport.getState().value()));
		return transportData;
	}

	@Override
	public boolean alive() {
		if(isPrimary) {
			System.out.println("Secondary is calling!");
			return true;
		} else {
			System.out.println("Primary is calling!");
			return true;
		}
	}

	public void setPrimary(boolean isPrimary) {
		if (isPrimary) {
			System.out.println("Run as primary server");
		}
		this.isPrimary = isPrimary;
	}


	private String discoverCompanyEndpoint(String companyName) {		// FIXME? catch JAXRException
		try {
			return uddiNaming.lookup(companyName);
		} catch (JAXRException e) {
			System.err.println("Caught JAX-R exception!");
            e.printStackTrace(System.err);
            return null;
		}
	}

	private void importNewTransport(TransportData transport) {
		String companyEndpoint = discoverCompanyEndpoint(transport.getTransporterCompany());
		TransporterClient company = new TransporterClient(companyEndpoint);
		Transport transp = new Transport(transport, company);
		if (this.transportId < Integer.parseInt(transport.getId()) ) {
			this.setTransportId(Integer.parseInt(transport.getId()));
		}
		transports.put(transport.getId(), transp);
	}


	@Override
	public void updateTransport(TransportData transport) {
		if (isPrimary) {
			return;
		} else {
			int transpId = Integer.parseInt(transport.getId());
			if (transpId <= this.transportId) {
				for(Entry <String, Transport> transportEntry: transports.entrySet()) {
					if ( Integer.parseInt(transportEntry.getValue().getId()) == transpId ) {
						String companyEndpoint = discoverCompanyEndpoint(transport.getTransporterCompany());
						TransporterClient company = new TransporterClient(companyEndpoint);
						transportEntry.getValue().update(transport, company);
						System.out.println("Update transport with id " + transpId);
						return;
					}
				}
				System.out.println("[id < currId] Add new transport with id " + transpId);
				this.importNewTransport(transport);
			} else {
				System.out.println("Add new transport with id " + transpId);
				this.importNewTransport(transport);
			}
		}
	}





}
