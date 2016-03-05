package kr.dcos.common.servlet.view;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface View {
	 void render(Object model, HttpServletRequest request,
             HttpServletResponse response, boolean include) throws Exception;
}
