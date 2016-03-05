package kr.dcos.common.servlet.view;

public class JsonResolver implements Resolver {

	@Override
	public View resolve(String viewName) {
		return new JsonView();
	}
}
