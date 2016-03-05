package kr.dcos.common.sql;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlParamTest {
	
	private static Logger logger = LoggerFactory.getLogger(SqlParamTest.class);
	

	SqlParam param = null;
	
	@Before
	public void setUp() throws Exception {
		param = new SqlParam();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSqlParam() {
		assertNotNull(param);
		assertNotNull(param.getMap());
	}

	@Test
	public void testSqlParamMapOfStringV() {
		Map<String,Integer> map = new HashMap<String,Integer>();
		map.put("1",1);
		map.put("2",1);
		map.put("3",1);
		param.putAll(map);
		assertEquals(param.get("1"), 1);
		logger.debug(param.toString());
	}

	@Test
	public void testPutAll() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("a","aaa");
		map.put("b","bbb");
		map.put("c","ccc");
		param.putAll(map);
		assertEquals(param.size(),3);
		
	}

	@Test
	public void testPut() {
		param.put("1",1);
		param.put("1","1");
		param.put("1",new String[]{"1","2"});
		param.put("1","a");
		assertEquals((String)param.get("1"),"a");
		
	}

	@Test
	public void testGet() {
		param.put("1",1);
		assertEquals(param.get("1"),1);
	}

	@Test
	public void testGetMap() {
		param.put("1",1);
		param.put("1",1);
		assertEquals(param.getMap().size(), 1);
		param.put("2",1);
		param.put("3",1);
		assertEquals(param.getMap().size(), 3);
	}

	@Test
	public void testToString() {
		param.put("1", 1);
		param.put("2", 1);
		param.put("3", 1);
		logger.debug(param.toString());
	}

}
