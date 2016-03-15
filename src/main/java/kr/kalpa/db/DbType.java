package kr.kalpa.db;

/**
 * 데이터베이스 종류들
 * @author Kim,Do Young
 * 
 */
public enum DbType {
	Unknown(0), MySql(1), Microsoft_Sql_Server(2), Oracle(3);
	private int value;

	private DbType(int value) {
		this.value = value;
	}

	public int getInt() {
		return value;
	}

	public static DbType typeOf(String db_type) {
		if(db_type.equalsIgnoreCase("MySql")){
			return DbType.MySql;
		}else if(db_type.equalsIgnoreCase("Microsoft_Sql_Server") || db_type.equalsIgnoreCase("mssql")){
			return DbType.Microsoft_Sql_Server;
		}else if(db_type.equalsIgnoreCase("oracle")){
			return DbType.Oracle;
		}else{
			return DbType.Unknown;
		}
		
	}
}
