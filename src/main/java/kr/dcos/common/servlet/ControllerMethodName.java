package kr.dcos.common.servlet;
/*
 * command를 해석하기 위해서만들었음. <br>
 * domain의 controller와 다른 용도임 <br>
 */
public class ControllerMethodName {
	private String controllerName;
	private String methodName;
	
	public ControllerMethodName(String controllerName,String methodName){
		this.controllerName = controllerName;
		this.methodName = methodName;
	}
	
	public String getControllerName() {
		return controllerName;
	}
	public void setControllerName(String controllerName) {
		this.controllerName = controllerName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	@Override
	public String toString(){
		return controllerName+"."+methodName;
	}
}
