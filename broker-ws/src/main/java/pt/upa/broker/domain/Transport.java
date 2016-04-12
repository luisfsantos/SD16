package pt.upa.broker.domain;



public class Transport  {
	protected String id;
    protected String origin;
    protected String destination;
    protected Integer price;
    protected String transporterCompany;
    protected TransportState state;

	public Transport() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

}
