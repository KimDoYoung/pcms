package kr.dcos.common.utils;

/**
 * html tag select의 option 값과 text부분을 위한 클래스
 * 
 * @author Kim Do Young
 *
 */
public class ValueText {
	private String value;
	private String text;
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public ValueText(){}
	public ValueText(String value,String text){
		this.value = value;
		this.text = text;
	}
	@Override
	public String toString(){
		return String.format("value:[%s],text:[%s]", value,text);
				
	}
}
