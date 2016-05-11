package pt.upa.ca;

import pt.upa.ca.ws.cli.CAClient;

public class CAClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(CAClientApplication.class.getSimpleName() + " starting...");
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s wsURL%n", CAClientApplication.class.getName());
			return;
		}

		/*
		TransporterClient port = new TransporterClient(args[0]);
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
		*/
		

	}
}
