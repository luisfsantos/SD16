package pt.upa.transporter.ws.it;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class PingIT extends AbstractTwsIT {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}


	@Test
	public void ping() {
		String ping = transporterClient1.ping("B");
		assertEquals(ping, "UpaTransporter1: B");
	}
}
