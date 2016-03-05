package kr.dcos.common.mail;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import kr.dcos.common.utils.StrUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mail보내기에 대한 정보를 담고 있는 클래스
 * MailSender에게 인자로 넘기면서 send 메소드를 호출하여 메일을 보낸다
 * MailInfo mailInfo = new MailInfo();
 * mailInfo.setXXX 를 한다.
 * to,cc 는 다음과 같은 구조를 갖는 문자열이다.
 * 콤마와 | 로 구분한다.
 * 이름,이메일주소,문자세(생략가능)| 
 * hong@naver.com,홍길동,utf-8|hong@gmail.com,홍길동
 * gabsun@naver.com,김갑순
 * ...
 * 사용법
 * if(mailInfo.isValid()){
 *     GoogleMailSender.send(mailInfo);
 * }
 * @author Kim Do Young
 *
 */
public class MailInfo {
	
	private static Logger logger = LoggerFactory.getLogger(MailInfo.class);
	

	public static final String defaultCharSet = "UTF-8";
	public enum EmailTarget { To,Cc,Reply };
	public class NameEmailCharset{
		public String Name;
		public String EmailAddress;
		public String CharSet;
		public NameEmailCharset(String name,String emailAddress,String charSet){
			this.Name = name; this.EmailAddress = emailAddress; this.CharSet = charSet;
		}
		public NameEmailCharset(String name,String emailAddress){
			this(name,emailAddress,defaultCharSet);
		}
		public NameEmailCharset(String emailAddress){
			this("",emailAddress);
		}
	}
	private List<String> errorList;
	private String       mailServerUserId=null;
	private String       mailServerUserPw=null;
	private List<String> toList    	= null;
	private List<String> ccList    	= null;
	private List<String> replyList  = null;
	private String    	 from  		= "";
	private String 		 subject  	= "";
	private String 		 content  	= "";

	private String 		 fileName  	= "";
	private String 		 filePath  	= "";
	private boolean 	 isHtml 		= false;
	private boolean 	 isXSS 		= true;
	private String 		 charSet = defaultCharSet;


	private List<String> attachFileList=null;
	
	public MailInfo(){
		errorList = new ArrayList<String>();
		toList = new ArrayList<String>();
		ccList = new ArrayList<String>();
		replyList = new ArrayList<String>();
		attachFileList = new ArrayList<String>();
	}

	public static boolean isValidEmailAddress(String emailAddress){
		if(emailAddress == null) return false;
		String regex ="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		Pattern pattern = Pattern.compile(regex);
		return pattern.matcher(emailAddress).matches();
		
	}	
	/**
	 * 필수 조건들이 모두 채워졌는지 체크한다.
	 * 체크해서 에러가 있으면 errorList를 추가한다.
	 * 1. 받는사람의 이메일이 제대로 들어가 있는지 체크
	 * 2. 보내는 사람의 이메일이 제대로 들어가 있는지 체크
	 */
	private void checkValid() {
		errorList.clear();
		//메일서버 id,pw가 들어 있어야한다.
		if(StrUtils.isNullOrEmpty(mailServerUserId)){
			errorList.add("mail server userId is empty");
		}
		if(toList.size() == 0){
			errorList.add("to email address is empty");
		}else {
			for (String item : toList) {
				String[] tmp = item.split("\\|");
				for (String enc : tmp) {
					String [] tmp2 = enc.split(",");
					String email = null;
					if(tmp2.length == 1){
						email = tmp2[0];
					}else if(tmp2.length == 2){
						email = tmp2[0];
					}else if(tmp2.length == 3){
						email = tmp2[0];
					}
					if(!isValidEmailAddress(email)){
						errorList.add(email +" is not valid email("+enc+")");
					}
				}
			}
		}

		if(from.length() == 0){
			errorList.add("to email address is empty");
		}else{
			if(!isValidEmailAddress(from)){
				errorList.add("from email address is not valid");
			}
		}
		//attach file exist?
		for (String fileName : attachFileList) {
			File file = new File(fileName);
			if(file.canRead() == false){
				errorList.add(file.getAbsolutePath()+" is not exist or can not read");
			}
		}
	}
	/**
	 * 채워진 내용으로 메일을 보낼 수 있는지 체크해서 가능하면 true를 리턴한다
	 * @return
	 */
	public boolean isValid(){
		checkValid();
		if(errorList != null && errorList.size()>0){
			return false;
		}
		return true;
	}	
	public String getMailServerUserId() {
		return mailServerUserId;
	}
	public void setMailServerUserId(String mailServerUserId) {
		this.mailServerUserId = mailServerUserId;
	}
	public String getMailServerUserPw() {
		return mailServerUserPw;
	}
	public void setMailServerUserPw(String mailServerUserPw) {
		this.mailServerUserPw = mailServerUserPw;
	}

	public String[] getTo() {
		return toList.toArray(new String[0]);
	}
	public void setTo(String[] to) {
		for (String s : to) {
			this.toList.add(s);
		}
	}
	public String[] getCc() {
		return ccList.toArray(new String[0]);
	}
	public void setCc(String[] cc) {
		for (String s : cc) {
			this.ccList.add(s);
		}
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public boolean isHtml() {
		return isHtml;
	}
	public void setHtml(boolean isHtml) {
		this.isHtml = isHtml;
	}
	public boolean isXSS() {
		return isXSS;
	}
	public void setXSS(boolean isXSS) {
		this.isXSS = isXSS;
	}


	public String getCharSet() {
		return charSet;
	}

	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}

	public String[] getReplyTo() {
		return replyList.toArray(new String[0]);
	}

	public void setReplyTo(String[] replyTo) {
		for (String s : replyTo) {
			this.replyList.add(s);
		}
	}

	public String[] errorMessages() {
		if(errorList == null) return null;
		return errorList.toArray(new String[0]);
	}
	@Override 
	public String toString(){
		StringBuilder sb = new StringBuilder();
		//TODO 채울것
		return sb.toString();
	}

	public void addTo(String email,String name,String charset) {
		String email_name_charset = null;
		String email1 = email;
		String name1 = name;
		String charset1 = charset;
		
		if(StrUtils.isNullOrEmpty(email)){
			logger.error("email is empty");
			return;
		}
		if(StrUtils.isNullOrEmpty(name)){
			name1 = "";
		}
		if(StrUtils.isNullOrEmpty(charset)){
			charset1 = defaultCharSet;
		}
		email_name_charset = String.format("%s,%s,%s",email1,name1,charset1);
		toList.add(email_name_charset);
	}

	public void addAttachFile(String fileName) {
		attachFileList.add(fileName);
		
	}

	public List<String> getAttachFileList() {
		return attachFileList;
	}

	public void setAttachFileList(List<String> attachFileList) {
		this.attachFileList = attachFileList;
	}

}
