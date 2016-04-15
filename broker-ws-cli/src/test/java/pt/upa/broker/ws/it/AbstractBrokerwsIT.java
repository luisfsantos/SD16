package pt.upa.broker.ws.it;

import org.junit.*;

import pt.upa.broker.ws.cli.BrokerClient;

import static org.junit.Assert.*;

import javax.xml.registry.JAXRException;

/**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class AbstractBrokerwsIT {

    // static members
	public static BrokerClient brokerClient;
	

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {
    	
    }


    // members
	
	private String url = "http://localhost:9090";
	private String name = "UpaBroker";

    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	try {
			 brokerClient = new BrokerClient(url, name);
		} catch (JAXRException e) {
			e.printStackTrace();
		}
    }

    @After
    public void tearDown() {
    	brokerClient = null;
    }


    // tests

    @Test
    public void test() {
    	
        // assertEquals(expected, actual);
        // if the assert fails, the test fails
    }

}