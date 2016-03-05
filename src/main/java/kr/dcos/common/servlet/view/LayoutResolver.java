package kr.dcos.common.servlet.view;

/**
 * 
 * layout을 지정하는 LayoutView를 리턴한다.
 * 
 * layoutName = /layouts/standard , 
 * viewName = /1.jsp 
 * @author Kim Do Young
 *
 */
public class LayoutResolver extends JspResolver {
	protected String layoutName ;
	
	public LayoutResolver(String layoutName){
		this.layoutName = refineName(layoutName);
	}
	
	@Override
	public View resolve(String viewName) {
		String title = getTitle(viewName);
		String layout1 = refineName(layoutName);
		String viewName1 = refineName(viewName);
		return new LayoutView(layout1,viewName1,title);
	}

	/**
	 * 첫글자는 /로 , 끝은 jsp로 
	 * @param layout
	 * @return
	 */
	private static String refineName(String layout) {
		String name = "";
		if(layout.startsWith("/")==false){
			name = "/";
		}
		name += layout;
		if(layout.endsWith(".jsp")==false){
			name += ".jsp";
		}
		return name;
	}
	
	
	//title.1.jsp
	
	/**
	 *  /1.jsp , /cmslatte/code_test.jsp
	 *  1.jsp.title, cmslatte.code_test.jsp.title
	 * @param viewName
	 * @return
	 */
	private String getTitle(String viewName) {
		String key =  viewName.replace('/', '.');
		if(key.startsWith(".")){
			key  = key.substring(1);
		}
		return key+".title";
	}
}
