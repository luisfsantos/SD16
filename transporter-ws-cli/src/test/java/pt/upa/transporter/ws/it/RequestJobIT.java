package pt.upa.transporter.ws.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;
import pt.upa.transporter.ws.cli.TransporterClient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class RequestJobIT extends AbstractTwsIT {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
		transporterClient1.clearJobs();
	}

	@Test
	public void requestJobNull() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView jobView = transporterClient1.requestJob("Porto", "Braga", 150);
		assertNull(jobView);
	}

	@Test(expected = BadPriceFault_Exception.class)
	public void requestJobBadPrice() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporterClient1.requestJob("Porto", "Braga", -1);
	}

	@Test
	public void requestJobSouthSuccess() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView jobView = transporterClient1.requestJob("Lisboa", "Beja", 50);
		assertEquals("UpaTransporter1", jobView.getCompanyName());
		assertEquals("Lisboa", jobView.getJobOrigin());
		assertEquals("Beja", jobView.getJobDestination());
		assertEquals(JobStateView.PROPOSED, jobView.getJobState());
	}

	@Test(expected = BadLocationFault_Exception.class)
	public void requestJobNorthOriginBadLocation() throws BadLocationFault_Exception, BadPriceFault_Exception {
		JobView jobView = transporterClient1.requestJob("Amsterdam", "Porto", 50);
		assertNull(jobView);
	}

}
