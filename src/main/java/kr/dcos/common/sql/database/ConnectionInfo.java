package kr.dcos.common.sql.database;

/**
 * DB연결에 필요한 정보들을 가지고 있는 클래스
 * 
 * @author Kim Do Young
 *
 */
public class ConnectionInfo {
	private String url;
	private String driver;
	private Integer minConnection;
	private Integer maxConnectionSize;
	private Integer loginTimeout;
	private Integer maxWait;
	private boolean defaultAutoCommit;

	public ConnectionInfo() {
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public Integer getMinConnection() {
		return minConnection;
	}

	public void setMinConnection(Integer minConnection) {
		this.minConnection = minConnection;
	}

	public Integer getMaxConnectionSize() {
		return maxConnectionSize;
	}

	public void setMaxConnectionSize(Integer maxConnectionSize) {
		this.maxConnectionSize = maxConnectionSize;
	}

	public Integer getLoginTimeout() {
		return loginTimeout;
	}

	public void setLoginTimeout(Integer loginTimeout) {
		this.loginTimeout = loginTimeout;
	}

	public Integer getMaxWait() {
		return maxWait;
	}

	public void setMaxWait(Integer maxWait) {
		this.maxWait = maxWait;
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean getDefaultAutoCommit() {
		return defaultAutoCommit;
	}

	public void setDefaultAutoCommit(boolean defaultAutoCommit) {
		this.defaultAutoCommit = defaultAutoCommit;
	}
}