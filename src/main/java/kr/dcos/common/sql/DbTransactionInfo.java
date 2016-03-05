package kr.dcos.common.sql;

import java.util.ArrayList;
import java.util.List;

public class DbTransactionInfo {
	private List<DbTransactionItem> list;
	
	public DbTransactionInfo(){
		list = new ArrayList<DbTransactionItem>();
	}
	
	public void addItem(DbTransactionItem item){
		list.add(item);
	}
	public void add(String sqlId,Object param){
		list.add(new DbTransactionItem(sqlId, param));
	}
	public void add(String sql){
		list.add(new DbTransactionItem(sql));
	}

	public int getCount() {
		return list.size();
	}

	public DbTransactionItem get(int index) {
		if (index < 0 || index >= list.size()) {
			return null;
		}
		return list.get(index);
	}

	
}
