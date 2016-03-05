package kr.dcos.common.sql;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.sql.DbTransactionItem.SqlType;
import kr.dcos.common.sql.database.ConnectionManager;
import kr.dcos.common.sql.database.DatabaseManager;
import kr.dcos.common.sql.exception.SqlExecutorException;
import kr.dcos.common.sql.exception.SqlPickerException;
import kr.dcos.common.sql.sqlpicker.SqlItem;
import kr.dcos.common.sql.sqlpicker.SqlPicker;
import kr.dcos.common.sql.utils.ValueConverter;
import kr.dcos.common.sql.utils.ValueConverterFactory;
import kr.dcos.common.utils.ClassUtil;
import kr.dcos.common.utils.ConvertUtil;
import kr.dcos.common.utils.StopWatch;
import kr.dcos.common.utils.table.Column;
import kr.dcos.common.utils.table.Column.DataType;
import kr.dcos.common.utils.table.Row;
import kr.dcos.common.utils.table.Table;
import kr.dcos.common.utils.table.TableException;

/**
 * sql을 수행한다. <br>
 * sqlPicker에서 sql을 가져온다. <br>
 * 수행함수는 3종류가 있다. <br>
 * 1. select : Table을 리턴한다<br>
 * 2. execute  : insert,update,delete : 반영된 레코드 갯수를 리턴한다.<br> 
 * 3. queryInteger : count(*)와 같이 수행된 함수에 대해서 integer숫자를 리턴한다.<br>
 * 4. queryDouble  : query후 double형으로 숫자를 리턴한다.<br>
 * 
 * @author Kim Do Young
 *
 */
public class SqlExecuter {
	private static Logger logger = LoggerFactory.getLogger(SqlExecuter.class);

	private ConnectionManager connManager;
	private SqlPicker sqlPicker;
	private StopWatch stopWatch;
	public SqlExecuter(){
		sqlPicker = new SqlPicker();
		stopWatch = new StopWatch();
	}

	/**
	 * load sql xml file
	 * @param sqlXmlFile
	 * @throws SqlPickerException 
	 */
	public void loadFromResourceFile(File file) throws SqlPickerException  {
		if(file != null){
			sqlPicker.load(file);
		}
	}
	/**
	 * load sql xml files in folder
	 * @param folder
	 * @throws SqlPickerException 
	 */
	public void loadFromResourceFolder(File folder) throws SqlPickerException  {
		
		if(folder == null) return ;
		
		if(folder.isDirectory()){
			for (final File fileEntry : folder.listFiles()) {
		        if (fileEntry.isDirectory()) {
		        	loadFromResourceFolder(fileEntry);
		        } else {
		            sqlPicker.load(fileEntry.getAbsolutePath());
		        }
		    }
		}
	}	
	/**
	 * count(*)와 같이 숫자값 하나만 조회될 때 사용한다.
	 * @param sqlId
	 * @return
	 * @throws SqlExecutorException
	 * @throws TableException
	 * @throws SqlPickerException
	 */
	public Object scalarWithStatement(String sqlId) throws SqlExecutorException, TableException, SqlPickerException {
		return scalarWithStatement(sqlId,null);
	}

	/**
	 * count(*)와 같이 숫자값 하나만 조회될 때 사용한다.
	 * @param sqlId
	 * @param param
	 * @return
	 * @throws SqlExecutorException
	 * @throws SqlPickerException 
	 * @throws TableException 
	 */
	public Object scalarWithStatement(String sqlId, SqlParam param) throws SqlExecutorException, TableException, SqlPickerException  {
		JdbcTable table = selectWithStatement(sqlId,param);
		if(table.getRowSize() == 1 && table.getColumnsSize() == 1){
			return table.getValue(0, 0);
		}else{
			throw new SqlExecutorException(sqlId + " is not proper for queryInteger, result row and col size should be 1 ");
		}
	}
	
	public JdbcTable selectWithStatement(String sqlId) throws SqlExecutorException, TableException, SqlPickerException {
		return selectWithStatement(sqlId,null);
	}

	public JdbcTable selectWithStatement(String sqlId, SqlParam param) throws SqlExecutorException, TableException, SqlPickerException {
		
		String sql = sqlPicker.sqlString(sqlId, param.getMap());
		logger.debug(sqlId+":["+sql+"]");
		return selectDirect(sql);
	}

	public int executeWithStatement(String sqlId, SqlParam param) throws SqlExecutorException {
		return executeWithStatement(sqlId, param.getMap());
	}

	public int executeWithStatement(String sqlId, Map<String, Object> param)
			throws SqlExecutorException {
		String sql;
		try {
			sql = sqlPicker.sqlString(sqlId, param);
		} catch (SqlPickerException e) {
			throw new SqlExecutorException(sqlId, sqlId, e.getMessage());
		}
		logger.debug(sqlId+":["+sql+"]");
		return executeDirect(sql);
	}
	public void setConnManager(ConnectionManager connManager) {
		this.connManager = connManager;
	}

	/**
	 * sql 문장을 직접 인자로 넘겨서 수행한다.
	 * @param sql
	 * @return
	 * @throws SqlExecutorException 
	 * @throws TableException 
	 */
	public JdbcTable selectDirect(String sql) throws SqlExecutorException, TableException {
		Connection connection = null;
		Statement stmt = null;
		ResultSet rs = null;
		JdbcTable table = null;
		
		try {
			stopWatch.start();
			connection = connManager.getConnection();
			stmt = connection.createStatement();
			// execute Query
			rs = stmt.executeQuery(sql);

			table = createTableWithResultSet(rs);
		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new SqlExecutorException(e.getMessage());
		} catch (JdbcTableException e) {
			logger.error(e.getMessage());
			throw new SqlExecutorException(e.getMessage());
		}finally{
			try {if(rs!=null)rs.close();} catch (SQLException e) {}
			try {if(stmt != null) stmt.close();} catch (SQLException e) {}
			try { connection.close(); } catch(Exception e) { }
		}
		stopWatch.stop();
		logger.debug(" execute time :"+stopWatch.toString());
		return table;
	}

	private JdbcTable createTableWithResultSet(ResultSet rs) throws  TableException {
		
		JdbcTable table = new JdbcTable();
		table.fillWithResultSet(rs);
		return table;
		
//		ResultSetMetaData rsmd = rs.getMetaData();
//
//		// Create columns
//		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//			String name = rsmd.getColumnLabel(i);
//			ValueConverter vc = ValueConverterFactory.getConverter(rsmd, i);
//			DataType dataType = vc.getType();
//			int length = vc.getLength();
//			Column column = new Column(name, dataType, length, false, false);
//			table.appendColumn(column);
//		}
//		// Data retrieve
//		while (rs.next()) {
//			Row row = table.newRow();
//			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
//				ValueConverter vc = ValueConverterFactory.getConverter(rsmd, i);
//				String name = rsmd.getColumnLabel(i);
//				row.setByName(name, vc.getValue(rs, i));
//			}
//			 table.addRow(row);
//		}
//		return table;
	}

	public Object scalarDirect(String sql) throws SqlExecutorException, TableException {
		JdbcTable table = selectDirect(sql);
		if(table.getRowSize() == 1 && table.getColumnsSize() == 1){
			return table.getValue(0, 0);
		}else{
			throw new SqlExecutorException("Maybe sql is not proper for integer, result row and col size should be 1 ");
		}
	}
	/**
	 * 직접 sql을 인자로 받아서 수행한다. sql은 Insert update delete 문장이어야한다.
	 * @param sqlString
	 * @return
	 * @throws SqlExecutorException 
	 */
	public int executeDirect(String sqlString) throws SqlExecutorException {
		Connection connection = null;
		Statement stmt = null;
		int effectedRecordCount = 0;
		stopWatch.start();
		try {
			connection = connManager.getConnection();
			connection.setAutoCommit(true);
			stmt = connection.createStatement();
			// execute Query
			effectedRecordCount = stmt.executeUpdate(sqlString);

		} catch (SQLException e) {
			logger.error(e.getMessage());
			throw new SqlExecutorException(sqlString,e.getMessage());
		} finally {
			try {stmt.close();} catch (SQLException e) {}
			try { connection.close(); } catch(Exception e) { }
		}
		stopWatch.stop();
		return effectedRecordCount;
	}

	/**
	 * sqlId를 찾아서 preparedStatement 방식으로 sql을 만들어서 수행한다
	 * 
	 * @param sqlId
	 * @return  effected row count
	 * @throws SqlExecutorException
	 * @throws SqlPickerException
	 */
	public Integer execute( String sqlId,Map<String, Object> param) throws SqlExecutorException  {

		PreparedStatement pstmt = null;
		SqlItem sqlItem = null;
		int result = 0;
		String sql = null;
		Connection conn = null;
		sqlItem = sqlPicker.getItem(sqlId);
		if(sqlItem == null ){
			throw new SqlExecutorException(sqlId + " is not exist");
		}
		try {
			stopWatch.start();
			conn = connManager.getConnection();
			sql = sqlPicker.getPreparedStatment(sqlId, param);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql); // SQL문 전송 객체 생성

			for (int i = 1; i <= sqlItem.preparedNameCount(); i++) {
				String name = sqlItem.getPreparedName(i-1);
				JdbcDataType dataType = getPreparedType(param, name);
				
				if (dataType == JdbcDataType.String) {
					pstmt.setString(i, param.get(name).toString());
				} else if (dataType == JdbcDataType.Integer) {
					pstmt.setInt(i, ConvertUtil.toInteger(param.get(name)));
				} else if (dataType == JdbcDataType.Double) {
					pstmt.setDouble(i, ConvertUtil.toDouble(param.get(name)));
				} else {
					throw new SqlExecutorException(sqlId, sql, name	+ " hava not definded data type");
				}

			}

			result = pstmt.executeUpdate(); // 질의문 수행
			stopWatch.stop();
			logger.debug(" sqlId:" + sqlId + ", execute time :"+stopWatch.toString());			
		} catch (Exception e) {
			throw new SqlExecutorException(sqlId, sql, e.getMessage());
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();
			} catch (Exception ex) {
			}
		}
		return result;	
	}

	/**
	 * dbName에 연결할 수 있는 connection을 리턴한다.
	 * @param dbName
	 * @return
	 * @throws SqlExecutorException
	 */
	public Connection getConnection(String dbName) throws SqlExecutorException {
		return DatabaseManager.getInstance().getConnection(dbName);
	}
	/**
	 * prepared Statement를 사용 scalar(숫자) 값을 리턴한다.
	 * Casting 또는 ConvertUtil를 이용하여 integer나 double로 변경하여 사용할 수있다
	 * @param sqlId
	 * @param param
	 * @return
	 * @throws SqlExecutorException 
	 */
	public Object scalar(String sqlId, Map<String, Object> param) throws SqlExecutorException {
		JdbcTable table = select(sqlId,param);
		if(table != null ){
			return table.getValue(0, 0);
		}
		return null;
	}
	/**
	 * sqlId로 찾아서 prepared statement를 만들고 수행한다. 그 결과는 Table로 리턴한다.
	 * @param sqlId
	 * @param param
	 * @return
	 * @throws SqlExecutorException 
	 */
	public JdbcTable select(String sqlId, Map<String, Object> param) throws SqlExecutorException {

		PreparedStatement pstmt = null;
		SqlItem sqlItem = null;
		JdbcTable resultTable = null;
		String sql = null;
		Connection conn = null;
		sqlItem = sqlPicker.getItem(sqlId);
		if(sqlItem == null ){
			throw new SqlExecutorException(sqlId + " is not exist");
		}
		try {
			stopWatch.start();
			conn = connManager.getConnection();
			sql = sqlPicker.getPreparedStatment(sqlId, param);
			conn.setAutoCommit(true);
			pstmt = conn.prepareStatement(sql); // SQL문 전송 객체 생성
			pstmt = setPreparedStatmentWithValues(pstmt, sqlItem, param);
//
//			for (int i = 1; i <= sqlItem.preparedNameCount(); i++) {
//				String name = sqlItem.getPreparedName(i-1);
//				PreparedDataType dataType = getPreparedType(param, name);
//				
//				if (dataType == PreparedDataType.String) {
//					pstmt.setString(i, param.get(name).toString());
//				} else if (dataType == PreparedDataType.Integer) {
//					pstmt.setInt(i, ConvertUtil.toInteger(param.get(name)));
//				} else if (dataType == PreparedDataType.Double) {
//					pstmt.setDouble(i, ConvertUtil.toDouble(param.get(name)));
//				} else {
//					throw new SqlExecutorException(sqlId, sql, name	+ " hava not definded data type");
//				}
//			}
			ResultSet rs = pstmt.executeQuery() ;
			resultTable = createTableWithResultSet(rs);
			stopWatch.stop();
			logger.debug(" sqlId:" + sqlId + ", execute time :"+stopWatch.toString());
		} catch (Exception e) {
			throw new SqlExecutorException(sqlId, sql, e.getMessage());
		} finally {
			try {
				if (pstmt != null){	pstmt.close();}
				if (conn != null){	conn.close();}
			} catch (Exception ex) {
			}
		}
		return resultTable;	
	}
	/**
	 * tranInfo에 담겨있는 트랜잭션을 모두 수행한다.
	 * 
	 * @param tranInfo
	 * @return
	 * @throws SqlExecutorException
	 */
	public boolean execTransaction(DbTransactionInfo tranInfo) throws SqlExecutorException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sqlId = null;
		String sql = null;
		SqlItem sqlItem = null; 
		
		try 
		{
			conn = connManager.getConnection();	
			conn.setAutoCommit(false);
			
			for(int i=0;i<tranInfo.getCount();i++){
				DbTransactionItem item = tranInfo.get(i);
				
				if (item.getType() == SqlType.SqlId) {
					Object param = item.getParam();
					sqlId = item.getSqlId();
					sqlItem = sqlPicker.getItem(sqlId);
					if (sqlItem == null) {
						throw new SqlExecutorException(sqlId + " is not found");
					}

					sql = sqlPicker.getPreparedStatment(sqlId, param);
					pstmt = conn.prepareStatement(sql);
					setPreparedStatmentWithValues(pstmt, sqlItem, param);

					item.setPrepareStatement(pstmt);
					pstmt.executeUpdate();
				} else if (item.getType() == SqlType.SqlItself) {
					sql = item.getSql();
					pstmt = conn.prepareStatement(sql);
					pstmt.executeUpdate();
				}
			}
			conn.commit();
			return true;
		} 
		catch (SQLException e) 
		{
			logger.error(e.getMessage());
			try{
				conn.rollback();
				return false;
			}catch(SQLException e1){
				throw new SqlExecutorException(sqlId, sql, e1.getMessage());
			}
		} catch (SqlPickerException e) {
			logger.error(e.getMessage());
			try{
				conn.rollback();
				return false;
			}catch(SQLException e1){
				throw new SqlExecutorException(sqlId, sql, e1.getMessage());
			}
		} 
		finally 
		{
			for(int j=0;j<tranInfo.getCount();j++){
				pstmt = tranInfo.get(j).getPreparedStatement();
				
				try {
					if(pstmt != null){pstmt.close();}
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error(e.getMessage());
				}
			}
 		}
	}
	private PreparedStatement setPreparedStatmentWithValues(PreparedStatement pstmt,
			SqlItem sqlItem, Object param) throws SqlExecutorException {
		if(param == null) return pstmt;

		try {
			for (int i = 1; i <= sqlItem.preparedNameCount(); i++) {
				String name = sqlItem.getPreparedName(i - 1); 
					
				Object object = getValue(param,name);
				if(object == null) throw new SqlExecutorException(name + " value is null");
				JdbcDataType dataType = getPreparedType(param, name);
				
				if (dataType == JdbcDataType.String) {
					pstmt.setString(i, object.toString());
				} else if (dataType == JdbcDataType.Integer) {
					pstmt.setInt(i, ConvertUtil.toInteger(object));
				} else if (dataType == JdbcDataType.Double) {
					pstmt.setDouble(i, ConvertUtil.toDouble(object));
				} else if (dataType == JdbcDataType.UnKnown){
					logger.error(name + " data type is unknown");
				}
			}
		} catch (SQLException e) {
			throw new SqlExecutorException(e.getMessage());
		}
		return pstmt;
	}

	@SuppressWarnings("unchecked")
	private Object getValue(Object param, String name) {
		if(ClassUtil.isPrimitive(param)){
			return param;
		}else if(param instanceof Map){
			return ((Map<String,Object>)param).get(name);
		}else if(param instanceof SqlParam){
			return ((SqlParam)param).get(name);
		}else {
			return ClassUtil.getValueFromClass(param, name);
		}
	}

	/**
	 * param에서 name을 key로 찾아서 param안에 정의된 타입이 jdbc 타입으로는 무엇인지 매칭 시켜 
	 * jdbc타입으로 리턴해준다.
	 * @param param
	 * @param fieldName
	 * @return PreparedDataType JdbcType
	 * @throws SqlExecutorException 
	 */
	@SuppressWarnings("unchecked")
	private JdbcDataType getPreparedType(Object param,
			String fieldName) throws SqlExecutorException {
		Object o = null;
		
		if(ClassUtil.isPrimitive(param)){
			o = param;
		}else if(param instanceof Map){
			Map<String,Object> map = (Map<String,Object>)param;
			o = map.get(fieldName);
		}else if(param instanceof SqlParam){
			o =  ((SqlParam)param).get(fieldName);
		}else {
			String typeName = ClassUtil.getTypeFromClass(param, fieldName);
			return JdbcDataType.fromString(typeName);
		}
		
		if(o == null){
			throw new SqlExecutorException(fieldName + " is not exist in parameter");
		}else if(o instanceof String){
			return JdbcDataType.String;
		}else if(o instanceof Integer){
			return JdbcDataType.Integer;
		}else if(o instanceof Double){
			return JdbcDataType.Double;		
		}else if(o instanceof java.util.Date){
			return JdbcDataType.Date;					
		}else {
			return JdbcDataType.UnKnown;
		}
	}
}
