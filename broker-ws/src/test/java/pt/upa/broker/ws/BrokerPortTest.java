package pt.upa.broker.ws;

import org.junit.*;

import mockit.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.cli.TransporterClient;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.registry.JAXRException;

/**
 *  Unit Test for Broker
*/

public class BrokerPortTest {
	@Mocked TransporterClient transporter1;
	@Mocked TransporterClient transporter2;

	public final class MockUDDINaming0Companies extends MockUp<UDDINaming>
	{
	   @Mock
	   public void $init(String uddiURL)
	   {
	      assertEquals("http://localhost:9090", uddiURL);
	   }

	   @Mock
	   public Collection<String> list(String pattern) {
		   Collection<String> transporterCompanies = new ArrayList<String>();
		   assertEquals("UpaTransporter%", pattern);
		   return transporterCompanies;
	   }

	}
	
	public final class MockUDDINaming1Companies extends MockUp<UDDINaming>
	{
	   @Mock
	   public void $init(String uddiURL)
	   {
	      assertEquals("http://localhost:9090", uddiURL);
	   }

	   @Mock
	   public Collection<String> list(String pattern) {
		   Collection<String> transporterCompanies = new ArrayList<String>();
		   assertEquals("UpaTransporter%", pattern);
		   transporterCompanies.add("http://localhost:8080/transporter-ws/endpoint");
		   return transporterCompanies;
	   }

	}
	
	public final class MockUDDINaming2Companies extends MockUp<UDDINaming>
	{
	   @Mock
	   public void $init(String uddiURL)
	   {
	      assertEquals("http://localhost:9090", uddiURL);
	   }

	   @Mock
	   public Collection<String> list(String pattern) {
		   Collection<String> transporterCompanies = new ArrayList<String>();
		   assertEquals("UpaTransporter%", pattern);
		   transporterCompanies.add("http://localhost:8080/transporter-ws/endpoint");
		   transporterCompanies.add("http://localhost:8081/transporter-ws/endpoint");
		   return transporterCompanies;
	   }

	}



    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
    	
    }

    @AfterClass
    public static void oneTimeTearDown() {

    }


    // members


    // initialization and clean-up for each test

    @Before
    public void setUp() {
    	new Expectations() {{ 
    		new TransporterClient("http://localhost:8080/transporter-ws/endpoint"); result = transporter1;
    		new TransporterClient("http://localhost:8081/transporter-ws/endpoint"); result = transporter2;
    	}};
    }

    @After
    public void tearDown() {
    	
    }
    
    void setUp0Comp() {
    	
    }


    // tests

    @Test
    public void testConstructor( ) {
    	new MockUDDINaming2Companies();
    	try {
			new BrokerPort("http://localhost:9090");;
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    @Test
    public void testPingZeroCompanies() {
    	new MockUDDINaming0Companies();
    	
    	String result = "No one is there!";
    	try {
			BrokerPort server = new BrokerPort("http://localhost:9090");
			assertEquals(result, server.ping("ola"));
		} catch (JAXRException e) {
			e.printStackTrace();
		}
    }
    
    @Test
    public void testPingTwoCompanies() {
    	new MockUDDINaming2Companies();
    	
    	new Expectations() {{ 
    		transporter1.ping("ola"); result = "UpaTransporterN: ola";
    		transporter2.ping("ola"); result = "UpaTransporterN: ola";
    	}};
    	
    	String result = "UpaTransporterN: ola\nUpaTransporterN: ola\n";
    	try {
			BrokerPort server = new BrokerPort("http://localhost:9090");
			assertEquals(result, server.ping("ola"));
		} catch (JAXRException e) {
			e.printStackTrace();
		}
    }

}
