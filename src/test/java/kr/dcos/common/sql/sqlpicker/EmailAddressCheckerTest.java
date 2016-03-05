package kr.dcos.common.sql.sqlpicker;

import static org.junit.Assert.*;

import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class EmailAddressCheckerTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	private boolean isValidEmailAddress(String emailAddress){
		if(emailAddress == null) return false;
		String regex ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(emailAddress).matches();
		
	}
	@Test
	public void eMailAddressCheck(){
		assertTrue(isValidEmailAddress("dykim@kalpa.co.kr"));
		assertTrue(isValidEmailAddress("kdy987@naver.com"));
		assertTrue(isValidEmailAddress("kdy987@google.com"));
		assertFalse(isValidEmailAddress("1"));
		assertFalse(isValidEmailAddress("1@1"));
		assertFalse(isValidEmailAddress(null));
		assertFalse(isValidEmailAddress(""));
	}

}
