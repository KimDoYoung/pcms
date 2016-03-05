package kr.dcos.common.servlet.view;


public class HtmlResolver implements Resolver {

	@Override
	public View resolve(String viewName) {
		if(viewName == null){
			viewName = "html";
		}
		HtmlView htmlView = new HtmlView();
		htmlView.setDisplayType(viewName.toLowerCase());
		return htmlView;
	}

}
