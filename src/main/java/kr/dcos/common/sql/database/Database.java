package kr.dcos.common.sql.database;

import kr.dcos.common.sql.SqlExecuter;
import kr.dcos.common.sql.exception.SqlExecutorException;

/**
 * 데이터베이스 클래스
 * 
 * @author Kim Do Young
 *
 */
public class Database {
	private BasicInfo basicInfo;
	private SqlExecuter sqlExecuter= null;
	private ConnectionManager connManager = null;

	public Database() {
		basicInfo = new BasicInfo();
		sqlExecuter = new SqlExecuter();
		connManager	= new ConnectionManager();
	}

	public BasicInfo getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(BasicInfo basicInfo) {
		this.basicInfo = basicInfo;
	}

	public SqlExecuter getSqlExecuter() throws SqlExecutorException {
		sqlExecuter.setConnManager(connManager);
		return sqlExecuter;
	}

	public void setSqlExecuter(SqlExecuter sqlExecuter) {
		this.sqlExecuter = sqlExecuter;
	}

	public ConnectionManager getConnManager() {
		return connManager;
	}

	public void setConnManager(ConnectionManager connManager) {
		this.connManager = connManager;
	}


}
