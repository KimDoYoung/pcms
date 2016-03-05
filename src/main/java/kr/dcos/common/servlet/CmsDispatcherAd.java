package kr.dcos.common.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kr.dcos.common.config.ConfigManager;
import kr.dcos.common.servlet.session.IAuthorityChecker;
import kr.dcos.common.servlet.session.SessionManager;
import kr.dcos.common.servlet.view.JspResolver;
import kr.dcos.common.servlet.view.Resolver;
import kr.dcos.common.servlet.view.View;
import kr.dcos.common.utils.StrUtils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * CmsMvc의 핵심 모듈 <br>
 * 1. 받은 url에서 class.method명을 해석하여 <br> 
 * 2. 인증과 권한을 체크한 후 <br>
 * 3. 해당 class의 method를 호출한다.<br>
 *  * 
 * @author Kim Do Young
 *
 */
public class CmsDispatcherAd extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory
			.getLogger(CmsDispatcherAd.class);
	

	private static final String POSTFIX = ".cms";
	private ControllerMethodTable methodTable;
	private Resolver viewResolver = null;
	private IAuthorityChecker authorityChecker=null;
	private String tempDir = ""; //업로드 되는 파일 올리는 위치
	public static String webRootPath;
	public static String errorPage;

	public CmsDispatcherAd() {
        super();
        viewResolver = new JspResolver();
    }
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        //
        //webRootPath를 정한다.
        //
        insertRealWebRootDirectoryToConfig();
        //
        //class-method테이블을 로드한다.
        //
        methodTable = new ControllerMethodTable();
        String configFile = webRootPath+"/"+config.getInitParameter("ClassDefinedXmlFile");
        
        logger.debug("Config File Path:"+configFile);
        File file = new File(configFile);
        if(file.exists()){
        	methodTable.load(configFile);
        }else{
        	logger.error("cms mvc config file is not exist:"+configFile);
        }
        //
        //file upload를 위한 임시 디렉토리를 설정한다.
        //
        String tempDir = config.getInitParameter("TempDir");
        if(tempDir !=null && tempDir.length()>0){
        	 String s = tempDir.replace('\\','/');
        	 if(s.endsWith("/")==false){
        		 this.tempDir = s+"/";
        	 }else{
        		 this.tempDir = s;
        	 }
        }else{
        	this.tempDir = null;
        }
        //
        //ErrorPage Setting
        //
        String errPage = config.getInitParameter("ErrorPage");
        if(errPage != null){
        	errorPage = errPage;
        }else{
        	createErrorPage("/cmsmvc_error.jsp");
        	errorPage = "/cmsmvc_error.jsp";
        }
        //application.properties를 로딩한다.
        String configFileName = webRootPath+"/"+config.getInitParameter("ApplicationConfig");
        ConfigManager.getInstance().load(new File(configFileName));
        
        //
        // AuthorityChecker를 만든다.
        //
        String authorityClassName = config.getInitParameter("AuthorityChecker");
        createAuthorityClass(authorityClassName);
        
        
//        logger.debug(methodTable.toString());
//        //logger.debug(getServletContext().getAttribute("))
//        CmsRepositorySetupServlet s = (CmsRepositorySetupServlet) getServletContext().getAttribute("cms.repository.setup.servet");
//        try {
//			repository = (Repository)s.getRepository();
//		} catch (RepositoryException e) {
//			repository = null;
//			logger.error(e.getMessage());
//			
//		}
    }
    /**
     * IAuthorityChecker를 상속받은 클래스를 생성한다.
     * 클래스의 이름은 web.xml에 기술되어진다. 
     * 
     * @param authorityClassName
     */
    private void createAuthorityClass(String authorityClassName) {
        if(authorityClassName != null && authorityClassName.length()>0){
        	
        	Class<? extends IAuthorityChecker> tmpAC; 
    		Class<?> clazz;
			try {
				clazz = Class.forName(authorityClassName);
	    		if ((tmpAC = clazz.asSubclass(IAuthorityChecker.class)) != null) {
	    			this.authorityChecker = tmpAC.newInstance();
	    		}else{
	    			logger.error(authorityClassName +" is not valid implemenation of IAuthorityChecker");
	    		}
			} catch (ClassNotFoundException e) {
				logger.error(e.getMessage());
			} catch (InstantiationException e) {
				logger.error(e.getMessage());
			} catch (IllegalAccessException e) {
				logger.error(e.getMessage());
			} 
        }
		
	}
	/**
     * cmsmvc_error.jsp를 만든다.
     */
	private void createErrorPage(String path) {
		String fn = webRootPath+path;
		PrintWriter printWriter = null;
        try {
        	String jsp = FileUtils.readFileToString(
        	        FileUtils.toFile(
        	            this.getClass().getResource("/cmsmvc_error.template")
        	        )
        	    );
        	// Will overwrite the file if exists or creates new
            printWriter = new PrintWriter( fn,  "UTF-8");
           
            printWriter.print(jsp);
        } catch (IOException e) {
        	logger.error(e.getMessage());
		} finally {
			if (printWriter != null){
				printWriter.close();
			}
        }
 
		
	}
	private void insertRealWebRootDirectoryToConfig() {
		String realPath = this.getServletContext().getRealPath(".");
		if(realPath.endsWith(".")){
			realPath = realPath.substring(0,realPath.length()-1);
		}
		webRootPath = realPath.replace('\\', '/');
		
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//request.setCharacterEncoding("UTF-8");
		dispatch(HttpMethod.GET,request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//request.setCharacterEncoding("UTF-8");
		dispatch(HttpMethod.POST,request,response);
	}
	/**
	 * 실제로 분기하는 곳
	 * @param method
	 * @param request
	 * @param response
	 */
	private void dispatch(HttpMethod method, HttpServletRequest request,
			HttpServletResponse response) {
		ControllerMethodName cmn = extractCommand(request.getRequestURI());
		String command = null;
		if(cmn!=null){
			command = cmn.toString();
			logger.debug("requestURI:" + request.getRequestURI()+" command:" + cmn.toString());
		}else{
			logger.error("requestURI:" + request.getRequestURI()+" command is null");
		}
		RequestInfo requestInfo = new RequestInfo(request, response,method,tempDir);
		//requestInfo.setRepository(repository);
		CmsMvcMethodInfo methodInfo = methodTable.get(command);

		//methodInfo = methodTable.get("LoginController.login");
		if(methodInfo == null){ // /user와 같이 path
			
			if(StrUtils.isNullOrEmpty(command)){
				methodInfo = methodTable.get("Nogada.go");
			}else{ //nogada.go1.cms
				String msg =  "command:" + command + " is not found";
				requestInfo.setErrorMessage(msg);
				logger.error(msg);

				methodInfo = methodTable.get("Nogada.error");
			}
		}

		//메소드 session-check 가 Y인지 N인지 체크
		if(methodInfo.isSessionCheck()){ //session check 해야한다면
			//login 되어 있는 상태인지 체크
			SessionManager sm = new SessionManager(request);
			sm.setAuthorityChecker(authorityChecker);
			if(!sm.authenticationCheck(cmn)){
				methodInfo = methodTable.get("LoginController.login");
			}else{
				//sessionInfo를 셋팅해준다
				request.setAttribute("sessionInfo", sm.getSessionInfo());
				//no authority for controller.method
				if(!sm.authorityCheck(cmn)){
					requestInfo.setErrorMessage("current user have no authority for "+cmn.toString());
					methodInfo = methodTable.get("Nogada.error");
				}
			}
		}		
		try {
			//authoritylimitcontroller에만 methodTable의 내용을 넘긴다. yamae
//			if( cmn.getControllerName().equalsIgnoreCase("authoritylimitcontroller") ||
//			    cmn.getControllerName().equalsIgnoreCase("ControllerController") )
//			{
//				requestInfo.setAttribute("methodTable",methodTable.getControllerList());
//			}
			Object o = methodInfo.getMethod().invoke(requestInfo);
			if(o==null){
				;//???
			}else{
				if(o instanceof String){
					String jsp = (String)o;
					renderView(jsp,request,response);
				}else if(o instanceof ModelAndView){
					ModelAndView mv = (ModelAndView)o;
					renderView(mv.getView(),mv.getModel(),request,response);
				}else if(o instanceof ForwardInfo){
					ForwardInfo fi = (ForwardInfo)o;
					Model model = fi.getModel();
					View view=null;
					if(fi.getResolver()!=null){
						view = fi.getResolver().resolve(fi.getPath());
					}else{
						view = viewResolver.resolve(fi.getPath());
					}
					renderView(view,model,request,response);
					
				}else{
					renderView("/common/error.jsp",request,response);
				}
			}
		} catch (Throwable e) {
			try {
				renderView("/common/error.jsp",request,response);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			//수행중에 error가 났다면
//			try {
//				logger.error("",e);
//				PrintWriter out = response.getWriter();
//				StackTraceElement[] st = e.getStackTrace();
//				out.print("<h3>Unexpected Error, check log</h3>");
//		        for (StackTraceElement elm : st) {
//		            String s = (elm.getClassName()+"("+elm.getLineNumber()+")" + ":" + elm.toString()+"<br>");
//		            if(s.contains("kr.dcos")){
//		            	out.print("<b>"+s+"</b><br>");
//		            }else{
//		            	out.print(s+"<br>");
//		            }
//		        }
//				out.print("<input type='button' value='back' onclick='javascript:history.go(-1)' />");
// 			} catch (Exception e1) {
//				logger.error("",e1);
//			}
		}
	}

	//
	// renderViews
	//
	private void renderView(String jsp,HttpServletRequest request,HttpServletResponse response) throws Exception {
		View view = viewResolver.resolve(jsp);
		
		renderView(view,null,request,response);
	}
	private void renderView(View view,Object model,HttpServletRequest request,HttpServletResponse response) throws Exception {
		view.render(model, request, response, false);
	}
	
	/**
	 * webAppName/nogada.go.cms에서 'nogada.go'를 뽑아낸다
	 * @param requestURI
	 * @return class.method 문자열
	 */
	private ControllerMethodName extractCommand(String requestURI) {
		int posCms = requestURI.toLowerCase().indexOf(POSTFIX);
		int start = requestURI.lastIndexOf('/')+1;
		if(posCms<0 || start<0 || start>=posCms){
			return null;
		}
		String command   = requestURI.substring(start,posCms);
		String controllerName = command.substring(0,command.indexOf('.'));
		String methodName = command.substring(command.indexOf('.')+1);
		return new ControllerMethodName(controllerName, methodName);
	}
	//
	// Resolver getter,setter
	//
    public Resolver getViewResolver() {
		return viewResolver;
	}
	public void setViewResolver(Resolver viewResolver) {
		this.viewResolver = viewResolver;
	}
}
