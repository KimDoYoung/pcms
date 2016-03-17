package kr.kalpa.mboard;

public class MBoardException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -3107274318384375323L;

	public MBoardException() {}

    public MBoardException(String message)
    {
       super(message);
    }
}
