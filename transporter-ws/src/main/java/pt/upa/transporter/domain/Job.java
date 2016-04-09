package pt.upa.transporter.domain;

import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

import pt.upa.transporter.ws.BadPriceFault;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;

public abstract class Job extends JobView {
	protected int id;
	protected static final Set<String> north = new HashSet<String>(
			Arrays.asList(new String[] {"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança"}
			));
	protected static final Set<String> centre = new HashSet<String>(
			Arrays.asList(new String[] {"Lisboa", "Leiria", "Santarem", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"}
			));
	protected static final Set<String> south = new HashSet<String>(
			Arrays.asList(new String[]{"Setubal", "Évora", "Portalegre", "Beja", "Faro"}
			));
	
	public Job(String origin, String destination, String companyName, int max_price, int id) throws BadPriceFault_Exception {
		if (max_price < 0) {
			BadPriceFault fault = new BadPriceFault();
			fault.setPrice(max_price);
			throw new BadPriceFault_Exception("Price cannot be below zero", fault);
		}
		this.jobOrigin = origin;
		this.jobDestination = destination;
		this.companyName = companyName;
		this.id = id;
		this.jobPrice = this.evaluate(max_price);
	}
	protected abstract int evaluate(int max_price);
	public abstract void evolve();
}
