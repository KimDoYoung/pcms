package kr.kalpa.db.sql;

import kr.kalpa.db.DbType;
import kr.kalpa.mboard.MetaData;
import kr.kalpa.mboard.MetaData.DataType;
import kr.kalpa.mboard.MetaData.Field;

/**
 * Metadata의 Field Desc을 받아서 DDL SQL을 만든다. <br>
 * Table을 받아서 insert SQL 또는 update SQL을 만든다.
 * 
 * @author Kim Do Young
 *
 */
public class SqlGeneratorBase {
	protected DbType dbType = DbType.Unknown;

	public SqlGeneratorBase(DbType dbType) {
		this.dbType = dbType;
	}
	
	public DbType getDbType(){
		return this.dbType;
	}
	

	protected String dataType(Field field) {
		DataType dataType = field.dataType;
		if(dataType == DataType.String){
			return String.format("VARCHAR(%d)",field.size);
		}else if(dataType == DataType.Integer){
			return String.format("NUMBER(10)");
		}else if(dataType == DataType.Double){
			if(field.precision == 0){
				return String.format("NUMBER(%d)", field.size);
			}else{
				return String.format("NUMBER(%d,%d)", field.size, field.precision);
			}
		}else if(dataType == DataType.DateTime){
			return "TIMESTAMP";
		}
		return null;
	}



}