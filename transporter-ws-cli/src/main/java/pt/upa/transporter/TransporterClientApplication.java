package pt.upa.transporter;

import pt.upa.transporter.ws.cli.TransporterClient;

public class TransporterClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterClientApplication.class.getSimpleName() + " starting...");
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s wsURL%n", TransporterClientApplication.class.getName());
			return;
		}

		
		TransporterClient port = new TransporterClient(args[0], "UpaBroker");
		System.out.println(port.ping("testing..."));
		String jobid = port.requestJob("Leiria", "Lisboa", 20).getJobIdentifier();
		System.out.println(jobid);
		System.out.println(port.decideJob(jobid, true).getJobState().value());
		try {
		    Thread.sleep(60);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println(port.jobStatus(jobid).getJobState().value());
		try {
		    Thread.sleep(800);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println(port.jobStatus(jobid).getJobState().value());
		try {
		    Thread.sleep(800);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println(port.jobStatus(jobid).getJobState().value());
		try {
		    Thread.sleep(800);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println(port.jobStatus(jobid).getJobState().value());
		try {
		    Thread.sleep(800);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println(port.jobStatus(jobid).getJobState().value());
		try {
		    Thread.sleep(800);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println(port.jobStatus(jobid).getJobState().value());
		try {
		    Thread.sleep(800);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println(port.jobStatus(jobid).getJobState().value());
		try {
		    Thread.sleep(800);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println(port.jobStatus(jobid).getJobState().value());
		try {
		    Thread.sleep(800);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println(port.jobStatus(jobid).getJobState().value());
		try {
		    Thread.sleep(6000);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		System.out.println(port.jobStatus(jobid).getJobState().value());
		System.out.println(port.requestJob("Leiria", "Lisboa", 21).getJobPrice());
		
		

	}
}
