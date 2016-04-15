package pt.upa.broker.ws;

import org.junit.*;

import mockit.*;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;

import javax.xml.registry.JAXRException;

/**
 *  Unit Test for Broker
*/

public class BrokerPortTest {
	@Mocked JobView job1;
	@Mocked JobView job2;
	@Mocked UDDINaming uddi;
	@Mocked TransporterClient transporter1;
	@Mocked TransporterClient transporter2;
	
	final static String knownCentre = "Lisboa";
	final static String knownNorth = "Porto";
	final static String knownSouth = "Faro";
	final static String unknownCity = "foobar";
	
	static Collection<String> list0 = new ArrayList<String>();
	static Collection<String> list1 = new ArrayList<String>();
	static Collection<String> list2 = new ArrayList<String>();

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
 	   list1.add("http://localhost:8080/transporter-ws/endpoint");
 	   list2.add("http://localhost:8080/transporter-ws/endpoint");
 	   list2.add("http://localhost:8081/transporter-ws/endpoint");
    }

    @AfterClass
    public static void oneTimeTearDown() {

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
    public void testConstructor( ) throws JAXRException {
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list0;
    	}};
    	try {
			new BrokerPort("http://localhost:9090");;
		} catch (JAXRException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    @Test
    public void testPingZeroCompanies() throws JAXRException {
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list0;
    	}};
    	String result = "No one is there!";
		BrokerPort server = new BrokerPort("http://localhost:9090");
		assertEquals(result, server.ping("ola"));
    }
    
    @Test
    public void testPingTwoCompanies() throws JAXRException {
    	
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list2;
        	new TransporterClient("http://localhost:8080/transporter-ws/endpoint"); result = transporter1;
        	new TransporterClient("http://localhost:8081/transporter-ws/endpoint"); result = transporter2;
    		transporter1.ping("ola"); result = "UpaTransporterN: ola";
    		transporter2.ping("ola"); result = "UpaTransporterN: ola";
    	}};
    	
    	String result = "UpaTransporterN: ola\nUpaTransporterN: ola\n";
		BrokerPort server = new BrokerPort("http://localhost:9090");
		assertEquals(result, server.ping("ola"));
    }
    
    @Test (expected = UnavailableTransportFault_Exception.class)
    public void requestTransportNoCompany() throws UnavailableTransportFault_Exception, InvalidPriceFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, JAXRException {
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list0;
    	}};
		BrokerPort server = new BrokerPort("http://localhost:9090");
		server.requestTransport(knownCentre, knownCentre, 20);
    }
    
    @Test
    public void requestTransportOneCompany() throws UnavailableTransportFault_Exception, InvalidPriceFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, BadLocationFault_Exception, BadPriceFault_Exception, JAXRException, UnknownTransportFault_Exception {
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list1;
        	new TransporterClient("http://localhost:8080/transporter-ws/endpoint"); result = transporter1;
    		transporter1.requestJob(knownCentre, knownCentre, 20); result = job1;
    		job1.getJobPrice(); result = 19;
    	}};
    	
		BrokerPort server = new BrokerPort("http://localhost:9090");
		String id = server.requestTransport(knownCentre, knownCentre, 20);
		int price = server.viewTransport(id).getPrice();
		assertEquals(19, price);
    }
    
    @Test (expected = UnavailableTransportPriceFault_Exception.class)
    public void requestTransportTwoCompanyNoValidPrice() throws UnavailableTransportFault_Exception, InvalidPriceFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, BadLocationFault_Exception, BadPriceFault_Exception, JAXRException, UnknownTransportFault_Exception {
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list2;
        	new TransporterClient("http://localhost:8080/transporter-ws/endpoint"); result = transporter1;
        	new TransporterClient("http://localhost:8081/transporter-ws/endpoint"); result = transporter2;
    		transporter1.requestJob(knownCentre, knownCentre, 20); result = job1;
    		transporter2.requestJob(knownCentre, knownCentre, 20); result = job2;
    		job1.getJobPrice(); result = 21;
    		job2.getJobPrice(); result = 20;
    	}};
    	
		BrokerPort server = new BrokerPort("http://localhost:9090");
		server.requestTransport(knownCentre, knownCentre, 20);
    }
    
    @Test
    public void requestTransportTwoCompanyOneValidPrice() throws UnavailableTransportFault_Exception, InvalidPriceFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, BadLocationFault_Exception, BadPriceFault_Exception, JAXRException, UnknownTransportFault_Exception {
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list2;
        	new TransporterClient("http://localhost:8080/transporter-ws/endpoint"); result = transporter1;
        	new TransporterClient("http://localhost:8081/transporter-ws/endpoint"); result = transporter2;
    		transporter1.requestJob(knownCentre, knownCentre, 20); result = job1;
    		transporter2.requestJob(knownCentre, knownCentre, 20); result = job2;
    		job1.getJobPrice(); result = 21;
    		job2.getJobPrice(); result = 19;
    	}};
    	
		BrokerPort server = new BrokerPort("http://localhost:9090");
		String id = server.requestTransport(knownCentre, knownCentre, 20);
		int price = server.viewTransport(id).getPrice();
		assertEquals(19, price);
    }
    
    @Test
    public void requestTransportTwoCompanyTwoValidPrice() throws UnavailableTransportFault_Exception, InvalidPriceFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, BadLocationFault_Exception, BadPriceFault_Exception, JAXRException, UnknownTransportFault_Exception {
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list2;
        	new TransporterClient("http://localhost:8080/transporter-ws/endpoint"); result = transporter1;
        	new TransporterClient("http://localhost:8081/transporter-ws/endpoint"); result = transporter2;
    		transporter1.requestJob(knownCentre, knownCentre, 20); result = job1;
    		transporter2.requestJob(knownCentre, knownCentre, 20); result = job2;
    		job1.getJobPrice(); result = 19;
    		job2.getJobPrice(); result = 16;
    	}};
    	
		BrokerPort server = new BrokerPort("http://localhost:9090");
		String id = server.requestTransport(knownCentre, knownCentre, 20);
		int price = server.viewTransport(id).getPrice();
		assertEquals(16, price);
    }
    
    @Test (expected = UnknownLocationFault_Exception.class)
    public void requestTransportTwoCompanyNoLocation() throws UnavailableTransportFault_Exception, InvalidPriceFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, BadLocationFault_Exception, BadPriceFault_Exception, JAXRException, UnknownTransportFault_Exception {
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list2;
        	new TransporterClient("http://localhost:8080/transporter-ws/endpoint"); result = transporter1;
        	new TransporterClient("http://localhost:8081/transporter-ws/endpoint"); result = transporter2;
    	}};
    	
		BrokerPort server = new BrokerPort("http://localhost:9090");
		server.requestTransport(knownCentre, unknownCity, 20);
    }
    
    @Test (expected = UnavailableTransportFault_Exception.class)
    public void requestTransportTwoCompanyNoOperation() throws UnavailableTransportFault_Exception, InvalidPriceFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, BadLocationFault_Exception, BadPriceFault_Exception, JAXRException, UnknownTransportFault_Exception {
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list2;
        	new TransporterClient("http://localhost:8080/transporter-ws/endpoint"); result = transporter1;
        	new TransporterClient("http://localhost:8081/transporter-ws/endpoint"); result = transporter2;
    		transporter1.requestJob(knownCentre, knownNorth, 20); result = null;
    		transporter2.requestJob(knownCentre, knownNorth, 20); result = null;
    	}};
    	
		BrokerPort server = new BrokerPort("http://localhost:9090");
		String id = server.requestTransport(knownCentre, knownNorth, 20);
    }
    
    
    @Test (expected = UnknownTransportFault_Exception.class)
    public void viewTransportNonExistant() throws UnavailableTransportFault_Exception, InvalidPriceFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, BadLocationFault_Exception, BadPriceFault_Exception, JAXRException, UnknownTransportFault_Exception {
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list2;
        	new TransporterClient("http://localhost:8080/transporter-ws/endpoint"); result = transporter1;
        	new TransporterClient("http://localhost:8081/transporter-ws/endpoint"); result = transporter2;
    	}};
    	
		BrokerPort server = new BrokerPort("http://localhost:9090");
		server.viewTransport("1");
    }
    
    @Test
    public void viewTransportExistant() throws UnavailableTransportFault_Exception, InvalidPriceFault_Exception, UnavailableTransportPriceFault_Exception, UnknownLocationFault_Exception, BadLocationFault_Exception, BadPriceFault_Exception, JAXRException, UnknownTransportFault_Exception {
    	new Expectations() {{ 
    		uddi.list("UpaTransporter%"); result = list2;
        	new TransporterClient("http://localhost:8080/transporter-ws/endpoint"); result = transporter1;
        	new TransporterClient("http://localhost:8081/transporter-ws/endpoint"); result = transporter2;
        	transporter1.requestJob(knownCentre, knownNorth, 20); result = job1;
    		transporter2.requestJob(knownCentre, knownNorth, 20); result = job1;
    		job1.getJobPrice(); result = 15;
    	}};
    	
		BrokerPort server = new BrokerPort("http://localhost:9090");
		String id = server.requestTransport(knownCentre, knownNorth, 20);
		TransportView t = server.viewTransport("1");
		int price = t.getPrice();
		assertEquals(15, price);
    }
    
    

}
