package pt.upa.transporter.ws;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.jws.HandlerChain;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import pt.upa.transporter.domain.Job;
import pt.upa.transporter.domain.NorthJob;
import pt.upa.transporter.domain.SouthJob;
import pt.upa.ws.handler.AuthenticationHandler;

@WebService(
	    endpointInterface="pt.upa.transporter.ws.TransporterPortType",
	    wsdlLocation="transporter.1_0.wsdl",
	    name="TransporterWebService",
	    portName="TransporterPort",
	    targetNamespace="http://ws.transporter.upa.pt/",
	    serviceName="TransporterService"
	)

@HandlerChain(file = "/upa_handler-chain.xml")
public class TransporterPort implements TransporterPortType {
	protected int id_counter;
	protected Map<String, Job> jobs = new HashMap<String, Job>();
	private static final Set<String> locations = new HashSet<String>(
			Arrays.asList(new String[] {"Porto", "Braga", "Viana do Castelo", "Vila Real", "Bragança", 
					"Lisboa", "Leiria", "Santarem", "Castelo Branco", "Coimbra", "Aveiro", "Viseu", "Guarda",
					"Setubal", "Évora", "Portalegre", "Beja", "Faro"}
			));
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
		} else if (!locations.contains(origin) || !locations.contains(destination)) {
			BadLocationFault fault = new BadLocationFault();
			fault.setLocation(destination);
			throw new BadLocationFault_Exception("No such destination", fault);
		} else {
			String newid = String.valueOf(newID());
			Job newJob;
			try {
				if (even) {
					newJob = new NorthJob(origin, destination, companyName, price, newid);
				} else {
					newJob = new SouthJob(origin, destination, companyName, price, newid);
				}
			} catch (BadLocationFault_Exception e) {
				return null;
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
