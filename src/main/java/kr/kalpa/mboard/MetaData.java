package kr.kalpa.mboard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.dcos.common.utils.StrUtils;

/**
 * MBoard의 메타데이터
 * 
 * @author Kim Do Young
 *
 */
public class MetaData {
	private static Logger logger = LoggerFactory.getLogger(MetaData.class);
	public enum DataType { String, Integer, Double, AutoIncrement,  DateTime};
	public class Field {
		public String id;
		public String title;
		public DataType dataType;
		public Integer size;
		public Integer precision; //소수점 자리
		public String pilsuYn;
		@Override
		public String toString(){
			return String.format("%s, %s, %s, %s.%s, %s",id, title, dataType.toString(), size,precision,pilsuYn);
		}
	}
	private String id;
	private String name;
	private String boardType;
	private String desc;

	private String dataSourceId;
	private String dataSourceType;

	private List<Field> fields;
	
	public MetaData(String metaString) throws MBoardException {
		fields = new ArrayList<Field>();
		parsing(metaString);
	}
	
	public MetaData(File file) throws MBoardException {
		
		String metaString;
		try {
			metaString = FileUtils.readFileToString(file, "UTF-8");
			fields = new ArrayList<Field>();
			parsing(metaString);

		} catch (IOException e) {
			throw new MBoardException(e.getMessage());
		}
	}

	private void parsing(String metaString) throws MBoardException{
		String[] lines = metaString.split("\n");
		String s; int pos; boolean descFlag = false, fieldFlag = false;
		for (String line : lines) {
			s = line.trim();
			if(s.length() < 1) continue;
			if(s.startsWith("#")) continue;

			if(s.startsWith("$id")){
				pos = s.indexOf(':');
				if(pos > -1){
					id = s.substring(pos+1).trim();
				}
				continue;
			}
			
			if(s.startsWith("$name")){
				pos = s.indexOf(':');
				if(pos > -1){
					name = s.substring(pos+1).trim();
				}
				continue;
			}
			
			if(s.startsWith("$type")){
				pos = s.indexOf(':');
				if(pos > -1){
					boardType = s.substring(pos+1).trim();
				}
				continue;
			}
			if(s.startsWith("$dataSourceType")){
				pos = s.indexOf(':');
				if(pos > -1){
					dataSourceType = s.substring(pos+1).trim();
				}
				continue;
			}
			if(s.startsWith("$dataSourceId")){
				pos = s.indexOf(':');
				if(pos > -1){
					dataSourceId = s.substring(pos+1).trim();
				}
				continue;
			}			
			if(s.startsWith("$desc")){
				pos = s.indexOf(':');
				if(pos > -1){
					desc = s.substring(pos+1).trim();
				}
				descFlag = true;
				continue;
			}
			if(s.endsWith("desc$")) {
				desc += s.substring(0, s.indexOf("desc$"));
				descFlag = false;
				desc = desc.trim();
			}
			if(descFlag){
				desc += s + "\n";
			}
			
			if(s.startsWith("$fields")){
				pos = s.indexOf(':');
				if(pos > -1){
					addField(s.substring(pos+1).trim());
				}
				fieldFlag = true;
				continue;
			}
			if(s.endsWith("fields$")) {
				addField(s.substring(0, s.indexOf("fields$")));
				fieldFlag = false;
			}
			if(fieldFlag){
				addField(s);
			}
				
		}
	}
	/**
	 * string s를 짤라서 Field를 생성한 후 fields에 추가한다
	 * 만약 조건에 안 맞는다면 추가하지 않는다.
	 * @param s
	 * @throws MBoardException 
	 */
	private void addField(String s) throws MBoardException {
		if(s.trim().length() < 1) return;
		String[] ss = s.trim().split("\\s*[,]\\s*");
		if(ss.length < 3) {
			logger.error("[" + s + "] is not valid field desc");
			return;
		}
		Field field = new Field();
		field.id = ss[0].trim();
		field.title = ss[1].trim();
		field.dataType = toDataType(ss[2]);
		if(field.dataType == DataType.DateTime ||field.dataType == DataType.Integer ){
			field.size = 0;
			field.precision = 0;
		} else {
			field.size = getJeongsu(ss[3]);
			field.precision = getPrecision(ss[3]);
		}
		if(ss.length > 3){
			field.pilsuYn = toYn(ss[4]);
		}
		fields.add(field);
	}
	/**
	 * 4.3 또는 4 와같은 경우에 소수점 이하의 자리수를 리턴
	 * @param s
	 * @return
	 */
	private Integer getPrecision(String s) {
		String j = s;
		if (s.indexOf('.') > -1) {
			j = s.substring(s.indexOf('.')+1);
			return Integer.parseInt(j);
		}else{
			return 0;
		}
	}

	/**
	 * 4.3과 같을 때 4를 리턴한다.
	 * 4 와 같이 정수일때는 그냥 4를 리턴한다.
	 * @param s
	 * @return
	 */
	private Integer getJeongsu(String s) {
		String j = s;
		if (s.indexOf('.') > -1) {
			j = s.substring(0, s.indexOf('.'));
			if(j.trim().length()<1) return 0;
			int n = 0;
			n = Integer.parseInt(j);
			return n;
		}else{
			return  Integer.parseInt(j);
		}
	}

	private String toYn(String s) {
		if(s.substring(0,1).equalsIgnoreCase("Y")) {
			return "Y";
		}
		return "N";
	}

	/**
	 * string s 을 해석 적당한 데이터타입을 고른다.
	 * @param s
	 * @return
	 * @throws MBoardException 
	 */
	private DataType toDataType(String s) throws MBoardException {
		String t = s.trim();
		if(	t.equalsIgnoreCase("string") 
				||t.equalsIgnoreCase("char") 
				|| t.equalsIgnoreCase("varchar") 
				) {
			return DataType.String;
		}else if(s.trim().equalsIgnoreCase("int") || s.trim().equalsIgnoreCase("integer") ){
			return DataType.Integer;
		}else if(s.trim().equalsIgnoreCase("real") || s.trim().equalsIgnoreCase("double") || s.trim().equalsIgnoreCase("number") ){
			return DataType.Double;
		}else if(s.trim().equalsIgnoreCase("date") || s.trim().equalsIgnoreCase("datetime")){
			return DataType.DateTime;
		}else{
			throw new MBoardException(s + " is invalid type");
		}
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public List<Field> getFields() {
		return fields;
	}
	public String getDataSourceId() {
		return dataSourceId;
	}

	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}	
	public String getBoardType() {
		return boardType;
	}


	public String getDataSourceType() {
		return dataSourceType;
	}

	public void writeToFile(File file) throws  MBoardException {
		try {
			
			FileUtils.writeStringToFile(file, toString());
		} catch (Exception e) {
			throw new MBoardException(e.getMessage());
		}
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("$dataSourceType:").append(dataSourceType).append("\n");
		sb.append("$dataSourceId:").append(dataSourceId).append("\n");
		sb.append("$id:").append(id).append("\n");
		sb.append("$name:").append(name).append("\n");
		sb.append("$type:").append(boardType).append("\n");
		sb.append("$desc:").append("\n").append(desc).append("\n");
		sb.append("$fields").append("\n");
		for (Field field : fields) {
			sb.append(field.toString()).append("\n");
		}
		sb.append("fields$").append("\n");
		return sb.toString();
	}

	/**
	 * 올바른 메타데이터 인지 체크한다. <br>
	 * 올바른 메타데이터라면 "OK" 를 리턴한다. <br>
	 * 올바르지 않은 메타데이터라면 에러문자열을 리턴한다. <br>
	 * @return 문자열 성공시 "OK", 실패시 "\n"으로 분리가능한 에러 문자열들
	 */
	public String validCheck() {
		StringBuilder sb = new StringBuilder();
		if(StrUtils.isEmpty(id)){
			sb.append("id is empty").append("\n");
		}
		if(StrUtils.isEmpty(name)){
			sb.append("name is empty").append("\n");
		}
		if(StrUtils.isEmpty(boardType)){
			sb.append("boardType is empty").append("\n");
		}
		if(StrUtils.isEmpty(dataSourceId)){
			sb.append("dataSourceId is empty").append("\n");
		}
		if(StrUtils.isEmpty(dataSourceType)){
			sb.append("dataSourceType is empty").append("\n");
		}
		if(fields.size() == 0){
			sb.append("fields size is zero").append("\n");
		}
		String result = sb.toString();
		if(result.length() == 0){
			return "OK";
		}else{
			return result.substring(0, result.length()-1);
		}
	}


}
