package pt.upa.transporter.ws.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.upa.transporter.ws.BadLocationFault_Exception;
import pt.upa.transporter.ws.BadPriceFault_Exception;
import pt.upa.transporter.ws.JobView;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class ListJobsIT extends AbstractTwsIT {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
		transporterClient1.clearJobs();
	}


	@Test
	public void listJobs() throws BadLocationFault_Exception, BadPriceFault_Exception {
		transporterClient1.requestJob("Lisboa", "Lisboa", 50);
		List<JobView> jobViewList = transporterClient1.listJobs();
		assertTrue((!jobViewList.isEmpty()));
	}
}
