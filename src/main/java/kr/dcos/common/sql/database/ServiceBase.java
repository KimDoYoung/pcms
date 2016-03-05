package kr.dcos.common.sql.database;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.sql.JdbcTable;
import kr.dcos.common.sql.SqlExecuter;
import kr.dcos.common.sql.SqlParam;
import kr.dcos.common.sql.exception.SqlExecutorException;

/**
 * 데이터베이스관련 서비스 클래스
 * 
 * @author Kim Do Young
 *
 */
public class ServiceBase implements ServiceInterface {
	
	private static Logger logger = LoggerFactory.getLogger(ServiceBase.class);
	

	@Override
	public JdbcTable select(String databaseName, String sqlId, SqlParam param)  {
		SqlExecuter se;
		Map<String,Object> paramMap = null;
		if(param != null){
			paramMap = param.getMap();
		}else{
			paramMap = null;
		}
		try {
			se = DatabaseManager.getInstance().getSqlExecutor(databaseName);
			return se.select(sqlId, paramMap);
		} catch (SqlExecutorException e) {
			logger.error(e.getMessage());
		} catch (Exception e1){
			logger.error(e1.getMessage());
		}
		return null;
		
	}

	@Override
	public int insert(String databaseName, String sqlId, SqlParam param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(String databaseName, String sqlId, SqlParam param) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(String databaseName, String sqlId, SqlParam param) {
		// TODO Auto-generated method stub
		return 0;
	}

}
