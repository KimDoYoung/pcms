package kr.dcos.common.servlet.view;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.dcos.common.servlet.Model;
import kr.dcos.common.utils.StrUtils;
import kr.dcos.common.utils.ValueText;

import com.google.gson.Gson;

public class JsonView implements View {
	
	@SuppressWarnings("unchecked")
	@Override
	public void render(Object model, HttpServletRequest request,
			HttpServletResponse response, boolean include) throws Exception {
		if(model instanceof Model){
			model = ((Model)model).getMap();
			HashMap<String,Object> hashMap = (HashMap<String, Object>) model;

			Gson gson = new Gson();
			String s = gson.toJson(hashMap);
			
			response.setContentType("application/json;charset=UTF-8");
		    response.setCharacterEncoding("UTF-8");
			//String result = StrUtils.toCharset(s.toString(), "UTF-8");
			response.getWriter().print(s);
		}
	}
}
