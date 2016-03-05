package kr.dcos.common.servlet.view;

import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.dcos.common.servlet.Model;

public class JspView implements View {

	private String name;
	public JspView(String name){
		this.name = name;
	}
	
	@Override
	public void render(Object model, HttpServletRequest request,
			HttpServletResponse response, boolean include) throws Exception {
		
		RequestDispatcher rd = request.getRequestDispatcher(name);
		if(rd==null){
			throw new Exception(name+"  is not exist");
		}
		loadModel(model,request);
		if(include){
			rd.include(request, response);
		}else{
			rd.forward(request, response);
		}
	}

	private void loadModel(Object model, HttpServletRequest request) {
		if(model instanceof Model){
			model = ((Model)model).getMap();
		}
		if(model instanceof Map<?,?>){
			@SuppressWarnings("unchecked")
			Map<Object,Object>map = (Map<Object,Object>)model;
			for (Map.Entry<Object, Object> entry : map.entrySet()) {
				Object key = entry.getKey();
				Object value = entry.getValue();
				request.setAttribute(key.toString(), value);
			}
		}
	}

}
