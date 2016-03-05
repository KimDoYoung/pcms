package kr.dcos.common.servlet.view;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import kr.dcos.common.servlet.Model;

public class HtmlView implements View {

	//private List<String> attributeList;
	private String displayType;
	@SuppressWarnings("unchecked")
	@Override
	public void render(Object model, HttpServletRequest request,
			HttpServletResponse response, boolean include) throws Exception {
		if(model instanceof Model){
			model = ((Model)model).getMap();
			HashMap<String,Object> hashMap = (HashMap<String, Object>) model;
			//if(attributeList == null ) return;
			Object o = null;
			String s = "";
			if(displayType.equals("html")){
				o = hashMap.get("html");
				if(o != null){
					s = o.toString();
				}
			}else if(displayType.equals("text")){
				o = hashMap.get("text");
				if(o != null){
					s = StringEscapeUtils.escapeHtml4(o.toString());				
				}
			}			
			response.setContentType("text/html;charset=UTF-8");
		    response.setCharacterEncoding("UTF-8");
			//String result = StrUtils.toCharset(s.toString(), "UTF-8");
		    
			response.getWriter().print(s);
		}
	}

	public void setDisplayType(String displayType) {
		this.displayType = displayType;
	}
	
//	public void setAttributeNames(String viewName) {
//		String[] tmp = viewName.split("\\.");
//		if(tmp.length>0){
//			attributeList = new ArrayList<String>();
//			for (String attr : tmp) {
//				attributeList.add(attr);
//			}
//		}
//	}

}
