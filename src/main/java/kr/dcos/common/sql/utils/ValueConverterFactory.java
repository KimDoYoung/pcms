package kr.dcos.common.sql.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import kr.dcos.common.utils.table.Column.DataType;

/**
 * 
 * JDBC로 가져온 데이터의 값을 변경시키는 클래스
 *  
 * @author Kim Do Young
 *
 */
public class ValueConverterFactory {
	public static final ValueConverter DOUBLE_MAPPER = new DoubleConverter();
	public static final ValueConverter INTEGER_MAPPER = new IntegerConverter();
	public static final ValueConverter DATE_MAPPER = new DateConverter();
//	public static final ValueConverter STRING_MAPPER = new StringConverter();

	public static ValueConverter getConverter(ResultSetMetaData rsmd,
			int columnIndex) throws SQLException {
		
		String classname = rsmd.getColumnClassName(columnIndex);
		int precision = rsmd.getPrecision(columnIndex);
		int scale = rsmd.getScale(columnIndex);
		int sqlType = rsmd.getColumnType(columnIndex);

		if (sqlType == Types.INTEGER
				|| classname.equalsIgnoreCase("java.lang.Integer")
				|| (classname.equalsIgnoreCase("java.math.BigDecimal")
						&& precision < 10 && scale == 0))
			return INTEGER_MAPPER;

		if (classname.equalsIgnoreCase("java.math.BigDecimal"))
			return DOUBLE_MAPPER;

		if (classname.equalsIgnoreCase("java.sql.Timestamp"))
			return DATE_MAPPER;

		if (classname.equalsIgnoreCase("java.String"))
			return new StringConverter(precision);

		// default is string
		return new StringConverter(precision);
	}

	public static class IntegerConverter implements ValueConverter {

		@Override
		public DataType getType() {
			return DataType.INTEGER;
		}

		@Override
		public Object getValue(ResultSet rs, int columnIndex) throws SQLException {
		   return new Integer(rs.getInt(columnIndex));
		}

		@Override
		public int getLength() {
			return 0;
		}

	}

	public static class DateConverter implements ValueConverter {

		@Override
		public DataType getType() {
			return DataType.DATE;
		}

		@Override
		public Object getValue(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getTimestamp(columnIndex);
		}

		@Override
		public int getLength() {
			return 0;
		}

	}

	public static class DoubleConverter implements ValueConverter {

		@Override
		public DataType getType() {
			return DataType.DOUBLE;
		}

		@Override
		public Object getValue(ResultSet rs, int columnIndex) throws SQLException {
			return new Double(rs.getDouble(columnIndex));
			
		}

		@Override
		public int getLength() {
			return 0;
		}

	}

	public static class StringConverter implements ValueConverter {
		private Integer length = 10;
		public StringConverter(int length){
			this.length = length;
		}
		@Override
		public DataType getType() {
			return  DataType.STRING;
		}

		@Override
		public Object getValue(ResultSet rs, int columnIndex) throws SQLException {
			return rs.getString(columnIndex);
		}
		@Override
		public int getLength() {
			return length;
		}

	}
}
