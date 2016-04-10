package pt.upa.transporter.domain;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import pt.upa.transporter.ws.BadPriceFault;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;

public abstract class Job extends JobView {
	protected static final Set<String> north = new HashSet<String>(
			Arrays.asList(new String[] {"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança"}
			));
	protected static final Set<String> centre = new HashSet<String>(
			Arrays.asList(new String[] {"Lisboa", "Leiria", "Santarem", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda"}
			));
	protected static final Set<String> south = new HashSet<String>(
			Arrays.asList(new String[]{"Setubal", "Évora", "Portalegre", "Beja", "Faro"}
			));
	
	private Timer timer = new Timer();
	
    class UpdateJob extends TimerTask {
    	
    	 @Override
         public void run() {
    		 switch (jobState) {
				case ACCEPTED:
					setJobState(JobStateView.HEADING);
					schedule();
					break;
				case HEADING:
					setJobState(JobStateView.ONGOING);
					schedule();
					break;
				case ONGOING:
					setJobState(JobStateView.COMPLETED);
					schedule();
					break;
				default:
					timer.cancel();
					break;
    		 }
         }
         
         public void schedule() {
        	 int delay = new Random().nextInt(5000);
        	 try {
        		 timer.schedule(new UpdateJob(), delay);
        	 } catch (IllegalStateException e) {
        		 
        	 }
         }

    }
	
	public Job(String origin, String destination, String companyName, int max_price, String id) throws BadPriceFault_Exception {
		if (max_price < 0) {
			BadPriceFault fault = new BadPriceFault();
			fault.setPrice(max_price);
			throw new BadPriceFault_Exception("Price cannot be below zero", fault);
		}
		this.jobOrigin = origin;
		this.jobDestination = destination;
		this.companyName = companyName;
		this.jobIdentifier = id;
		this.jobPrice = this.evaluate(max_price);
		this.jobState = JobStateView.PROPOSED;
	}
	
	
	
	public void setTimer() {
		new UpdateJob().schedule();
	}
	
	protected abstract int evaluate(int max_price);

}
