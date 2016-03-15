package kr.kalpa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.sql.JdbcTable;
import kr.dcos.common.sql.SqlExecuter;
import kr.dcos.common.sql.SqlParam;
import kr.dcos.common.sql.database.DatabaseManager;
import kr.dcos.common.sql.database.ServiceBase;
import kr.dcos.common.sql.exception.SqlExecutorException;
import kr.dcos.common.utils.ConvertUtil;

public class DatabaseService extends ServiceBase  {
	
	private static Logger logger = LoggerFactory.getLogger(DatabaseService.class);

	private String dbName;
	
	public DatabaseService(){
		dbName = "wansync";
	}

	public JdbcTable select(String sqlId,SqlParam param){
		return select(dbName, sqlId, param);
	}

	public int count(String sqlId, SqlParam param) {
		JdbcTable table =  select(dbName, sqlId, param);
		if(table.getRowSize() > 0){
			 return  ConvertUtil.toInteger(table.getValue(0, 0));
		}
		return 0;
		
	}

	public int delete(String sqlId, SqlParam param) {
		return super.delete(dbName, sqlId, param);
		
	}
	public int insert(String sqlId, SqlParam param) {
		return super.delete(dbName, sqlId, param);
		
	}
	public int update(String sqlId, SqlParam param) {
		return super.delete(dbName, sqlId, param);
		
	}

	public JdbcTable directSelect(String sql) {
		SqlExecuter se;
		try {
			se = DatabaseManager.getInstance().getSqlExecutor(dbName);
			return se.selectDirect(sql);
		} catch (SqlExecutorException e) {
			logger.error(e.getMessage());
		} catch (Exception e1){
			logger.error(e1.getMessage());
		}
		return null;
	}
}
