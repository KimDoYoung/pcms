package kr.dcos.common.servlet;

/**
 * CmsMvc의 콘트롤러 인터페이스
 * 
 * @author Kim Do Young
 *
 */
public interface IControllerMethod {
	public Object invoke(RequestInfo requestInfo) throws Throwable; 
}
