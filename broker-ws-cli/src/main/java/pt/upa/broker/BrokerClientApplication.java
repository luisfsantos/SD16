package pt.upa.broker;

import java.util.List;

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
			
			String result = port.ping("Ping response");
			System.out.println(result);
			
			
			System.out.println("press Enter to call requestTransport");
			System.in.read();
			
			String idTransport = port.requestTransport("Lisboa", "Coimbra", 6);
			System.out.println("Transport ID = " + idTransport);
			
			System.out.println("press Enter to call requestTransport");
			System.in.read();
			
			String idTransport2 = port.requestTransport("Coimbra", "Lisboa", 7);
			System.out.println("Transport ID = " + idTransport2);
			
			System.out.println("press Enter to call viewTransport, where Transport ID = " + idTransport);
			System.in.read();
			
			TransportView trView = port.viewTransport(idTransport);
			System.out.println("Transport ID = " + trView.getId() + " company = " + trView.getTransporterCompany() + " state = " + trView.getState().value() );
			
			System.out.println("press Enter to call viewTransport, where Transport ID = " + idTransport2);
			System.in.read();
			
			TransportView trView2 = port.viewTransport(idTransport2);
			System.out.println("Transport ID = " + trView2.getId() + " company = " + trView2.getTransporterCompany() + " state = " + trView2.getState().value() );
			

			System.out.println("press Enter to call listTransports");
			System.in.read();
			
			List<TransportView> trViews = port.listTransports();
			System.out.println("All transports views:");
			for(TransportView transportView : trViews ){
				System.out.println("Transport ID = " + transportView.getId() + " company = " + transportView.getTransporterCompany() + " state = " + transportView.getState().value() );
			}
			
			
			System.out.println("press Enter to call clearTransports");
			System.in.read();
			
			port.clearTransports();
			
			
			System.out.println("press Enter to call requestTransport");
			System.in.read();
			
			String idTransport3 = port.requestTransport("Coimbra", "Lisboa", 8);
			System.out.println("Transport ID = " + idTransport3);
			

			System.out.println("press Enter to call requestTransport");
			System.in.read();
			
			String idTransport4 = port.requestTransport("Coimbra", "Lisboa", 2);
			System.out.println("Transport ID = " + idTransport4);
			
			
			System.out.println("press Enter to call listTransports");
			System.in.read();
			
			List<TransportView> trViews2 = port.listTransports();
			System.out.println("All transports views:");
			for(TransportView transportView : trViews2 ){
				System.out.println("Transport ID = " + transportView.getId() + " company = " + transportView.getTransporterCompany() + " state = " + transportView.getState().value() );
			}
			
			
		} catch (Exception pfe) {
			System.out.println("Caught: " + pfe);
		}
	}

}
