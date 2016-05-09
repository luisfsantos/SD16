package pt.upa.transporter.domain;

import java.util.concurrent.ThreadLocalRandom;

import pt.upa.transporter.ws.BadLocationFault;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;

public class NorthJob extends Job {

	public NorthJob(String origin, String destination, String companyName, int max_price, String id) throws BadPriceFault_Exception, BadLocationFault_Exception {
		super(origin, destination, companyName, max_price, id);
		
		if (!centre.contains(origin) && !north.contains(origin))  {
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(origin);
			throw new BadLocationFault_Exception("No such origin", fault);
		} else if (!centre.contains(destination) && !north.contains(destination)) {
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(destination);
			throw new BadLocationFault_Exception("No such destination", fault);
		}
		
		
		
	}

	@Override
	protected int evaluate(int max_price) {
		if (max_price <= 10) {
			return ThreadLocalRandom.current().nextInt(0, max_price);
		} else {
			if ((max_price & 1) == 0) {
				return ThreadLocalRandom.current().nextInt(1, max_price);
			} else {
				return ThreadLocalRandom.current().nextInt(max_price, 101);
			}
		}
		
	}

}
