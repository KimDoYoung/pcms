package kr.dcos.common.sql.utils;

/**
 * JDBC를 통해서 DB를 핸들링할 때 필요한 유틸리티 함수들
 * 
 * @author Kim Do Young
 *
 */
public class JdbcUtil {
	/**
	 * JDBC 의 CLOB 데이터를 스트링으로 반환한다.
	 * @param clob
	 * @return String
	 */
	public static String clobToString(java.sql.Clob clob) {
		StringBuffer sbf = new StringBuffer();
		java.io.Reader br = null;
		char[] buf = new char[1024];
		int readcnt;

		try {
			br = clob.getCharacterStream();
			while ((readcnt = br.read(buf, 0, 1024)) != -1) {
				sbf.append(buf, 0, readcnt);
			}
		} catch (Exception e) {
			e.printStackTrace();
			//
		} finally {
			if (br != null)
				try {
					br.close();
				} catch (Exception e) {
					e.printStackTrace();
					//
				}
		}
		return sbf.toString();
	}
}
