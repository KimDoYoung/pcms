package kr.kalpa.config;

import java.util.HashMap;

import kr.dcos.common.servlet.session.ISessionInfo;

public class SessionInfo extends HashMap<String,Object> implements ISessionInfo {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4401348202614005360L;
	private boolean isLogin;
	
	public SessionInfo(){
		isLogin = false;
	}
	
	@Override
	public String getLevel() {
		if(get("level")!=null && get("level").equals("A")){
			return "ADMIN";
		}
		return "NORAML";
	}
	
		
	@Override
	public void setValue(String key, Object value) {
		put(key,value);
	}

	@Override
	public Object getValue(String key) {
		return get(key);
	}


	public Boolean getIsLogin() {
		return isLogin;
	}

	public void setLogin(Boolean isLogined) {
		this.isLogin = isLogined;
		
	}

}
