package kr.dcos.common.servlet;

import java.util.List;

import kr.dcos.common.servlet.view.ErrorResolver;
import kr.dcos.common.servlet.view.FileResolver;
import kr.dcos.common.servlet.view.HtmlResolver;
import kr.dcos.common.servlet.view.ImageResolver;
import kr.dcos.common.servlet.view.JsonResolver;
import kr.dcos.common.servlet.view.LayoutResolver;
import kr.dcos.common.servlet.view.Resolver;
import kr.dcos.common.utils.SuccessInfo;
import kr.dcos.common.utils.ValueText;


/**
 * Command 실행후에 보여줘야할 jsp의 내용에 대한 정보 <br>
 * if Each handler meet exception or error , can carry them use errorMessage class <br>
 * 모델과 resovler를 가지고 있다. resolver는 Dispatcher에서 기본으로 JspResolver를 사용한다<br>
 * 그러므로 LayoutResolver 를 사용하기 위해서는 각각의 Method에서 LayoutResovler를 생성해야한다<br>
 * default는 jspResovler로 한다<br>
 * 
 * @author Kim Do Young
 * 
 */
public class ForwardInfo {
	//private final static String ErrorPage = "/common/cms_error.jsp";
	private String ErrorPage;
	private Model model;
	private String path=null;
	private Resolver resolver; // layoutresovler
	private JspErrorManager errorManager = null;

	public static Resolver ResolverFactory(ViewType resolverType){
		if(resolverType == ViewType.HTML){
			return new HtmlResolver();
		}else if(resolverType == ViewType.JSON){ 
			return new JsonResolver();
		}else if(resolverType == ViewType.DOWNLOAD){
			return new FileResolver();
		}else if(resolverType == ViewType.IMAGE){
			return new ImageResolver();
		}else if(resolverType == ViewType.ERROR){
			return new ErrorResolver();
		}
		return null;
	}
	/**
	 * constructor 
	 * @param path
	 * @param resolver
	 */
	public ForwardInfo(String path,String controllerName,Resolver resolver) {
		model = new Model();
		if(resolver != null){
			this.resolver = resolver;
			if(path != null){
				this.path = path;
			}else{
				if(resolver instanceof HtmlResolver){
					this.path = "html";
				}else if(resolver instanceof JsonResolver){
					this.path = "json";
				}else if(resolver instanceof FileResolver){
					this.path = "download";
				}else if(resolver instanceof ImageResolver){
					this.path = "image";
				}else if(resolver instanceof ErrorResolver){
					this.path = CmsDispatcherAd.errorPage;
					this.resolver = null; //jsp resolver를 사용
				}
			}
			
		}else{
			setPath(path);
			this.resolver = null;
		}
		
		this.ErrorPage = CmsDispatcherAd.errorPage;
		this.errorManager = new JspErrorManager();
		model.addAttribute("errorManager", errorManager); //errorManager를 항상 넣는다.
		model.addAttribute("controllerName", controllerName);
	}
	public ForwardInfo(Resolver resolver){
		this(null,null,resolver);
	}
	public ForwardInfo(String path) {
		this(path,null,null);
	}
	public ForwardInfo(String controllerName,String path){
		this(path,controllerName,null);
	}
	public ForwardInfo(ViewType resolverType){
		this(ForwardInfo.ResolverFactory(resolverType));
	}

	//
	// ErrorManager 
	//
	public void addErrorMessage(String key,String message){
		errorManager.add(key, message);
	}
	public void addErrorMessages(String key, String[] validCheckArray) {
		errorManager.add(key, validCheckArray);
	}
	public void addErrorMessages(String key,List<String> validCheckList){
		if(validCheckList == null) return;
		for (String error : validCheckList) {
			errorManager.add(key, error);
		}
	}
	public ForwardInfo setError(String path,String errorMessage){
		setPath(path);
		addErrorMessage("unknown", errorMessage);
		return this;
	}
	public ForwardInfo setError(String errorMessage) {
		return setError(ErrorPage,errorMessage);
	}

	public void clearError(){
		errorManager.clear();
	}
	
	public ForwardInfo setSuccess(String path, String msg, String title,List<ValueText> buttons ) {
		SuccessInfo successInfo = new SuccessInfo();
		successInfo.setTitle(title);
		successInfo.setMessage(msg);
		successInfo.addButtons(buttons);
		return setSuccess(path,successInfo);
	}
	public ForwardInfo setSuccess(String path,SuccessInfo successInfo){
		setPath(path);
		setAttribute("successInfo", successInfo);
		return this;
	}
	//
	// model에 데이터를 넣는다
	//
	public void addAttribute(String name,Object value){
		if(value == null) return;
		model.addAttribute(name, value);
	}
	public void setAttribute(String name, Object value) {
		addAttribute(name,value);
	}
	
	//
	//getter,setter
	//
	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		if( this.path == null || this.path.equals(path)==false){
			this.path = path;
			this.model.deleteAllMessage();
		}
	}

	public Resolver getResolver() {
		return resolver;
	}
	public void setResolver(Resolver resolver) {
		this.resolver = resolver;
	}
	public void setLayoutResolver(String layoutName){
		if(this.resolver != null){
			resolver = null;
		}
		resolver = new LayoutResolver(layoutName);
	}

	public boolean hasError() {
		return (errorManager.size() > 0);
	}

}
