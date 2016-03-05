package kr.dcos.common.sql.database;

import java.sql.Connection;
import java.sql.SQLException;

import kr.dcos.common.sql.exception.SqlExecutorException;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * 데이터 베이스 연결 정보를 가지고 있는 클래스
 * 
 * @author Kim Do Young
 *
 */
public class ConnectionManager {

	private BasicInfo basicInfo;
	private ConnectionInfo connectionInfo ;
	private BasicDataSource dataSource;

	public ConnectionManager(){
		dataSource = new BasicDataSource();

	}
	
	public Connection getConnection() throws SqlExecutorException {
		if(basicInfo == null) {
			throw new SqlExecutorException("basic information of database is null");
		}
		if(connectionInfo == null){
			throw new SqlExecutorException("connection information of database is null");
		}
		setDataSource();
		Connection connection =null;
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			throw new SqlExecutorException(e.getMessage());
		}
		return connection;
	}

	private void setDataSource() {
		dataSource.setUrl(basicInfo.getUrl());
		dataSource.setDriverClassName(connectionInfo.getDriver());
        dataSource.setUsername(basicInfo.getUserId());
        dataSource.setPassword(basicInfo.getPassword());
        dataSource.setDefaultAutoCommit(connectionInfo.getDefaultAutoCommit());
	}

	public ConnectionInfo getConnectionInfo() {
		return connectionInfo;
	}

	public void setConnectionInfo(ConnectionInfo connectionInfo) {
		this.connectionInfo = connectionInfo;

	}

	public BasicInfo getBasicInfo() {
		return basicInfo;
	}

	public void setBasicInfo(BasicInfo basicInfo) {
		this.basicInfo = basicInfo;
	}


	
}
