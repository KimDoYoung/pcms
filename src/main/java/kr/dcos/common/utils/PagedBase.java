package kr.dcos.common.utils;


/**
 * orderby 는 한개만
 * @author Kim Do Young
 *
 */
public class PagedBase {
	//for page display
	protected PageAttribute pageAttribute;


	//Constructor
	public PagedBase(){
		pageAttribute = new PageAttribute();
		
	}
	
	//
	// Getter,Setter
	//
	public PageAttribute getPageAttr() {
		return pageAttribute;
	}
	
	public void setPageAttr(PageAttribute pageAttribute) {
		this.pageAttribute = pageAttribute;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(pageAttribute.toString());
		sb.append("\n");
		return sb.toString();
	}
}
