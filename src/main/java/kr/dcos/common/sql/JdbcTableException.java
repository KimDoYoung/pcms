package kr.dcos.common.sql;

import kr.dcos.common.utils.table.TableException;

public class JdbcTableException extends TableException {

	public JdbcTableException(String msg) {
		super("JdbcTable Exception-"+msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1345097375596309656L;

}
