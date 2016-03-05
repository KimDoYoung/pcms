package kr.kalpa;

import java.net.URL;
import java.security.ProtectionDomain;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class Main {

	public static final String PORT = "8888";

    public static final String WEBAPP_DIR = "src/main/webapp";

    public static final String WEBAPP_CTX = "/";
    
    /**
     * jetty main
     * 
     * run exmaples : 
     * 	java -jar myapp.war
	 *  java -Dport=8443 -jar myapp.war
	 *  
     * @param args
     * @throws Exception
     */
	public static void main(String[] args) throws Exception
    {
		int port = Integer.parseInt(System.getProperty("port", PORT));
        Server server = new Server(port);

        ProtectionDomain domain = Main.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation();

                
        WebAppContext context = new WebAppContext();

        context.setResourceBase(WEBAPP_DIR);
        context.setContextPath(WEBAPP_CTX);
        context.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");
        context.setWar(location.toExternalForm());
        
        context.setServer(server);
        
        server.setHandler(context);

        server.start();
        server.join();
       
    }
}
