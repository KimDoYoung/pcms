package kr.dcos.common.sql.database;

/**
 * 데이터베이스 접속에 대한 기본정보 클래스
 * 
 * @author Kim Do Young
 *
 */
public class BasicInfo {
	private String url;
	private String userId;
	private String password;


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
