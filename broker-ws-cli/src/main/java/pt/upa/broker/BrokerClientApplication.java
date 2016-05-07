package pt.upa.broker;

import java.util.List;

import pt.upa.broker.ws.TransportData;
import pt.upa.broker.ws.TransportStateView;
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
			String idTransport = port.requestTransport("Lisboa", "Coimbra", 6);
			System.out.println(idTransport);
			String idTransport2 = port.requestTransport("Coimbra", "Lisboa", 7);
			System.out.println(idTransport2);
			
			TransportView trView = port.viewTransport(idTransport);
			System.out.println("id = " + trView.getId() + " Origin = " + trView.getOrigin() + " price = " + trView.getPrice());
			
			TransportView trView2 = port.viewTransport(idTransport2);
			System.out.println("id = " + trView2.getId() + " Origin = " + trView2.getOrigin() + " price = " + trView2.getPrice() );
			
			
			System.out.println("---sleep 10 sec---");
			Thread.sleep(10000);		
			System.out.println("---continue---");
			
			List<TransportView> trViews = port.listTransports();
			System.out.println("All transports views:");
			for(TransportView transportView : trViews ){
				System.out.println("id = " + transportView.getId() + " Origin = " + transportView.getOrigin() + " price = " + transportView.getPrice());
			}
			port.clearTransports();
			List<TransportView> trViews2 = port.listTransports();
			System.out.println("Transports deleted:");
			for(TransportView transportView : trViews2 ){
				System.out.println("id = " + transportView.getId() + " Origin = " + transportView.getOrigin() + " price = " + transportView.getPrice());
			}
			
			String idTransport3 = port.requestTransport("Coimbra", "Lisboa", 8);
			System.out.println(idTransport3);
			
			TransportView trView3 = port.viewTransport(idTransport3);
			System.out.println("id = " + trView3.getId() + " Origin = " + trView3.getOrigin() + " price = " + trView3.getPrice() );
			
			String idTransport4 = port.requestTransport("Coimbra", "Lisboa", 2);
			System.out.println(idTransport4);
			
			TransportView trView4 = port.viewTransport(idTransport4);
			System.out.println("id = " + trView4.getId() + " Origin = " + trView4.getOrigin() + " price = " + trView4.getPrice() );
			
			System.out.println("TR COMPANIES =" + trView.getTransporterCompany() +
					" " + trView2.getTransporterCompany() + " " + trView3.getTransporterCompany() +
					" " + trView4.getTransporterCompany());
			
			System.out.println("WSDL NEW METHOD = " + port.alive());
			
			TransportData t = new TransportData();
			t.setId("a");
			t.setJobId("b");
			t.setOrigin("c");
			t.setDestination("d");
			t.setPrice(5);
			t.setTransporterCompany("e");
			t.setState(TransportStateView.BUDGETED);
			t.setEndpointAddress("f");
			
			port.updateTransport(t);
			System.out.println("update transport...");
			
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		} catch (Exception pfe) {
			System.out.println("Caught: " + pfe);
		}
	}

}
