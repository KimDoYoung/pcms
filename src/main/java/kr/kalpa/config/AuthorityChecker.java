package kr.kalpa.config;

import kr.dcos.common.servlet.session.IAuthorityChecker;
import kr.dcos.common.servlet.session.ISessionInfo;

public class AuthorityChecker implements IAuthorityChecker {

	@Override
	public boolean hasAuthority(ISessionInfo sessionInfo, String controllerName, String methodName) {
		// TODO Auto-generated method stub
		return true;
	}

}
