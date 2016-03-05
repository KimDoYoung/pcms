package kr.dcos.common.servlet;

/**
 * CmsMvc에서 해석할 수 있는 Resolver Type들 <br>
 * example: ForwardInfo fw = new ForwardInfo(ResolverType.HTML);<br>
 * 
 * @author Kim Do Young
 *
 */
public enum ViewType {
	HTML,JSON,DOWNLOAD,IMAGE,ERROR
}
