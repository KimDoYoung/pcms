package kr.dcos.common.servlet.view;

public class ImageResolver implements Resolver {

	@Override
	public View resolve(String viewName) {
		if(viewName.equalsIgnoreCase("image")){
			ImageView imageView = new ImageView();
			return imageView;
		}else{
			return null;
		}
	}

}
