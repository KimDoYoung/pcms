package kr.dcos.common.servlet.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import kr.dcos.common.servlet.ControllerMethodName;

/**
 * 
 * 인증과 권한을 관리하는 클래스
 * for authentication and authorization 
 * @author Kim Do Young
 *
 */
public class SessionManager {
	private ISessionInfo sessionInfo = null;
	private IAuthorityChecker authorityChecker = null;
	private boolean isLogin =false;
	//constructor
	public SessionManager(HttpServletRequest request){
		HttpSession session =  request.getSession(false);
		if(session == null){
			isLogin = false;
		}else{
			String sid = session.getId();
			sessionInfo = (ISessionInfo) session.getAttribute(sid);
			isLogin = (sessionInfo != null);
		}
	}
	//getter, setter
	public boolean isLogin(){
		return isLogin;
	}
	public ISessionInfo getSessionInfo(){
		return sessionInfo;
	}
	/**
	 * login check
	 * @param cmn
	 * @return
	 */
	public boolean authenticationCheck(ControllerMethodName cmn) {
		String controller = cmn.getControllerName();
		if(controller.equalsIgnoreCase("logincontroller")){
			return true;
		}
		return isLogin;
	}
	/**
	 * Authority 수행하고자하는 controller.method에 대한 권한이 있는가? 체크한다.
	 * authorityChecker가 기술되어 있지않다면 즉 null이라면 true를 리턴한다 
	 * 
	 */
	public boolean authorityCheck(ControllerMethodName cmn){
		
		if(authorityChecker == null) return true;
		
		//logincontroller는 무조건 통과
		String controller = cmn.getControllerName();
		if(controller.equalsIgnoreCase("logincontroller")){
			return true;
		}
		
		if(sessionInfo == null) return false;
		return authorityChecker.hasAuthority(getSessionInfo(), cmn.getControllerName(), cmn.getMethodName());
	}
	public IAuthorityChecker getAuthorityChecker() {
		return authorityChecker;
	}
	public void setAuthorityChecker(IAuthorityChecker authorityChecker) {
		this.authorityChecker = authorityChecker;
	}
}
