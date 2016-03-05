package kr.dcos.common.servlet.session;

/**
 * 세션정보 인터페이스
 * 
 * @author Kim Do Young
 *
 */
public interface ISessionInfo {

	String getLevel();
	void setValue(String key,Object value);
	Object getValue(String key);

}
