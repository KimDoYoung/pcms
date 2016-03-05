package kr.dcos.common.servlet;

/**
 * upload한 파일에 대한 정보를 담고 있는 클래스 
 * 
 * @author Kim Do Young
 *
 */
public class FileJeongBo {
	//private SuccessFail successFail;
	private String htmlTagName;
	private String fullPath;
	private String fileName;
	private long size;
	private String contentType;
	private String errorMessage;
	private int width=0;
	private int height=0;
	private String url;
	
	
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean hasError() {
		return (errorMessage!=null); 
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("FileName:" + fullPath);
		sb.append(" size:" + size);
		sb.append(" contentType:" + contentType);
		sb.append(" message:" + errorMessage);
		return sb.toString();
	}
	public String getHtmlTagName() {
		return htmlTagName;
	}
	public void setHtmlTagName(String htmlTagName) {
		this.htmlTagName = htmlTagName;
	}

	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFullPath() {
		return fullPath;
	}
	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
}
