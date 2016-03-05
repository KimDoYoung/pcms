package kr.dcos.common.mail;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import kr.dcos.common.utils.StrUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.mail.smtp.SMTPTransport;


/**
 * 구글계정을 이용하여 메일을 보낸다
 * 
 * @author Kim Do Young
 *
 */
public class GoogleMailSender {
	
	private static Logger logger = LoggerFactory
			.getLogger(GoogleMailSender.class);

	/**
	 * mailInfo를 인자로 받아서 그 정보에 기반하여 메일을 보낸다
	 * @param mailInfo
	 * @throws javax.mail.MessagingException 
	 * @throws AddressException 
	 * @throws UnsupportedEncodingException 
	 */
	public static void send(MailInfo mailInfo) throws AddressException, javax.mail.MessagingException, UnsupportedEncodingException{
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

		Properties props = System.getProperties();
		props.setProperty("mail.smtps.host", "smtp.gmail.com");
		props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
		props.setProperty("mail.smtp.socketFactory.fallback", "false");
		props.setProperty("mail.smtp.port", "465");
		props.setProperty("mail.smtp.socketFactory.port", "465");
		props.setProperty("mail.smtps.auth", "true");
		props.put("mail.smtps.quitwait", "false");

		Session session = Session.getInstance(props, null);

		// 새로운 메세지를 만든다
		final Message msg = new MimeMessage(session);
		
		// 메일서버의 id와 pw를 구한다
		String gMailId = mailInfo.getMailServerUserId();
		String gMailPw = mailInfo.getMailServerUserPw();
		if(gMailId.endsWith("@gmail.com")){
			gMailId = StrUtils.removePostfix(gMailId, "@gmail.com");
		}
		//보재는 사람 설정
		msg.setFrom(new InternetAddress(gMailId + "@gmail.com"));
		
		//받는사람 설정
		InternetAddress[] toAddresses = changeToInternetAddresses(mailInfo.getTo());
		msg.setRecipients(Message.RecipientType.TO, toAddresses);

		//CC를 설정
		if (mailInfo.getCc() != null && mailInfo.getCc().length > 0) {
			InternetAddress[] ccAddresses = changeToInternetAddresses(mailInfo.getCc());
			msg.setRecipients(Message.RecipientType.CC,	ccAddresses);
		}
		//ReplyTo설정
		if (mailInfo.getReplyTo() != null && mailInfo.getReplyTo().length > 0) {
			InternetAddress[] replyAddresses = changeToInternetAddresses(mailInfo.getReplyTo());
			msg.setReplyTo(replyAddresses);
		}		
		//제목 내용을 설정한다.
		msg.setSubject(mailInfo.getSubject());
		msg.setSentDate(new Date());

		//
		MimeBodyPart contentBody = new MimeBodyPart();
		//alternate text or html
		MimeMultipart partNaeyong = new MimeMultipart("alternative");
		MimeBodyPart bodyNaeyong = new MimeBodyPart();
		partNaeyong.addBodyPart(bodyNaeyong);
		contentBody.setContent(partNaeyong);
		

		//attach 파일이 있다면
		MimeMultipart content = new MimeMultipart("related");
		content.addBodyPart(contentBody);
		
		 
        for (String attachmentFileName : mailInfo.getAttachFileList()) {
            String id = UUID.randomUUID().toString();
 
            MimeBodyPart attachment = new MimeBodyPart();
            
            DataSource fds = new FileDataSource(attachmentFileName);
            attachment.setDataHandler(new DataHandler(fds));
            attachment.setHeader("Content-ID", "<" + id + ">");
            attachment.addHeader("Content-Type", "application/octet-stream; charset=UTF-8"); 
            attachment.setFileName(MimeUtility.encodeText(fds.getName()));
            logger.debug(MimeUtility.encodeText(fds.getName()));
            content.addBodyPart(attachment);
        }	
		if(mailInfo.isHtml()){
			bodyNaeyong.setContent(mailInfo.getContent(),"text/html;charset="+mailInfo.getCharSet());
		}else {
			bodyNaeyong.setText(mailInfo.getContent(),mailInfo.getCharSet());
		}        
		
		//message에 셋팅한다.
		msg.setContent(content);
		
		SMTPTransport t = (SMTPTransport) session.getTransport("smtps");

		t.connect("smtp.gmail.com", gMailId, gMailPw);
		t.sendMessage(msg, msg.getAllRecipients());
		t.close();
		
	}

	/**
	 * to 문자열 배열을 잘라서 InternetAddress 배열로 만들어서 리턴한다.<br>
	 * to[0] : 홍길동,hong@naver.com,utf-8|홍길동,hong@gmail.com <br> 
	 * to[1] : 김갑순 ,gabsun@naver.com<br>
	 * 
	 * @param to
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	private static InternetAddress[] changeToInternetAddresses(String[] to) throws UnsupportedEncodingException {
		if(to == null) return null;
		List<InternetAddress> list = new ArrayList<InternetAddress>();
		//InternetAddress[] iAddressArray = new InternetAddress[to.length];
		for (String mailItem : to) {
			String [] tmp = mailItem.split("\\|");
			for (String name_email_charset : tmp) {
				if(name_email_charset.length()<1){
					continue;
				}
				String[] tmp2 = name_email_charset.split(",");
				if(!MailInfo.isValidEmailAddress(tmp2[0])){
					continue;
				}
				InternetAddress ia = new InternetAddress();
				if(tmp2.length == 1){
					ia.setAddress(tmp2[0]);
				}else if(tmp2.length == 2){
					ia.setAddress(tmp2[0]);
					ia.setPersonal(tmp2[1],MailInfo.defaultCharSet);
				}else if(tmp2.length==3){
					ia.setAddress(tmp2[0]);					
					ia.setPersonal(tmp2[1],tmp2[2]);
				}
				list.add(ia);
			}
		}
		return list.toArray(new InternetAddress[0]);
	}
}
