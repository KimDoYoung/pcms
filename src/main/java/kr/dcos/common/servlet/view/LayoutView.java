package kr.dcos.common.servlet.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * layout
 * @author Kim Do Young
 *
 */
public class LayoutView extends JspView {

	private String viewName;
	private String title;
	
	public LayoutView(String layout, String viewName, String title) {
		super(layout); //layout 명이 
		this.viewName = viewName;
		this.title = title;
	}

	@Override
	public void render(Object model, HttpServletRequest request,
			HttpServletResponse response, boolean include) throws Exception {
		request.setAttribute("title", title);
		request.setAttribute("mainPage", viewName);
		super.render(model, request, response, false);
	}

}
