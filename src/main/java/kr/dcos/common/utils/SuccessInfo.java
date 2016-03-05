package kr.dcos.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * insert,update,delete등이 성공했을 경우 화면에 메세지를 보여준다 <br>
 * 보여주는 jsp는 /common/success.jsp이다.  <br>
 * success.jsp가 필요로 하는 멤버데이터를 가지고 있다. <br>
 * success.jsp를 보여주기 위해서는 <br>
 * 각 controller에서 이 클래스를 생성해서 forwardInfo에 셋팅해야한다 <br>
 * forwardInfo.setAttribute("successInfo",successInfo); <br>
 * 
 * @author Kim Do Young
 *
 */
public class SuccessInfo {
	private String title;
	private String message;
	private List<ValueText> buttons;

	public SuccessInfo(){
		title = "Success";
		message="operation is successfully done";
		buttons = new ArrayList<ValueText>();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public List<ValueText> getButtons(){
		return buttons;
	}

	public void addButton(String value, String text) {
		buttons.add(new ValueText(value,text));
	}

	public void addButtons(List<ValueText> buttons) {
		for (ValueText vt : buttons) {
			addButton(vt.getValue(), vt.getText());
		}
	}
}
