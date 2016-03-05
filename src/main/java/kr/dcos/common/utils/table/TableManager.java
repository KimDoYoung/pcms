package kr.dcos.common.utils.table;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;


/**
 * 여러개의 KedTable을 Map에 담아서 관리한다
 * @author Kim,Do Young
 *
 */
public class TableManager {
	private String id ;
	private Map<String,Table> tableMap;
	
	public TableManager(String id){
		this.id = id;
		tableMap = new LinkedHashMap<String,Table>();
	}
	/**
	 * table; Manager�� ���ϴ� ����ҿ� �߰��Ѵ�
	 * @param nc
	 * @throws TableException 
	 */
	public void add(Table nc) {
		tableMap.put(nc.getName(), nc);
		
	}
	

	public String getId() {
		return id;
	}
	/**
	 * 
	 * ���̺�; ã�Ƽ� �������ش�. ã�� ����; ��� ���̺�; ����� �װ�; �����Ѵ�<br>
	 * 
	 * @return
	 */
	public Table getTable(String name){
		if(tableMap.containsKey(name)==false){
			Table niceTable = new Table();
			tableMap.put(name,niceTable);
			return niceTable;
		}else{
			return tableMap.get(name);
		}
	}
	public Collection<Table> getTables() {
		return tableMap.values();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (Table table : tableMap.values()){
			result = prime * result + table.hashCode();
		}
		return result;
	}
	/**
	 * �����8�� �ۼ���.
	 * �� �Ҽӵ� ���̺�� ��ϰ� �� d���� �����ش�.
	 * @return
	 */
	public String information() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("total count of table : %d\n", tableMap.size()));
		for (Entry<String,Table> entry : tableMap.entrySet()) {
			Table table = entry.getValue();
			String name = table.getName();
			int columnCount = table.getColumnsSize();
			int rowCount = table.getRowSize();
			int hashCode = table.hashCode();
			sb.append(String.format("%s-col:%d-row:%d-hashcode:%d\n",name,columnCount,rowCount,hashCode));
			if(rowCount == 0){
				sb.append(String.format("%s �� �����Ͱ� �ϳ��� ��>�ϴ�. üũ���ֽʽÿ�\n", name ));
				continue; // ���ڵ尡 ��8�� �÷�: �˻����� �ʴ´�. ������ �ϳ��� ��;�Ŵϱ�
			}
			//���̺��� ���ڵ�� �ִµ�, �÷��� ��� ��� �ִ°��� �ִٸ� �װ͵�; �����Ѵ�.
			for (Column column : table.getColumns()) {
				String colName = column.getName();
				boolean allEmpty = true;
				for (Row row : table.getRows()) {
					if(row.getString(colName).length() > 0){
						allEmpty = false;
						break;
					}
				}
				if(allEmpty){
					sb.append(String.format("%s �÷� %s �� �����Ͱ� �ϳ��� ��>�ϴ�. üũ���ֽʽÿ�\n", name ,colName));
				}
			}
		}
		return sb.toString();
	}
	
	public void putTable(String name,Table niceTable){
		tableMap.put(name,niceTable);
	}

	public void setId(String id) {
		this.id = id;
	}

	public int size() {
		return tableMap.size();
	}

	/**
	 * ��� NiceTable; �迭���·� �����Ѵ� 
	 * 
	 * @return
	 */
	public Table[] toArray() {
		return (Table[]) tableMap.values().toArray(new Table[tableMap.size()]);
	}

	public String[] toArrayOfIds(){
		return (String[]) tableMap.keySet().toArray();
	}	

	/**
	 * write all table contents in HTML Tag table
	 * @return HTML table string
	 */
	public String toHtml(){
		StringBuilder sb = new StringBuilder();
		sb.append("id:"+id+"<br/>");
		for (Table table : tableMap.values()) {
			sb.append(table.toHtml());
		}
		return sb.toString();
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("tableManager:"+id + " ��:"+tableMap.size()); sb.append("\n");
		
		for (Entry<String,Table> entry : tableMap.entrySet()) {
			sb.append("-----------------------------------------\n");
			sb.append("table:"+entry.getValue().getName()); sb.append("\n");	
			sb.append("-----------------------------------------\n");
			sb.append(entry.getValue().columnDescToString());
		}
		return sb.toString();
	}


}
