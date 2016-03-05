package kr.dcos.common.utils;


/**
 * 페이지 Attribute 클래스
 * 
 * @author KDY
 * 
 */
public class PageAttribute {
	private int pageSize; // 화면에 표시될 item의 갯수
	private int startIndex; // 시작 index
	private int totalItemCount; // 총 item의 갯수
	private int totalPageCount; // 총 page의 갯수
	private int nowPageNumber; // 현재 page number


	private boolean isDisplayPrevPageNumberMark;
	private boolean isDisplayNextPageNumberMark;
	private int startPageNumber; //하단에 보여줄 시작 페이지 번호
	private int endPageNumber; //하단에 보여줄 끝 페이지 번호
	private int pageNumberDisplayCount; //페이지 번호를 몇개 보여줄 지 

	/**
	 * 생성자 
	 * @param totalItemCount 총갯수
	 * @param pageSize 한페이지에 보여줄 레코드 갯수
	 */
	public PageAttribute(int totalItemCount, int pageSize) {
		this.totalItemCount = totalItemCount;
		this.pageSize = pageSize;
		this.startIndex = 0;
		this.nowPageNumber = -1;
		this.pageNumberDisplayCount = 10;
		this.startPageNumber = 0;
		this.endPageNumber = 0;
		this.isDisplayPrevPageNumberMark = false;
		this.isDisplayNextPageNumberMark = false;
//		calculateTotalPageCount();
//		calculatePageNumbers();
		calculate();
	}
	public PageAttribute(int totalItemCount) {
		this(totalItemCount, 10);
	}

	public PageAttribute() {
		this(0, 10);
	}
	//
	// getter setter
	//
	public boolean getIsDisplayPrevPageNumberMark() {
		return isDisplayPrevPageNumberMark;
	}
	public boolean getIsDisplayNextPageNumberMark() {
		return isDisplayNextPageNumberMark;
	}
	public int getStartPageNumber() {
		return startPageNumber;
	}
	public int getEndPageNumber() {
		return endPageNumber;
	}
	public int getPageNumberDisplayCount() {
		return pageNumberDisplayCount;
	}
	public int getPageSize() {
		return pageSize;
	}

	public int getTotalItemCount() {
		return totalItemCount;
	}
	public int getStartIndex() {
		if(startIndex<0) return 0;
		return startIndex;
	}
	public int getTotalPageCount() {
		return totalPageCount;
	}
	public int getNowPageNumber(){
		return nowPageNumber;
	}

	public void setTotalItemCount(int totalItemCount) {
		this.totalItemCount = totalItemCount;
		calculate();
	}
	public void setPageNumberDisplayCount(int pageNumberDisplayCount) {
		this.pageNumberDisplayCount = pageNumberDisplayCount;
		calculate();
	}
	public void setPageSize(int pageSize) {
		if (pageSize <= 0){
			pageSize = 10;
		}
		this.pageSize = pageSize;
		calculate();
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public void setNowPageNumber(int nowPageNumber) {
		this.nowPageNumber = nowPageNumber;
		//현재페이지를 전체 페이지갯수에 맞추어서 셋팅할 필요가 있다면 계산
		//아니면 그냥 보관한다
		if(totalItemCount>0){ 
			calculate();
		}
	}
	public boolean isAvaible(){
		if(totalItemCount>0 && nowPageNumber>0 && pageSize>0) return true;
		return false;
	}
	private void calculate(){
		
		if (pageSize == 0 || totalItemCount == 0) {
			totalPageCount = 0;
		} else {
			totalPageCount = (totalItemCount + pageSize - 1) / pageSize;
		}


		if (nowPageNumber < 1){
			this.nowPageNumber = 1;
		}
		else if (nowPageNumber > this.totalPageCount){
			this.nowPageNumber = this.totalPageCount;
		}
		
		this.startIndex = (this.nowPageNumber - 1) * this.pageSize;
		
		isDisplayPrevPageNumberMark = false;
		isDisplayNextPageNumberMark = false;

		int mok = nowPageNumber / pageNumberDisplayCount;
		int nameji = nowPageNumber % pageNumberDisplayCount;
		
		if(nameji == 0) {
			startPageNumber = (mok-1) *  pageNumberDisplayCount + 1;
		}else{
			if(mok == 0) startPageNumber = 1;
			else startPageNumber = (mok*pageNumberDisplayCount)+1;
		}
		if(startPageNumber != 1) isDisplayPrevPageNumberMark = true;
		
		endPageNumber = startPageNumber + pageNumberDisplayCount -1;
		if(totalPageCount <= endPageNumber){
			endPageNumber = totalPageCount;
			isDisplayNextPageNumberMark = false;
		}else{
			isDisplayNextPageNumberMark = true;
		}
		
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("PageAttribute:\n");
		sb.append(String.format("totalItemCount:[%s]\n", getTotalItemCount()));
		sb.append(String.format("nowPageNumber:[%s]\n", getNowPageNumber()));
		sb.append(String.format("startIndex:[%s]\n", getStartIndex()));
		sb.append(String.format("pageSize:[%s]\n", getPageSize()));
		sb.append(String.format("totalPageCount:[%s]\n", getTotalPageCount()));
		sb.append(String.format("startPageNumber:[%s]\n", getStartPageNumber()));
		sb.append(String.format("endPageNumber:[%s]\n", getEndPageNumber()));
		return sb.toString();
	}

}
