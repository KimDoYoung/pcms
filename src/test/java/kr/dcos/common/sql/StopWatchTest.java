package kr.dcos.common.sql;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.utils.StopWatch;

public class StopWatchTest {

	private static Logger logger = LoggerFactory.getLogger(StopWatchTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws InterruptedException {
		StopWatch sw = new StopWatch();
		sw.start();
		Thread.sleep(1234);
		sw.stop();
		logger.debug(sw.toString());

	}
}
