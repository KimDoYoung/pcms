package kr.kalpa.db.sql;

import kr.kalpa.mboard.MetaData;

public interface SqlGenerator {
	String createTable(MetaData metaData);
	String dropTable(String tableName);
	/**
	 * 테이블이 존재하면 1 없으면 0을 리턴한다.
	 * @param tableName : 오라클의 경우 대문자여야한다 
	 * @return
	 */
	String existTable(String tableName);
}
