package pt.upa.broker;

public class BrokerApplication {

	public static void main(String[] args) throws Exception {
		System.out.println(BrokerApplication.class.getSimpleName() + " starting...");
		
		// Check arguments
		if (args.length < 4 || (!args[3].equals("0") && !args[3].equals("1")) ) {
			System.out.println("NUm args = " + args.length);
			System.out.println(args[3].getClass().getName());
			System.out.println("Arg 4 = " + args[3]);
			System.err.println("Missing or invalid argument(s)!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL brokerNumber%n", BrokerApplication.class.getName());
			return;
		}
		
		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];
		boolean isPrimary = args[3].equals("0") ? true : false;

		EndpointManager server = new EndpointManager(uddiURL, name, url, isPrimary);
		
		try {																// FIXME all printout
			System.out.printf("Starting %s%n", url);								
			System.out.printf("Publishing '%s' to UDDI at %s%n", name, uddiURL);
			server.start();
			// wait
			System.out.println("Awaiting connections");
			System.out.println("Press enter to shutdown");
			System.in.read();

		} catch (Exception e) {
			System.out.printf("Caught exception: %s%n", e);
			e.printStackTrace();

		} finally {
			try {
				System.out.println("Stopping...");
				server.stop();
				System.out.println("Stopped!!");
				
			} catch (Exception e) {
				System.out.printf("Caught exception when stopping: %s%n", e);
			}
		}
	}

}
