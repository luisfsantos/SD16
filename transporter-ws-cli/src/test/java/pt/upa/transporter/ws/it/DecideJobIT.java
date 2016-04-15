package pt.upa.transporter.ws.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pt.upa.transporter.ws.*;

import static org.junit.Assert.assertEquals;

public class DecideJobIT extends AbstractTwsIT {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	@Test
	public void test() {

		// assertEquals(expected, actual);
		// if the assert fails, the test fails
	}

	@Test(expected = BadJobFault_Exception.class)
	public void decideJobNewId() throws BadJobFault_Exception {
		transporterClient1.decideJob("new id", true);
	}

	@Test
	public void decideJobAcceptSuccess() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		String id = transporterClient1.requestJob("Beja", "Lisboa", 50).getJobIdentifier();
		JobView jobView = transporterClient1.decideJob(id, true);
		assertEquals(JobStateView.ACCEPTED, jobView.getJobState());
	}

	@Test
	public void decideJobRejectSuccess() throws BadLocationFault_Exception, BadPriceFault_Exception, BadJobFault_Exception {
		String id = transporterClient1.requestJob("Beja", "Lisboa", 50).getJobIdentifier();
		JobView jobView = transporterClient1.decideJob(id, false);
		assertEquals(JobStateView.REJECTED, jobView.getJobState());
	}
}
