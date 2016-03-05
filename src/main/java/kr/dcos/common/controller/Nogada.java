package kr.dcos.common.controller;

import kr.dcos.common.servlet.ControllerMethod;
import kr.dcos.common.servlet.ForwardInfo;
import kr.dcos.common.servlet.RequestInfo;
import kr.dcos.common.utils.StrUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nogada {
	
	private static Logger logger = LoggerFactory.getLogger(Nogada.class);
	
	@ControllerMethod
	public Object go(RequestInfo requestInfo) throws Throwable{
		String layout = requestInfo.getString("layout");
		String url = requestInfo.getString("url");
		if(StrUtils.isNullOrEmpty(requestInfo.getString("url"))){
			url = "/index.jsp";
		}
		ForwardInfo fi = new ForwardInfo(url);
		if(StrUtils.isNullOrEmpty(layout)==false){
			fi.setLayoutResolver(layout);
			return fi;
		}else{
			return url;
		}
	}
	@ControllerMethod
	public ForwardInfo error(RequestInfo requestInfo) throws Throwable{
		ForwardInfo fi = new ForwardInfo("/common/cms_error.jsp");
		String errorMessage = requestInfo.getErrorMessage();
		logger.debug("errorMessage:" + errorMessage);
		fi.addErrorMessage("dispatcher", errorMessage);
		
		String returnUrl = requestInfo.getReturnUrl();
		if(StrUtils.isNullOrEmpty(returnUrl)){
			returnUrl = requestInfo.getParameter("returnUrl");
		}
		fi.addAttribute("returnUrl", requestInfo.getParameter("returnUrl"));
		return fi;
	}
	
}
