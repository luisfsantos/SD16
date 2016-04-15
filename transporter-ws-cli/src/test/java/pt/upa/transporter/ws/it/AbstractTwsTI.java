package pt.upa.transporter.ws.it;

import org.junit.*;
import pt.upa.transporter.ws.cli.TransporterClient;

import static org.junit.Assert.*;

/**
 *  Integration Test example
 *  
 *  Invoked by Maven in the "verify" life-cycle phase
 *  Should invoke "live" remote servers 
 */
public class AbstractTwsTI {

    // static members
	static private TransporterClient transporterClient1;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
		TransporterClient transporterClient1 = new TransporterClient("http://localhost:8081/transporter-ws/endpoint");
    }

    @AfterClass
    public static void oneTimeTearDown() {
		transporterClient1 = null;
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