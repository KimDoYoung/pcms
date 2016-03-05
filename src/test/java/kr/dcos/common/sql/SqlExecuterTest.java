package kr.dcos.common.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.print.Book;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.sql.database.DatabaseManager;
import kr.dcos.common.sql.exception.SqlExecutorException;
import kr.dcos.common.utils.ConvertUtil;
import kr.dcos.common.utils.table.Row;
import kr.dcos.common.utils.table.Table;

public class SqlExecuterTest {
	
	private static Logger logger = LoggerFactory
			.getLogger(SqlExecuterTest.class);
	

	SqlExecuter se = null; 
//	ConnectionManager cm = null;
//	BasicInfo bi = null;
//	ConnectionInfo ci = null;
	@Before
	public void setUp() throws Exception {
		DatabaseManager.getInstance().setup();
//		se = new SqlExecuter();
//		cm = new ConnectionManager();
//		bi = new BasicInfo();
//		ci = new ConnectionInfo();
//
//		bi.setUrl("jdbc:sqlserver://220.76.203.236:1433;DatabaseName=KDYDB");
//		bi.setUserId("kalpadb");
//		bi.setPassword("kalpadb987");
//		
//		ci.setUrl("jdbc:sqlserver://220.76.203.236:1433;DatabaseName=KDYDB");
//		ci.setDriver("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		
//		cm.setBasicInfo(bi);
//		cm.setConnectionInfo(ci);
//		se.setConnManager(cm);
//		File file  = SqlPicker.getFileFromResourcePath("/sql1.xml");
//		se.loadFromResourceFile(file);
		se = DatabaseManager.getInstance().getSqlExecutor("kdydb");
	}

	@After
	public void tearDown() throws Exception {
		

	}

//	@Test
//	public void testSqlExecuter() throws SqlExecutorException {
//		Map<String,Object> param = new HashMap<String,Object>();
//		param.put("title", StrUtils.randomString(10));
//		param.put("isbn", StrUtils.randomString(5));
//		int i = se.execute("p1", param);
//		assertEquals(i,1);
//	}
//
//	@Test
//	public void testSelect1() throws SqlExecutorException {
//		Map<String,Object> param = new HashMap<String,Object>();
//		param.put("title", "제국");
//		param.put("id",48);
//		Table table =  se.select("p2", param);
//		assertNotNull(table);
//		logger.debug("rcd count:" +table.rowSize());
//
//	}
//
//	@Test
//	public void testScalarString() throws SqlExecutorException {
//		Map<String,Object> param = new HashMap<String,Object>();
//		param.put("title", "제국");
//		param.put("id",48);
//		Integer cnt =  (Integer) se.scalar("p3", param);
//		assertTrue(cnt>0);
//		logger.debug("rcd count:" +cnt.toString());
//		
//	}
//
//	@Test
//	public void testSelectWithStatementString() throws SqlExecutorException {
//		Map<String,Object> param = new HashMap<String,Object>();
//		param.put("title", "제국");
//		param.put("id",48);
//		Integer cnt =  (Integer) se.scalar("p4", param);
//		assertTrue(cnt>0);
//		logger.debug("rcd count:" +cnt.toString());
//		
//		Table table = se.select("p5", param);
//		assertEquals(table.rowSize(),(int)cnt);
//	}

	@Test @Ignore
	public void testSelectTitle() throws SqlExecutorException {
//		Table table =  se.select("p1", null);
//		assertNotNull(table);
//		for (String title : table.getHeaders()) {
//			System.out.println(title);
//			
//		}
//		for (Row row : table.getRows()) {
//			for (String title : table.getHeaders()) {
//				Object o = row.getByName(title);
//				System.out.println(ConvertUtil.toString(o));
//			}
//		}
		//String s = table.getValue(0,"id");
	}
	@Test
	public void transactionTest1(){
		
//		DbTransactionInfo tranInfo = new DbTransactionInfo();
//		Map<String,Object> map = new HashMap<String,Object>();
//		map.put("title", "789");
//		map.put("isbn", "123-456");
//		tranInfo.add("insert1",map);
//		tranInfo.add("delete1",map);
//		
//		try {
//			boolean b = se.execTransaction(tranInfo);
//			assertTrue(b);
//		} catch (SqlExecutorException e) {
//			logger.error(e.getMessage());
//		}
	}
	/**
	 * 
	 * SqlParam을 넘긴다.
	 */
	@Test
	public void transactionTest2(){
		
//		DbTransactionInfo tranInfo = new DbTransactionInfo();
//		
//		SqlParam sqlParam = new SqlParam();
//		sqlParam.put("title", "123");
//		sqlParam.put("isbn", "123-456");
//
//		tranInfo.add("insert1",sqlParam);
//		tranInfo.add("delete1",sqlParam);
//
//		try {
//			boolean b = se.execTransaction(tranInfo);
//			assertEquals(b,true);
//		} catch (SqlExecutorException e) {
//			logger.error(e.getMessage());
//		}
	}
	/**
	 * 
	 * POJO 타입의 class를 넘긴다.
	 */
	@Test
	public void transactionTest3(){
		
//		DbTransactionInfo tranInfo = new DbTransactionInfo();
//		
//		Book book = new Book();
//		book.setTitle("we are the world");
//		book.setIsbn("123456");
//
//		tranInfo.add("insert1",book);
//		tranInfo.add("delete1",book);
//
//		try {
//			boolean b = se.execTransaction(tranInfo);
//			assertEquals(b,true);
//		} catch (SqlExecutorException e) {
//			logger.error(e.getMessage());
//		}


	}
	/**
	 * 
	 * xml에서 sqlId로 찾아서 sql을 만드는 것과
	 * 직접 sql를 집어 넣어서 tranInfo를 채운 후 돌리는 것
	 */
	@Test
	public void transactionTest4(){
		
//		DbTransactionInfo tranInfo = new DbTransactionInfo();
//		
//		Book book = new Book();
//		book.setTitle("we are the world");
//		book.setIsbn("123456");
//
//		tranInfo.add("insert1",book);
//		tranInfo.add("delete from book where isbn='123456'");
//
//		try {
//			boolean b = se.execTransaction(tranInfo);
//			assertEquals(b,true);
//		} catch (SqlExecutorException e) {
//			logger.error(e.getMessage());
//		}


	}
}
