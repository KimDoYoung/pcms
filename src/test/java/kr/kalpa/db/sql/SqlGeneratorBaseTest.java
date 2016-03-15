package kr.kalpa.db.sql;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import kr.kalpa.db.DbType;
import kr.kalpa.mboard.MetaData;

public class SqlGeneratorBaseTest {


	private MetaData metaData;
	@Before
	public void setUp() throws Exception {
		String resourcePath = "/metadata/example.meta";
		URL url = MetaData.class.getResource(resourcePath);
		assertNotNull(url);
		String filePath = url.getFile();
		File file = new File(filePath);
		metaData  = new MetaData(file);		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		SqlGenerator sg = new  OracleSqlGenerator();
		String s = sg.createTable(metaData);
		System.out.println(s);
		assertTrue(s.indexOf("null")<0);
		
		s = sg.dropTable(metaData.getId());
		assertEquals(s,"DROP TABLE " + metaData.getId().toLowerCase());
		System.out.println(s);
		
	}

}
