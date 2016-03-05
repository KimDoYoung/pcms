package kr.dcos.common.utils;

/**
 * ValueText 클래스를 그냥 이용하면서 이름만 key value로 하다
 * @author Kim Do Young
 *
 */
public class KeyValue extends ValueText {
	public KeyValue(String key, String value) {
		super(key,value);
	}
	public String getKey(){
		return super.getValue();
	}
	public void setKey(String key){
		setValue(key);
	}
	public String getValue(){
		return super.getText();
	}
	public void setValue(String value){
		super.setText(value);
	}
	@Override
	public String toString(){
		return String.format("keyvalue:[%s]->[%s]",getKey(),getValue());
	}
}
