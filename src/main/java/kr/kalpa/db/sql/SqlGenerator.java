package kr.kalpa.db.sql;

import kr.kalpa.mboard.MetaData;

public interface SqlGenerator {
	String createTable(MetaData metaData);
	String dropTable(String tableName);
}
