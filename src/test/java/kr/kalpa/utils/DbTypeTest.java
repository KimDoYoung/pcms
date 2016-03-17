package kr.kalpa.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.kalpa.db.DbType;

public class DbTypeTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		assertNotNull(DbType.MySql);
		assertNotNull(DbType.Oracle);
		assertNotNull(DbType.Microsoft_Sql_Server);
		assertEquals(DbType.typeOf("oracle"), DbType.Oracle);
	}

}
