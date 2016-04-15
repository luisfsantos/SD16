package pt.upa.transporter.ws.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobStateView;
import pt.upa.transporter.ws.JobView;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


public class JobStatusIT extends AbstractTwsIT {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
		transporterClient1.clearJobs();
	}


	@Test
	public void jobStatusNull() {
		JobView jobView = transporterClient1.jobStatus("New id");
		assertNull(jobView);
	}

	@Test
	public void jobStatus() throws BadLocationFault_Exception, BadPriceFault_Exception {
		String id = transporterClient1.requestJob("Lisboa", "Beja", 50).getJobIdentifier();
		JobView jobView = transporterClient1.jobStatus(id);
		assertEquals(JobStateView.PROPOSED, jobView.getJobState());
	}

}
