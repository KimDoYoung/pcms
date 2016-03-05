package kr.dcos.common.servlet.view;

public class FileResolver  implements Resolver {

	@Override
	public View resolve(String viewName) {
		if(viewName.equalsIgnoreCase("download")){
			DownloadFileView downloadFileView = new DownloadFileView();
			return downloadFileView;
		}else{
			return null;
		}
	}

}
