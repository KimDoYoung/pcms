package kr.dcos.common.sql.sqlpicker;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.LoggerFactory;



public class InvalidXmlTagTest {
	
	
	

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	private boolean isValidEmailAddress(String emailAddress){
		String regex ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(emailAddress).matches();
		
	}
	@Test
	public void eMailAddressCheck(){
		assertTrue(isValidEmailAddress("dykim@kalpa.co.kr"));
	}
}
