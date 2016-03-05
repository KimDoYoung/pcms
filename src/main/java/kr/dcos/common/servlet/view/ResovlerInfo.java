package kr.dcos.common.servlet.view;

import java.io.InputStream;

public class ResovlerInfo {
	private String fullPath;
	private String mimeType;
	private InputStream inputStream;
	
	public ResovlerInfo(){
		;
	}

	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public String getFullPath() {
		return fullPath;
	}

	public void setFullPath(String fullPath) {
		this.fullPath = fullPath;
	}
}
