package pt.upa.transporter;

import java.io.IOException;
import java.util.Properties;

public class TransporterApplication {

	private static final String PROP_FILE = "/prop.properties";
	private static Properties PROPS;

	public static void main(String[] args) throws Exception {
		System.out.println(TransporterApplication.class.getSimpleName() + " starting...");
		// Check arguments
		if (args.length < 3) {
			System.err.println("Argument(s) missing!");
			System.err.printf("Usage: java %s uddiURL wsName wsURL%n", TransporterApplication.class.getName());
			return;
		}

		PROPS = new Properties();
		try {
			PROPS.load(TransporterApplication.class.getResourceAsStream(PROP_FILE));
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", PROP_FILE);
			System.out.println(msg);
			throw e;
		}


		String uddiURL = args[0];
		String name = args[1];
		String url = args[2];

		PROPS.setProperty("ws.name", name);

		EndpointManager server = new EndpointManager(uddiURL, name, url);
		
		try {
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
