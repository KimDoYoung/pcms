package kr.dcos.common.servlet;

import kr.dcos.common.servlet.view.JspView;
import kr.dcos.common.servlet.view.View;

/**
 * ModelAndView 클래스
 * 
 * @author Kim Do Young
 *
 */
public class ModelAndView {

	private Object model;
	private View view;
	
	public ModelAndView(){
		this.model = null;
		this.view = null;
	}
	public ModelAndView(String path){
		this.model=null;
		this.view=null;
		if(path != null){
			this.view = new JspView(path);
		}
	}
	public ModelAndView(Model model,View view){
		this.model= model;
		this.view = view;
	}
	
	public Object getModel() {
		return model;
	}
	public void setModel(Object model) {
		this.model = model;
	}
	public View getView() {
		return view;
	}
	public void setView(View view) {
		this.view = view;
	}
	public void addAttribute(Object object){
		
	}
}
