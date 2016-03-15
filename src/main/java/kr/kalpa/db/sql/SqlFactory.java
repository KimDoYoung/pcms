package kr.kalpa.db.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.sql.database.Database;
import kr.dcos.common.sql.database.DatabaseManager;
import kr.dcos.common.sql.exception.SqlExecutorException;
import kr.kalpa.db.DbType;

/**
 * 
 * Database의 종류에 따라서 SQL을 생성해 내는 SqlGenerator를 선별해서 리턴해 준다.
 * 
 * @author Kim Do Young
 *
 */
public class SqlFactory {
	private static Logger logger = LoggerFactory.getLogger(SqlFactory.class);
	
	public static SqlGenerator getSqlGenerator(String databaseName) {
		Database db;
		try {
			db = DatabaseManager.getInstance().getDatabase(databaseName);
			if(db.getBasicInfo().getDbType() == DbType.Oracle){
				return new OracleSqlGenerator();
			}
		} catch (SqlExecutorException e) {
			logger.error("{}는 올바르지 않은 또는 존재하지 않는 데이터베이스 종류입니다 :{}",databaseName, e.getMessage() ); 
			return null;
		}
		return null;
	}

}
