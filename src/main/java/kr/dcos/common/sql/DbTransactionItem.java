package kr.dcos.common.sql;

import java.sql.PreparedStatement;

public class DbTransactionItem {
	public enum SqlType {SqlId, SqlItself};
	private SqlType type;
	private String sql;
	private String sqlId;
	private Object param;
	private PreparedStatement preparedStatement;
	public DbTransactionItem(){
		this(null,null);
	}
	public DbTransactionItem(String sqlId, Object param){
		this.type = SqlType.SqlId;
		this.sql = null;
		this.sqlId = sqlId;
		this.param = param;
	}
	public DbTransactionItem(String sql){
		this.type = SqlType.SqlItself;
		this.sql = sql;
		this.sqlId =  null;
		this.param = null;
	}
	public SqlType getType() {
		return type;
	}
	public void setType(SqlType type) {
		this.type = type;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getSqlId() {
		return sqlId;
	}
	public void setSqlId(String sqlId) {
		this.sqlId = sqlId;
	}
	public Object getParam() {
		return param;
	}
	public void setParam(Object param) {
		this.param = param;
	}
	public void setPrepareStatement(PreparedStatement pstmt) {
		this.preparedStatement = pstmt;
		
	}
	public PreparedStatement getPreparedStatement() {
		return preparedStatement;
	}
	public void setPreparedStatement(PreparedStatement preparedStatement) {
		this.preparedStatement = preparedStatement;
	}
}
