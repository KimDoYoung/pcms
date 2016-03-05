package kr.dcos.common.servlet.view;

/**
 * Resolver란 논리적인 path를 물리적인 path로 바꾸어주는 것으로
 * user/insert 와 같이 오면 
 * /user/insert.jsp로 만들어준다.
 * @author Kim Do Young
 *
 */
public class JspResolver implements Resolver {
	protected  String prefix = "/";
	protected  String suffix = ".jsp";
	
	@Override
	public View resolve(String viewName) {
		String name = refinePath(viewName);
		JspView jspView = new JspView(name);
		return jspView;
	}

	private String refinePath(String viewName) {
		int i = viewName.indexOf("?");
		if (i>0) {
			viewName = viewName.substring(0, i);
		}
		String realPath = "";
		if(viewName.startsWith(prefix)==false){
			realPath = prefix;
		}
		realPath += viewName;
		if(viewName.endsWith(suffix)==false){
			realPath += suffix;
		}
		return realPath ;
	}
}
