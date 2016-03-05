package kr.dcos.common.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

//import kr.dcos.common.sql.Column.DataType;
import kr.dcos.common.sql.utils.ValueConverter;
import kr.dcos.common.sql.utils.ValueConverterFactory;
import kr.dcos.common.utils.table.Column;
import kr.dcos.common.utils.table.Column.DataType;
import kr.dcos.common.utils.table.Row;
import kr.dcos.common.utils.table.Table;
import kr.dcos.common.utils.table.TableException;

public class JdbcTable extends Table{

	public JdbcTable() {
	}

	/**
	 * ResultSet rs로부터 row를 추가해가면서 값들을 채운다
	 * @param rs
	 * @throws TableException
	 */
	private void setupValueWithResultSet(ResultSet rs) throws TableException {
		
		ResultSetMetaData rsmd;
		try {
			rsmd = rs.getMetaData();
			while(rs.next()){
				Row row = newRow();
				for (int i = 1; i <= getColumnsSize(); i++) {
					ValueConverter vc = ValueConverterFactory.getConverter(rsmd, i);
					row.setValue(i - 1, vc.getValue(rs, i));
				}
				appendRow(row);
			}
		} catch (SQLException e) {
			throw new JdbcTableException(e.getMessage());
		} 
	}

	/**
	 * jdbc ResultSet으로 table의 Column 정의를 다시 만든다
	 * @param rs
	 * @throws JdbcTableException
	 */
	private void setupColumnsWithResultSet(ResultSet rs) throws TableException {
		
		try {
			ResultSetMetaData rsmd = rs.getMetaData();
			for(int i=1;i<=rsmd.getColumnCount();i++){
				String name = rsmd.getColumnLabel(i);
				ValueConverter vc  = ValueConverterFactory.getConverter(rsmd, i);
				DataType dataType = vc.getType();
				Column column = new Column(name,dataType, vc.getLength(), false, false);
				appendColumn(column);
			}
		} catch (SQLException e) {
			throw new JdbcTableException(e.getMessage());
		}
	}
	/**
	 * ResultSet rs 로부터 테이블 내용을 채운다
	 * @param rs
	 * @throws TableException
	 */
	//TODO make clear all rows and columns
	public void fillWithResultSet(ResultSet rs) throws TableException{
		//empty(); -> row 삭제, columns삭제
		setupColumnsWithResultSet(rs);
		setupValueWithResultSet(rs);
	}

	public Object getValue(int rowIndex, int colIndex) {
		return getRow(rowIndex).getObject(colIndex);
	}

	public String getString(int rowIndex, String columnName) {
		return getRow(rowIndex).getString(columnName);
	}

}
