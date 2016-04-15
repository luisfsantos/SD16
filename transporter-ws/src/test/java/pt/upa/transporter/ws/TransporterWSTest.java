package pt.upa.transporter.ws;

import org.junit.*;
import pt.upa.transporter.domain.Job;
import pt.upa.transporter.domain.JobState;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 *  Unit Test example
 *  
 *  Invoked by Maven in the "test" life-cycle phase
 *  If necessary, should invoke "mock" remote servers 
 */
public class TransporterWSTest {

    // static members
	private static TransporterPort transporterPort2;

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {
		transporterPort2 = new TransporterPort("UpaTransporter2");
    }

    @AfterClass
    public static void oneTimeTearDown() {
		transporterPort2 = null;
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


	@Test
	public void ping() {
		TransporterPort transporterPort = new TransporterPort("UpaTransporter1");
		String ping = transporterPort.ping("B");
		assertEquals(ping, "UpaTransporter1: B");
	}

	@Test
	public void requestJobNull() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView jobView = transporterPort2.requestJob("Porto", "Braga", 150);
		assertNull(jobView);
	}

	@Test(expected = BadPriceFault_Exception.class)
	public void requestJobBadPrice() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporterPort2.requestJob("Porto", "Braga", -1);
	}

	@Test
	public void requestJobNorthSuccess() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView jobView = transporterPort2.requestJob("Porto", "Lisboa", 50);
		assertEquals("UpaTransporter2", jobView.getCompanyName());
		assertEquals("Porto", jobView.getJobOrigin());
		assertEquals("Lisboa", jobView.getJobDestination());
		assertEquals(JobStateView.PROPOSED, jobView.getJobState());
	}

	@Test
	public void requestJobNorthOriginBadLocation() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView jobView = transporterPort2.requestJob("Setubal", "Porto", 50);
		assertNull(jobView);
	}

	@Test
	public void requestJobNorthDestinationBadLocation() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView jobView = transporterPort2.requestJob("Porto", "Setubal", 50);
		assertNull(jobView);
	}

	@Test
	public void requestJobSouthOriginBadLocation() throws BadLocationFault_Exception, BadPriceFault_Exception {
		TransporterPort transporterPort = new TransporterPort("UpaTransporter1");
		JobView jobView = transporterPort.requestJob("Porto", "Setubal", 50);
		assertNull(jobView);
	}

	@Test
	public void requestJobSouthDestinationBadLocation() throws BadLocationFault_Exception, BadPriceFault_Exception {
		TransporterPort transporterPort = new TransporterPort("UpaTransporter1");
		JobView jobView = transporterPort.requestJob("Setubal", "Porto", 50);
		assertNull(jobView);
	}

	@Test(expected = BadJobFault_Exception.class)
	public void decideJobNewId() throws BadJobFault_Exception {
		transporterPort2.decideJob("new id", true);
	}

	@Test
	public void decideJobAcceptSuccess() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		String id = transporterPort2.requestJob("Porto", "Porto", 50).getJobIdentifier();
		JobView jobView = transporterPort2.decideJob(id, true);
		assertEquals(JobStateView.ACCEPTED, jobView.getJobState());
	}

	@Test
	public void decideJobRejectSuccess() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		String id = transporterPort2.requestJob("Porto", "Porto", 50).getJobIdentifier();
		JobView jobView = transporterPort2.decideJob(id, false);
		assertEquals(JobStateView.REJECTED, jobView.getJobState());
	}

	@Test
	public void jobStatusNull() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView jobView = transporterPort2.jobStatus("New id");
		assertNull(jobView);
	}

	/*
	@Test
	public void jobStatusProposed() throws BadLocationFault_Exception, BadPriceFault_Exception {
		TransporterPort transporterPort = new TransporterPort("UpaTransporter2");
		String id = transporterPort.requestJob("Porto", "Porto", 50).getJobIdentifier();
		JobView jobView = transporterPort.jobStatus(id);
		assertEquals(JobStateView.PROPOSED, );
	}
	*/

	@Test
	public void listJobsEmpty() {
		List<JobView> jobViewList = transporterPort2.listJobs();
		assertTrue(jobViewList.isEmpty());
	}


	@Test
	public void listJobs() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporterPort2.requestJob("Porto", "Porto", 50);
		List<JobView> jobViewList = transporterPort2.listJobs();
		assertTrue((!jobViewList.isEmpty()));
	}

	@Test
	public void clearJobsEmpty() {
		transporterPort2.clearJobs();
	}

	@Test
	public void clearJobs() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporterPort2.requestJob("Porto", "Porto", 50);
		transporterPort2.clearJobs();
		assertTrue(transporterPort2.listJobs().isEmpty());
	}











}