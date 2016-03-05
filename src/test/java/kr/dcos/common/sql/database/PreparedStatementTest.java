package kr.dcos.common.sql.database;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import kr.dcos.common.sql.exception.SqlExecutorException;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class PreparedStatementTest {

	@Before
	public void setUp() throws Exception {
		DatabaseManager.getInstance().setup();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test @Ignore
	public void test() throws SqlExecutorException {
		
		PreparedStatement pstmt=null;
		Connection conn = DatabaseManager.getInstance().getConnection("dvddb");
		
		try{
			conn.setAutoCommit(true);
	             
	        String sql = "insert into hanjulinfo(line) values( ? )";
	        pstmt = conn.prepareStatement(sql); //SQL문 전송 객체 생성
	        pstmt.setString(1, "AAAAA");
	        int result = pstmt.executeUpdate(); //질의문 수행
	        assertEquals(result, 1);
	     }catch(Exception e) {
	        System.out.println("Data 입력 실패!! : " + e.getMessage());
	     }
	     finally {
	        try{  if(pstmt != null) pstmt.close();
	              if(conn != null)  conn.close();
	        }catch(Exception ex) {}
	     }
	}
	private  java.sql.Timestamp getCurrentTimeStamp() {
	    java.util.Date today = new java.util.Date();
	    return new java.sql.Timestamp(today.getTime());
	}
	@Test @Ignore
	public void test1() throws SqlExecutorException {
		
		PreparedStatement pstmt=null;
		Connection conn = DatabaseManager.getInstance().getConnection("dvddb");
		
		try{
			conn.setAutoCommit(true);
	             
	        String sql = "insert into member(id,pw,nm,reg_date,email,_level,last_access_time)"
	        			+"values(?,'bbb','ccc',?,'123@a.b.c','S',?)";

	        pstmt = conn.prepareStatement(sql); //SQL문 전송 객체 생성
	        pstmt.setString(1, "AAAAA");
	        pstmt.setTimestamp(2, getCurrentTimeStamp() );
	        pstmt.setTimestamp(3, getCurrentTimeStamp());
	        int result = pstmt.executeUpdate(); //질의문 수행
	        assertEquals(result, 1);
	     }catch(Exception e) {
	        System.out.println("Data 입력 실패!! : " + e.getMessage());
	     }
	     finally {
	        try{  if(pstmt != null) pstmt.close();
	              if(conn != null)  conn.close();
	        }catch(Exception ex) {}
	     }
	}
}
