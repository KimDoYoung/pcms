package kr.dcos.common.sql.database;

import kr.kalpa.db.DbType;

/**
 * 데이터베이스 접속에 대한 기본정보 클래스
 * 
 * @author Kim Do Young
 *
 */
public class BasicInfo {
	private DbType dbType;
	private String url;
	private String userId;
	private String password;

	public DbType getDbType() {
		return dbType;
	}
	public void setDbType(DbType dbType) {
		this.dbType = dbType;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
