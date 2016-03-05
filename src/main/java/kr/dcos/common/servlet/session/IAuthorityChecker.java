package kr.dcos.common.servlet.session;

/**
 * 
 * 권한 체크 인터페이스
 * 권한 체크 Controller는 이 인터페이스를 구현하여야한다.
 * 
 * @author Kim Do Young
 *
 */
public interface IAuthorityChecker {

	boolean hasAuthority(ISessionInfo sessionInfo, String controllerName, String methodName);

}
