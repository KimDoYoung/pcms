package kr.kalpa.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.config.ConfigManager;
import kr.dcos.common.servlet.ControllerMethod;
import kr.dcos.common.servlet.ForwardInfo;
import kr.dcos.common.servlet.RequestInfo;
import kr.dcos.common.sql.JdbcTable;
import kr.dcos.common.sql.SqlParam;
import kr.dcos.common.utils.DateUtil;
import kr.kalpa.config.SessionInfo;
import kr.kalpa.service.DbService;

public class LoginController {
	private static Logger logger = LoggerFactory.getLogger(LoginController.class);
	@ControllerMethod
	public ForwardInfo login(RequestInfo requestInfo) {
		String id = requestInfo.getString("id",null);
		String pw = requestInfo.getString("pw",null);
		String mode  = requestInfo.getString("mode",null);
		ForwardInfo forwardInfo = new ForwardInfo("/common/login.jsp");
		//처음 들어왔을 때
		if(mode == null  ){
			//forwardInfo.setAttribute("config",ConfigManager.getInstance().getConfig());
			//forwardInfo.setAttribute("msg","id is empty");
			forwardInfo.setAttribute("mode", "check");
			return forwardInfo;
		}
		if(id == null || pw==null){
			forwardInfo.setAttribute("mode", "check");
			forwardInfo.setAttribute("msg", "Id , Pw 를 넣어주십시오");
			return forwardInfo;
			
		}
		// 사용자 정보를 DB에서 가져와서 SessionInfo에 기록한다.
		try {
			SqlParam param = new SqlParam();
			param.put("id", id);
			DbService service  = new DbService();
			JdbcTable table = service.select("getWebUser", param);
			if(table.getRowSize() == 0){
				forwardInfo.setAttribute("msg", "id is not valid" );
				return forwardInfo;
			}
			
			if(pw.equals(table.getString(0, "pw")) == false){
				forwardInfo.setAttribute("msg", "pw is not correct" );
				return forwardInfo;
			}
			
			HttpSession session = requestInfo.getSession(true);
			if (session.isNew() == false) {
				session.invalidate();
				session = requestInfo.getSession(true);
			}
			String sessionId = session.getId();
			logger.debug("session id :" + sessionId);
			SessionInfo si = new SessionInfo();
			si.setLogin(true);
			si.setValue("id",table.getString(0, "id"));
			si.setValue("pw",table.getString(0, "pw"));
			si.setValue("name",table.getString(0, "name"));
			si.setValue("email",table.getString(0, "email"));
			si.setValue("loginTime",DateUtil.now());
			si.setValue("level", table.getString(0, "level"));
			si.setValue("isLogin",true);
			session.setAttribute(sessionId, si);
			
			int interval = ConfigManager.getInstance().getInteger(
					ConfigManager.getInstance().get("SESSION_INTERVAL_MINUTES"));
			session.setMaxInactiveInterval(interval * 60);
			
			// goto index
			forwardInfo.addAttribute("sessionInfo", si);
			//forwardInfo.setPath("/dashboard/dashboard.jsp");
			forwardInfo.setPath("/index.jsp");
			return forwardInfo;
		} catch (Exception e) {
			forwardInfo.setPath("/common/cms_error.jsp");
			forwardInfo.addErrorMessage("dbError", e.getMessage());
			logger.error("", e);
			return forwardInfo;
		}
	}
	
	@ControllerMethod
	public ForwardInfo logout(RequestInfo requestInfo) {
		HttpSession session = requestInfo.getSession(false);
		if(session == null){
			return new ForwardInfo("/common/login.jsp");
		}

		try{
			String sessionId = session.getId();
			logger.debug("session id :" + sessionId);

			SessionInfo sessionInfo = (SessionInfo)session.getAttribute(sessionId);
			//ForwardInfo forwardInfo = new ForwardInfo("/common/login.jsp");
			ForwardInfo forwardInfo = new ForwardInfo("/index.jsp");
			DbService service  = new DbService();
			SqlParam param = new SqlParam();
			param.put("id", sessionInfo.get("id"));
			service.update("webuser.lougout", param );
			if(sessionInfo != null){
				session.removeAttribute(sessionId);
				forwardInfo.addAttribute("sessionInfo", sessionInfo);
			}
			session.invalidate();
			return forwardInfo;
		}catch(Exception e){
			logger.error("",e);
			ForwardInfo forwardInfo = new ForwardInfo("/common/cms_error.jsp");
			forwardInfo.addErrorMessage("unknown", "unknown error occurred in logout process");
			return forwardInfo;
		}
	}
}
