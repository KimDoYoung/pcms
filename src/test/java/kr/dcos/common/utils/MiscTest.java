package kr.dcos.common.utils;

import static org.junit.Assert.*;

import org.apache.commons.lang3.math.NumberUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MiscTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertTrue(isInteger("10"));
		assertTrue(isInteger("1"));
		assertTrue(isInteger("2"));
		assertTrue(isInteger("999"));
		assertTrue(isInteger("-999"));
		assertFalse(isInteger("1.0"));
		assertFalse(isInteger("-1.0"));
		//double
		assertTrue(isDouble("1.0"));
		assertTrue(isDouble("1L"));
		assertTrue(isDouble("0.3"));
		assertTrue(isDouble("-0.3"));
		assertTrue(isDouble("-1L"));
		
		assertFalse(isDouble("1"));
		assertFalse(isDouble("-1"));
	}
	public  boolean isInteger(String s) {
		int radix = 10;
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
	public boolean isDouble(String s){
		try {
			if(isInteger(s)) return false;
			return NumberUtils.isNumber(s);			
		} catch (Exception e) {
			return false;
		}
	}
	@Test @Ignore
	public void test1() {
		Integer i = 10;
		Double d = 10.0;
		assertTrue( (double)i == (double)d);
		Object o = 10;
		assertTrue((Integer)o == 10);
		
	}
}
