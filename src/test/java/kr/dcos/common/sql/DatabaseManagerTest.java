package kr.dcos.common.sql;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.sql.database.DatabaseManager;
import kr.dcos.common.sql.exception.SqlExecutorException;
import kr.dcos.common.sql.exception.SqlPickerException;
import kr.dcos.common.utils.table.TableException;

public class DatabaseManagerTest {

	private static Logger logger = LoggerFactory.getLogger(DatabaseManagerTest.class);


	@Before
	public void setUp() throws Exception {
		DatabaseManager.getInstance().setup();
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Select for oracle 
	 * @throws SqlExecutorException
	 * @throws SqlPickerException
	 * @throws TableException 
	 */
	@Test  
	public void testBasic1() throws  SqlExecutorException, SqlPickerException, TableException {
		//인자가 없는 select 
		SqlExecuter se = DatabaseManager.getInstance().getSqlExecutor("oracle");
		String sqlId = "select1";
		JdbcTable table = se.selectWithStatement(sqlId,null);
		assertNotNull(table);
		assertTrue(table.getRowSize() == 5);
		
		//인자가 있는 select
		sqlId = "select2";
//		Map<String,Object> param = new HashMap<String, Object>();
		SqlParam param = new SqlParam();
		param.put("name", "Hong");
		table = se.selectWithStatement(sqlId,param);
		assertNotNull(table);
		assertTrue(table.getRowSize() == 1);		
	}

//	@Test  @Ignore
//	public void testBasic2() throws  SqlExecutorException, SqlPickerException {
//		
//		SqlExecuter se = DatabaseManager.getInstance().getSqlExecutor("airmap");
//		String sqlId = "test";
//		Table table = se.selectWithStatement(sqlId);
//		//logger.debug(table.toString());
//		assertNotNull(table);
//		assertTrue(table.getRowSize() > 0);
//	}
//	@Test  @Ignore
//	public void testBasic3() throws  SqlExecutorException, SqlPickerException {
//		
//		SqlExecuter se = DatabaseManager.getInstance().getSqlExecutor("cms");
//		String sqlId = "test";
//		Table table = se.selectWithStatement(sqlId);
//		//logger.debug(table.toString());
//		assertNotNull(table);
//		assertTrue(table.getRowSize() > 0);
//	}
//	@Test  @Ignore
//	public void testCount1() throws SqlExecutorException {
//		
//		SqlExecuter se = DatabaseManager.getInstance().getSqlExecutor("cms");
//		//count를 구한다.
//		int count1 = ConvertUtil.toInteger(se.scalarWithStatement("count_of_wcm_code"));
//		assertTrue(count1 > 0);
//		SqlParam param = new SqlParam();
//		String key = StrUtils.randomString(3);
//		param.put("locale","en");
//		param.put("grp",key);
//		param.put("code","666");
//		param.put("title","111");
//		param.put("seq",1);
//		//insert 수행
//		int result = se.executeWithStatement("insert_wcm_code", param);
//		assertTrue(result == 1);
//		int count2 = ConvertUtil.toInteger(se.scalarWithStatement("count_of_wcm_code"));
//		assertEquals(count1+1,count2);
//		
//		//delete수행
//		int count3 = se.executeWithStatement("delete_wcm_code",param);
//		assertEquals(count3,1);
//	
//		//처음구한 count와 같은지 체크
//		int count4 = ConvertUtil.toInteger(se.scalarWithStatement("count_of_wcm_code"));
//		assertEquals(count1,count4);
//	}
//	/**
//	 * 직접 sql을  수행한다.
//	 * @throws SqlExecutorException
//	 */
//	@Test @Ignore
//	public void testCountDirect() throws SqlExecutorException {
//		SqlExecuter se = DatabaseManager.getInstance().getSqlExecutor("cms");
//		String sql = "select * from WCM_CODE";
//		Table table = se.selectDirect(sql);
//		assertNotNull(table);
//		
//		int count = ConvertUtil.toInteger(se.scalarDirect("select count(*) from wcm_code"));
//		assertTrue(count > 0);
//		
//		se = DatabaseManager.getInstance().getSqlExecutor("dvddb");
//		int effectedRowCount = se.executeDirect("insert into hanjulinfo(line) values('testing cms mvc')");
//		assertEquals(effectedRowCount,1);
//	}
//	@Test  @Ignore
//	public void testKdyDb(){
//		SqlExecuter se;
//		
//		String databaseName = "kdydb";
//		String sqlId = "p1";
//		Map<String,Object> paramMap = null;
//		paramMap = null;
//		Table table = null;
//		try {
//			se = DatabaseManager.getInstance().getSqlExecutor(databaseName);
//			table =  se.select(sqlId, paramMap);
//		} catch (SqlExecutorException e) {
//			logger.error(e.getMessage());
//		}
//		assertNotNull(table);
//	}
}
