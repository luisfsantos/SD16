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
			
			String result = port.ping("friend");
			System.out.println(result);
			String idTransport = port.requestTransport("Lisboa", "Coimbra", 50);
			System.out.println(idTransport);
			String idTransport2 = port.requestTransport("Coimbra", "Lisboa", 7);
			System.out.println(idTransport2);
			
			TransportView trView = port.viewTransport(idTransport);
			System.out.println("id = " + trView.getId() + " Origin = " + trView.getOrigin() + " price = " + trView.getPrice());
			
			TransportView trView2 = port.viewTransport(idTransport2);
			System.out.println("id = " + trView2.getId() + " Origin = " + trView2.getOrigin() + " price = " + trView2.getPrice() );
			
			List<TransportView> trViews = port.listTransports();
			System.out.println("All transports views");
			for(TransportView transportView : trViews ){
				System.out.println("id = " + transportView.getId() + " Origin = " + transportView.getOrigin() + " price = " + transportView.getPrice());
			}
			port.clearTransports();
			List<TransportView> trViews2 = port.listTransports();
			System.out.println("Transports deleted");
			for(TransportView transportView : trViews2 ){
				System.out.println("id = " + transportView.getId() + " Origin = " + transportView.getOrigin() + " price = " + transportView.getPrice());
			}
			
			String idTransport3 = port.requestTransport("Coimbra", "Lisboa", 8);
			System.out.println(idTransport3);
			
			TransportView trView3 = port.viewTransport(idTransport3);
			System.out.println("id = " + trView3.getId() + " Origin = " + trView3.getOrigin() + " price = " + trView3.getPrice() );
			
			String idTransport4 = port.requestTransport("Coimbra", "Lisboa", 100);
			System.out.println(idTransport4);
			
			TransportView trView4 = port.viewTransport(idTransport4);
			System.out.println("id = " + trView4.getId() + " Origin = " + trView4.getOrigin() + " price = " + trView4.getPrice() );
			
			
			System.out.println("WSDL NEW METHOD = " + port.alive());
			
		} catch (Exception pfe) {
			System.out.println("Caught: " + pfe);
		}
	}

}
