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

		TransporterClient port = new TransporterClient(args[0]);
		System.out.println(port.ping("testing..."));

	}
}
