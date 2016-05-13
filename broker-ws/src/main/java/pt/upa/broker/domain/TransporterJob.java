package pt.upa.broker.domain;

import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterJob {
	private JobView job;
	private TransporterClient companyEndpoint;
	
	public TransporterJob (JobView job, TransporterClient company) {
		this.job = job;
		this.companyEndpoint = company;
	}
	
	public JobView getJob() {
		return job;
	}


	public TransporterClient getCompanyEndpoint() {
		return companyEndpoint;
	}
	
	public int getJobPrice(){
		return job.getJobPrice();
	}
	
	public String getJobIdentifier() {
		return job.getJobIdentifier();
	}
	
	public String getCompanyName() {
		return job.getCompanyName();
	}

}
