package kr.dcos.common.servlet;

/**
 * CmsMvcDispatcher가 가지고 있는 테이블의 Item
 * 
 * @author Kim Do Young
 *
 */
public class CmsMvcMethodInfo {
	private String packageName;
	private String className;
	private String methodName;
	private boolean sessionCheck;
	private IControllerMethod method;
	
	public CmsMvcMethodInfo(){
	}

	public void setClass(Class<?> clazz){
		String className = clazz.getName();
		this.packageName = clazz.getPackage().getName();
		this.className = className.substring(packageName.length()+1);
	}
	
	public String getKey(){
		String key= this.className +"."+ this.methodName;
		return key.toLowerCase();
	}

	public boolean isSessionCheck() {
		return sessionCheck;
	}

	public void setSessionCheck(boolean sessionCheck) {
		this.sessionCheck = sessionCheck;
	}
	public void setSessionCheck(String sessionCheck) {
		this.sessionCheck = sessionCheck.equals("Y");
	}

	public IControllerMethod getMethod() {
		return method;
	}

	public void setMethod(IControllerMethod method) {
		this.method = method;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	@Override
	public String toString() {
		return "CmsMvcMethodInfo [packageName=" + packageName + ", className="
				+ className + ", methodName=" + methodName + ", sessionCheck="
				+ sessionCheck + ", method=" + method + "]";
	}
	
}
