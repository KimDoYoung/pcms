package kr.dcos.common.utils.table;

public class TableException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8286581771731801861L;

	//Parameterless Constructor
    public TableException() {}

    //Constructor that accepts a message
    public TableException(String message)
    {
       super(message);
    }

}
