package kr.dcos.common.sql.database;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SqlDateTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		   java.util.Date utilDate1 = new java.util.Date();
		    java.sql.Date sqlDate1 = new java.sql.Date(utilDate1.getTime());
		    System.out.println("utilDate:" + utilDate1);
		    System.out.println("sqlDate:" + sqlDate1);
		    
		    java.util.Calendar cal = Calendar.getInstance();
		    java.util.Date utilDate = new java.util.Date(); // your util date
		    cal.setTime(utilDate);
		    cal.set(Calendar.HOUR_OF_DAY, 0);
		    cal.set(Calendar.MINUTE, 0);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.MILLISECOND, 0);    
		    java.sql.Date sqlDate = new java.sql.Date(cal.getTime().getTime()); // your sql date
		    System.out.println("utilDate:" + utilDate1);
		    System.out.println("sqlDate:" + sqlDate);
	}

}
