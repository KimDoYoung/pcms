package kr.dcos.common.sql;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import kr.dcos.common.sql.utils.ValueConverter;
import kr.dcos.common.sql.utils.ValueConverterFactory;
import kr.dcos.common.utils.table.Column;
import kr.dcos.common.utils.table.Column.DataType;
import kr.dcos.common.utils.table.Row;
import kr.dcos.common.utils.table.Table;
import kr.dcos.common.utils.table.TableException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcTableTest {
	
	private static Logger logger = LoggerFactory
			.getLogger(JdbcTableTest.class);
	

	private Connection conn_oracle , conn_mssql, conn_mysql;
	@Before 
	public void setUp() throws Exception {
//		String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
//		String url = "jdbc:sqlserver://220.76.203.236:1433;DatabaseName=DVD_DB";
//		String id = "kalpadb";
//		String pw = "kalpadb987";
//		conn_mssql = getConnection(driverName,url,id,pw);
//		assertNotNull(conn_mssql);

		String driverName = "oracle.jdbc.driver.OracleDriver";
		String url = "jdbc:oracle:thin:@KimDoYoung-PC:1521:XE";
		String id = "kdy";
		String pw = "kdy987";
		conn_oracle = getConnection(driverName,url,id,pw);
		assertNotNull(conn_oracle);
		
	}

	@After
	public void tearDown() throws Exception {
		conn_oracle.close();
	}

	private static Connection getConnection(String driverName,String url, String id, String password){
		Connection connection = null;
		try{
			Class.forName(driverName);		
			connection = DriverManager.getConnection(url, id, password);
		}catch(Exception e){
			return null;
		}
		return connection;
	}
	
	@Test 
	public void test() throws SQLException, TableException {
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select * from person";
		
		stmt = conn_oracle.createStatement();
		rs = stmt.executeQuery(sql);
		
		Table table = new JdbcTable();
		
		ResultSetMetaData rsmd = rs.getMetaData();
		//
		// Create columns
		//
		for(int i=1;i<=rsmd.getColumnCount();i++){
			String name = rsmd.getColumnLabel(i);
			ValueConverter vc  = ValueConverterFactory.getConverter(rsmd, i);
			//vc.getValue(rs, i);
			DataType dataType = vc.getType();
			if(name.equals("ID")) assertEquals(dataType.toString(),"STRING");
			if(name.equals("NAME")) assertEquals(dataType.toString(),"STRING");
			if(name.equals("HEIGHT")) assertEquals(dataType.toString(),"INTEGER");
			if(name.equals("SCORE")) assertEquals(dataType.toString(),"DOUBLE");
			if(name.equals("UPD_DT")) assertEquals(dataType.toString(),"DATE");
			Column column = new Column(name,dataType, vc.getLength(), false,false);
			
			table.appendColumn(column);
		}
		assertTrue(table.getColumnsSize() == 5);
		//
		// Create rows
		//
		while(rs.next()){
			//Row row = new SqlResultRow(table);
			Row row = table.newRow();
			for(int i=1; i<=rsmd.getColumnCount();i++){
				ValueConverter vc  = ValueConverterFactory.getConverter(rsmd, i);
				row.setValue(i-1,vc.getValue(rs, i));
			}
			table.appendRow(row);
		}
		assertTrue(table.getRowSize() == 5);
	}
	
	@Test
	public void testJdbc() throws SQLException, TableException {
		Statement stmt = null;
		ResultSet rs = null;
		String sql = "select * from person ";
		stmt = conn_oracle.createStatement();
		rs = stmt.executeQuery(sql);
		//
		//ResultSet으로 테이블 만들기
		//
		JdbcTable table = new JdbcTable();
		table.fillWithResultSet(rs);
		rs.close();
		stmt.close();
		assertNotNull(table);
		assertEquals(table.getColumnsSize(), 5);
		assertEquals(table.getRowSize(), 5);
	}
	
}
