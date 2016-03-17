package kr.kalpa.mboard;

import kr.dcos.common.sql.exception.SqlExecutorException;

/**
 * MBoard 
 * 게시판의 생성, 수정, 삭제
 * 게시판 Item들의 생성, 수정, 삭제, 조회
 * 
 * @author Kim Do Young
 *
 */
public class MBoard {
	private MetaData metaData=null;
	
	public MBoard(MetaData metaData){
		this.metaData = metaData;
	}

	/**
	 * 설정된 메타데이터를 바탕으로 게시판을다 생성한다
	 * @throws SqlExecutorException 
	 */
	public void createBoard() throws SqlExecutorException{
		//1 create table
		
		//2 
	}
	/**
	 * 새로운 메타데이터를 바탕으로 게시판을 수정한다. <br>
	 * 새로운 메타데이터를 저장한다<br>
	 */
	public void modifyBoard(MetaData newMetaData){
		
	}
	/**
	 * 게시판을 삭제하고 메타데이터를 삭제한다 <br>
	 * 
	 */
	public void deleteBoard(){
		
	}
	/**
	 * 게시판의 item을 생성한다
	 */
	public void create(){
		
	}
	/**
	 * 게시판의 item을 생성한다
	 */
	public void list(){
		
	}
	/**
	 * 게시판의 item을 수정한다
	 */
	public void modify(){
		
	}
	/**
	 * 게시판의 item을 삭제한다
	 */
	public void delete(){
		
	}
	
	public MetaData getMetaData() {
		return metaData;
	}

	@Override
	public String toString() {
		return "MBoard";
	}

	
}
