package pt.upa.transporter.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jws.WebService;

import pt.upa.transporter.domain.Job;
import pt.upa.transporter.domain.NorthJob;
import pt.upa.transporter.domain.SouthJob;

@WebService(
	    endpointInterface="pt.upa.transporter.ws.TransporterPortType",
	    wsdlLocation="transporter.1_0.wsdl",
	    name="TransporterWebService",
	    portName="TransporterPort",
	    targetNamespace="http://ws.transporter.upa.pt/",
	    serviceName="TransporterService"
	)
public class TransporterPort implements TransporterPortType {
	protected int id_counter;
	protected Map<String, Job> jobs = new HashMap<String, Job>();
	protected String companyName;
	protected boolean even;
	
	public TransporterPort (String name) {
		this.companyName = name;
		
		if (name != null && name.length() != 0) {
			String transportNumber = name.substring("UpaTransporter".length());
			int upatransporter = Integer.parseInt(transportNumber);
			this.even = (((upatransporter & 1) == 0)? true : false);
		}
		
		this.id_counter = 0;
	}

	@Override
	public String ping(String name) {
		return companyName + ": " + name;
	}

	@Override
	public JobView requestJob(String origin, String destination, int price)
			throws BadLocationFault_Exception, BadPriceFault_Exception {
		if (price > 100) {
			return null;
		} else {
			String newid = String.valueOf(newID());
			Job newJob;
			if (even) {
				newJob = new NorthJob(origin, destination, companyName, price, newid);
			} else {
				newJob = new SouthJob(origin, destination, companyName, price, newid);
			}
			this.jobs.put(newid, newJob);
			return createView(newJob);
		}
		
	}

	@Override
	public JobView decideJob(String id, boolean accept) throws BadJobFault_Exception {
		if(!this.jobs.containsKey(id)) {
			BadJobFault fault = new BadJobFault();
			fault.setId(id);
			throw new BadJobFault_Exception(id, null);
		} else {
			Job job = this.jobs.get(id);
			if (accept) {
				job.accept();
				job.setTimer();
				return createView(job);
			} else {
				job.reject();
				return createView(job);
			}
		}
	}

	@Override
	public JobView jobStatus(String id) {
		Job job = this.jobs.get(id);
		if (job != null) {
			return createView(job);
		}
		return null;
	}

	@Override
	public List<JobView> listJobs() {
		List<JobView> listJobs = new ArrayList<JobView>();
		for(Entry<String, Job> mapEntry : jobs.entrySet()) {
			listJobs.add(createView(mapEntry.getValue()));
		}
		return listJobs;
	}

	@Override
	public void clearJobs() {
		this.jobs.clear();
	}
	
	private int newID() {
		id_counter++;
		return id_counter;
	}
	
	private JobView createView(Job job) {
			JobView viewJob = new JobView();
			viewJob.setCompanyName(job.getCompanyName());
			viewJob.setJobDestination(job.getJobDestination());
			viewJob.setJobOrigin(job.getJobOrigin());
			viewJob.setJobIdentifier(job.getJobIdentifier());
			viewJob.setJobPrice(job.getJobPrice());
			viewJob.setJobState(JobStateView.fromValue(job.getJobState().value()));
			return viewJob;
	}

}
