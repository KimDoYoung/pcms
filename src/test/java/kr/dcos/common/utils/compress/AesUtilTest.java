package kr.dcos.common.utils.compress;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AesUtilTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testUuid() {
		Set<String> set = new HashSet<String>();
		for(int i=0; i< 10000; i++){
			set.add(AesUtil.uuid());
		}
		assertEquals(set.size(), 10000);
		assertEquals(AesUtil.uuid().length(),32);
	}

}
