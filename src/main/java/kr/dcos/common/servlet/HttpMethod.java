package kr.dcos.common.servlet;

/**
 * HTTP method를 정의한 enum 
 * 
 * @author Kim Do Young
 *
 */
public enum HttpMethod {
	GET,
	POST,
	PUT,
	DELETE,
	HEAD,
	TRACE,
	OPTIONS,
	ALL;
	
	
	public static HttpMethod parse(String s) {
		return Enum.valueOf(HttpMethod.class, s);
	}

	public static int toInt(HttpMethod httpMethod) {
		switch(httpMethod) {
			case GET: return 0;
			case POST: return 1;
			case PUT: return 2;
			case DELETE: return 3;
			case HEAD: return 4;
			case TRACE: return 5;
			case OPTIONS: return 6;
			case ALL: return 7;
		}
		return 0;
	}
	
};
