package kr.dcos.common.sql.sqlpicker;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PrimitiveCheckerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	public boolean isPrimitiveType(Object o){
		if(o instanceof String ||
		   o instanceof Double ||
		   o instanceof Boolean ||
		   o instanceof Character ||
		   o instanceof Integer ) {
			return true;
		}
		return false;
	}
	@Test
	public void test() {
		
		assertTrue(isPrimitiveType(1));
		assertTrue(isPrimitiveType(1.5));
		assertTrue(isPrimitiveType("abc"));
		assertTrue(isPrimitiveType(true));
		assertTrue(isPrimitiveType('a'));
	}


}
