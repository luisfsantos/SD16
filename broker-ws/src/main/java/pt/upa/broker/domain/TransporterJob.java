package pt.upa.broker.domain;

import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterJob {
	private JobView job;
	private TransporterClient company;
	
	public TransporterJob (JobView job, TransporterClient company) {
		this.job = job;
		this.company = company;
	}
	
	public JobView getJob() {
		return job;
	}


	public TransporterClient getCompany() {
		return company;
	}
	
	public int getJobPrice(){
		return job.getJobPrice();
	}
	
	public String getCompanyName() {
		return job.getCompanyName();
	}

}
