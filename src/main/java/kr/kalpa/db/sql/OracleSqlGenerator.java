package kr.kalpa.db.sql;

import kr.kalpa.db.DbType;
import kr.kalpa.mboard.MetaData;
import kr.kalpa.mboard.MetaData.Field;

public class OracleSqlGenerator extends SqlGeneratorBase implements SqlGenerator {

	public OracleSqlGenerator() {
		super(DbType.Oracle);
	}
	
	@Override
	public String createTable(MetaData metaData) {
		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(metaData.getId().toLowerCase());
		sb.append("(\n");
		for (Field field : metaData.getFields()) {
			String strNull = field.pilsuYn.equals("Y") ? "NOT NULL" : "NULL";
			String dataType = dataType(field);

			String s = String.format("\t%s %s %s,\n", field.id.toLowerCase(), dataType, strNull);
			sb.append(s);
		}
		sb.append(String.format("\tsys_id NUMBER(10) NOT NULL,\n"));
		sb.append(String.format("\tCONSTRAINT %s_pk PRIMARY KEY (sys_id)\n",metaData.getId().toLowerCase()));
		sb.append(")");
		return sb.toString();
	}
	@Override
	public String dropTable(String tableName) {
		return "DROP TABLE " + tableName.toLowerCase();
	}

}
