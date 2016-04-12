package pt.upa.broker.domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import pt.upa.broker.ws.InvalidPriceFault;
import pt.upa.broker.ws.InvalidPriceFault_Exception;
import pt.upa.broker.ws.UnknownLocationFault;
import pt.upa.broker.ws.UnknownLocationFault_Exception;
import pt.upa.transporter.ws.cli.TransporterClient;

public class Transport  {
	protected String id;
	protected String jobIdentifier;
	protected String origin;
    protected String destination;
    protected Integer price;
    protected String transporterCompany;
    protected TransportState state;
    protected TransporterClient transporterEndpoint;
    
	private static final Set<String> locations = new HashSet<String>(
			Arrays.asList(new String[] {"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança", 
					"Lisboa", "Leiria", "Santarem", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda",
					"Setubal", "Évora", "Portalegre", "Beja", "Faro"}
			));

	public Transport(String origin, String destination, Integer price, String id) 
			throws InvalidPriceFault_Exception, UnknownLocationFault_Exception {
		
		if (price < 0){
			InvalidPriceFault invalidPrice = new InvalidPriceFault();
			invalidPrice.setPrice(price);
			throw new InvalidPriceFault_Exception("Price cannot be below zero", invalidPrice);
		}
		if (!Transport.locations.contains(origin)) {
			UnknownLocationFault unknownLocation = new UnknownLocationFault();
			unknownLocation.setLocation(origin);
			throw new UnknownLocationFault_Exception("Unknown location", unknownLocation);
		}
		if (!Transport.locations.contains(destination)){
			UnknownLocationFault unknownLocation = new UnknownLocationFault();
			unknownLocation.setLocation(destination);
			throw new UnknownLocationFault_Exception("Unknown location", unknownLocation);
		}
		this.origin = origin;
		this.destination = destination;
		this.price = price;
		this.id = id;
		this.state = TransportState.REQUESTED;
	}
	
	public void bindTransporter(Integer price, String jobIdentifier, TransportState state,
			String companyName, TransporterClient transporterCompany) {
		
		this.setPrice(price);
		this.setJobIdentifier(jobIdentifier);
		this.setState(state);
		this.setTransporterCompany(companyName);
		this.setTransporterEndpoint(transporterCompany);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
    public String getJobIdentifier() {
		return jobIdentifier;
	}

	public void setJobIdentifier(String jobIdentifier) {
		this.jobIdentifier = jobIdentifier;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public String getTransporterCompany() {
		return transporterCompany;
	}

	public void setTransporterCompany(String transporterCompany) {
		this.transporterCompany = transporterCompany;
	}

	public TransportState getState() {
		return state;
	}

	public void setState(TransportState state) {
		this.state = state;
	}

	public TransporterClient getTransporterEndpoint() {
		return transporterEndpoint;
	}

	public void setTransporterEndpoint(TransporterClient transporterEndpoint) {
		this.transporterEndpoint = transporterEndpoint;
	}

}
