package kr.dcos.common.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 쿠키관련 유틸리티
 * 
 * @author Kim Do Young
 *
 */
public class CookieUtil {
	private HttpServletResponse response;
	private HttpServletRequest request;
	private String encode;
	public CookieUtil(HttpServletResponse response,HttpServletRequest request,String encode ){
		this.response =response;
		this.request = request;
		this.encode = encode;
	}
	public CookieUtil(HttpServletResponse response,HttpServletRequest request ){
		this(response,request,"UTF-8");
	}
	public  boolean setCookie(String name, String value) {
		try {
			Cookie cookie = new Cookie(name, URLEncoder.encode(value, "UTF-8"));
			cookie.setMaxAge(24 * 60 * 60);
			response.addCookie(cookie);

			return true;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();

			return false;
		}
	}

	public  String getCookie(String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return "";
		}

		for (int i = 0; i < cookies.length; i++) {
			if (cookies[i].getName().equals(name)) {
				try {
					return URLDecoder.decode(cookies[i].getValue(),encode);
				} catch (UnsupportedEncodingException e) {
					return "";
				}
			}
		}
		return "";
	}
}