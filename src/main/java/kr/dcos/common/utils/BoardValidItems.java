package kr.dcos.common.utils;

/**
 * Board용 Validate할 항목들을 가지고 있다. <br>
 * Json을 받아들여서 값을 채울 수 있어야한다.<br>
 * 이 클래스의 프로퍼티들이 json문자열로 변경될 수 있어야한다.<br>
 * Board생성에서 각 etc필드의 validate문자열을 json으로 사용하기로 한다.<br>
 * 
 * @author Kim Do Young
 *
 */
public class BoardValidItems {
	private boolean notNull=false;
	private boolean numberString=false;
	private String  minRange=null;
	private String  maxRange=null;
	private int minLength=0;
	private int maxLength=0;
	private int fixLength=0;
	private boolean emailAddress=false;
	private String  clientRegularExpress=null;
	private String  serverRegularExpress=null;
	
	public boolean isNotNull() {
		return notNull;
	}
	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}
	public boolean isNumberString() {
		return numberString;
	}
	public void setNumberString(boolean numberString) {
		this.numberString = numberString;
	}
	public String getMinRange() {
		return minRange;
	}
	public void setMinRange(String minRange) {
		this.minRange = minRange;
	}
	public String getMaxRange() {
		return maxRange;
	}
	public void setMaxRange(String maxRange) {
		this.maxRange = maxRange;
	}
	public int getMinLength() {
		return minLength;
	}
	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}
	public int getMaxLength() {
		return maxLength;
	}
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
	public int getFixLength() {
		return fixLength;
	}
	public void setFixLength(int fixLength) {
		this.fixLength = fixLength;
	}

	public String getClientRegularExpress() {
		return clientRegularExpress;
	}
	public void setClientRegularExpress(String clientRegularExpress) {
		this.clientRegularExpress = clientRegularExpress;
	}
	public String getServerRegularExpress() {
		return serverRegularExpress;
	}
	public void setServerRegularExpress(String serverRegularExpress) {
		this.serverRegularExpress = serverRegularExpress;
	}
	public boolean isEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(boolean emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	
}
