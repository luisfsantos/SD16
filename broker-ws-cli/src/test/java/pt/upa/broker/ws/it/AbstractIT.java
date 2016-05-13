package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.cli.BrokerClient;

import javax.xml.registry.JAXRException;

/**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class AbstractIT {

	protected static BrokerClient CLIENT;

	protected static int PRICE_UPPER_LIMIT = 100;
	protected static int PRICE_SMALLEST_LIMIT = 10;

	protected static int INVALID_PRICE = -1;
	protected static int ZERO_PRICE = 0;
	protected static int UNITARY_PRICE = 1;

	protected static int ODD_INCREMENT = 1;
	protected static int EVEN_INCREMENT = 2;
	
	protected static final String SOUTH_1 = "Beja";
	protected static final String SOUTH_2 = "Portalegre";
	
	protected static final String CENTER_1 = "Lisboa";
	protected static final String CENTER_2 = "Coimbra";
	
	protected static final String NORTH_1 = "Porto";
	protected static final String NORTH_2 = "Braga";
	
	protected static final String EMPTY_STRING = "";
	
	protected static final int DELAY_LOWER = 1000; // = 1 second
	protected static final int DELAY_UPPER = 5000; // = 5 seconds
    protected static final int TENTH_OF_SECOND = 100;



    // one-time initialization and clean-up
	


    @BeforeClass
    public static void oneTimeSetUp() {
    	String url = "http://localhost:9090";
		String name = "UpaBroker";

		try {
			CLIENT = new BrokerClient(url, name);
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	
    }

    @AfterClass
    public static void oneTimeTearDown() {
    	CLIENT = null;
    }



}