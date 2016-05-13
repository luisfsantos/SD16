package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.upa.transporter.ws.cli.TransporterClient;

import static org.junit.Assert.*;

import java.util.Properties;

/**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class AbstractIT {
	
	private static final String TEST_PROP_FILE = "/test.properties";

	private static Properties PROPS;
	protected static TransporterClient CLIENT;

	protected static final int PRICE_UPPER_LIMIT = 100;
	protected static final int PRICE_SMALLEST_LIMIT = 10;
	protected static final int UNITARY_PRICE = 1;
	protected static final int ZERO_PRICE = 0;
	protected static final int INVALID_PRICE = -1;
	protected static final String CENTRO_1 = "Lisboa";
	protected static final String SUL_1 = "Beja";
	protected static final String CENTRO_2 = "Coimbra";
	protected static final String SUL_2 = "Portalegre";
	protected static final String EMPTY_STRING = "";
	protected static final int DELAY_LOWER = 1000; // milliseconds
	protected static final int DELAY_UPPER = 5000; // milliseconds

    // static members
	protected static TransporterClient transporterClient1;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
		CLIENT = new TransporterClient("http://localhost:8081/transporter-ws/endpoint", "UpaBroker");

    }

    @AfterClass
    public static void oneTimeTearDown() {
		//transporterClient1 = null;
    }


    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    // tests

    @Test
    public void test() {

        // assertEquals(expected, actual);
        // if the assert fails, the test fails
    }

}