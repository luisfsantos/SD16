package pt.upa.broker;

import pt.upa.broker.ws.TransportView;
import pt.upa.broker.ws.cli.BrokerClient;

public class BrokerClientApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerClientApplication.class.getSimpleName() + " starting...");
		// Check arguments
		if (args.length < 2) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL name%n", BrokerClientApplication.class.getName());
			return;
		}

		String uddiURL = args[0];
		String name = args[1];


		BrokerClient port = new BrokerClient(uddiURL, name);

		try {
			String result = port.ping("friend");
			System.out.println(result);
			String idTransport = port.requestTransport("Lisboa", "Coimbra", 9);
			System.out.println(idTransport);
			
			TransportView trView = port.viewTransport(idTransport);
			System.out.print("id = " + trView.getId() + " Origin = " + trView.getOrigin());
			
		} catch (Exception pfe) {
			System.out.println("Caught: " + pfe);
		}
	}

}
