package pt.upa.transporter.domain;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import pt.upa.transporter.ws.BadJobFault;
import pt.upa.transporter.ws.BadJobFault_Exception;
import pt.upa.transporter.ws.BadPriceFault;
import pt.upa.transporter.ws.BadPriceFault_Exception;

public abstract class Job{
	protected String companyName;
    protected String jobIdentifier;
    protected String jobOrigin;
    protected String jobDestination;
    protected int jobPrice;
    protected JobState jobState;
	
	
	
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
					setJobState(JobState.HEADING);
					schedule();
					break;
				case HEADING:
					setJobState(JobState.ONGOING);
					schedule();
					break;
				case ONGOING:
					setJobState(JobState.COMPLETED);
					schedule();
					break;
				default:
					timer.cancel();
					break;
    		 }
         }
         
         public void schedule() {
        	 int delay = new Random().nextInt(4000) + 1000;
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
		this.jobState = JobState.PROPOSED;
	}
	
	
	
	public void setTimer() {
		new UpdateJob().schedule();
	}
	
	protected abstract int evaluate(int max_price);
	
	public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String value) {
        this.companyName = value;
    }

    public String getJobIdentifier() {
        return jobIdentifier;
    }

    public void setJobIdentifier(String value) {
        this.jobIdentifier = value;
    }

    public String getJobOrigin() {
        return jobOrigin;
    }

    public void setJobOrigin(String value) {
        this.jobOrigin = value;
    }

    public String getJobDestination() {
        return jobDestination;
    }


    public void setJobDestination(String value) {
        this.jobDestination = value;
    }

    public int getJobPrice() {
        return jobPrice;
    }

    public void setJobPrice(int value) {
        this.jobPrice = value;
    }

    public JobState getJobState() {
        return jobState;
    }

    public void setJobState(JobState value) {
        this.jobState = value;
    }


	public void accept() throws BadJobFault_Exception {
		if (this.getJobState().equals(JobState.PROPOSED)) {
			this.setJobState(JobState.ACCEPTED);
			this.setTimer();
		} else {
			BadJobFault fault = new BadJobFault();
			fault.setId(this.getJobIdentifier());
			throw new BadJobFault_Exception(this.getJobIdentifier(), null);
		}
	}



	public void reject() throws BadJobFault_Exception {
		if (this.getJobState().equals(JobState.PROPOSED)) {
			this.setJobState(JobState.REJECTED);
		} else {
			BadJobFault fault = new BadJobFault();
			fault.setId(this.getJobIdentifier());
			throw new BadJobFault_Exception(this.getJobIdentifier(), null);
		}
	}

}
