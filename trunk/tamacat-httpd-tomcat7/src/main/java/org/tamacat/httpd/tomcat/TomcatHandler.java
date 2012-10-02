package org.tamacat.httpd.tomcat;

import java.io.File;
import java.net.URL;

import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.DefaultReverseUrl;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.ReverseProxyHandler;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * The reverse proxy handler using the embedded Tomcat. 
 */
public class TomcatHandler extends ReverseProxyHandler {

	static final Log LOG = LogFactory.getLog(TomcatHandler.class);
	
	protected int port = 8080;
	protected String webapps = "./webapps";
	protected String work = "./work";

	@Override
	public void handle(HttpRequest request, HttpResponse response, 
			HttpContext context) {
		context.removeAttribute(CHECK_INFINITE_LOOP);
		super.handle(request, response, context);
	}
	
	@Override
    public void setServiceUrl(ServiceUrl serviceUrl) {
    	super.setServiceUrl(serviceUrl);
    	ReverseUrl reverseUrl = new DefaultReverseUrl(serviceUrl);
    	try {
			reverseUrl.setReverse(new URL("http://localhost:" + port + serviceUrl.getPath()));
	    	serviceUrl.setReverseUrl(reverseUrl);
	    	
	    	Tomcat tomcat = TomcatManager.getInstance(port);
			tomcat.setBaseDir(work);
	    	String contextRoot = webapps + serviceUrl.getPath();

	    	//ProtectionDomain domain = TomcatHandler.class.getProtectionDomain();
			//URL location = domain.getCodeSource().getLocation();
	    	String baseDir = new File(contextRoot).getAbsolutePath();
			tomcat.addWebapp(serviceUrl.getPath().replaceAll("/$", ""), baseDir);
	    } catch (Exception e) {
	    	LOG.error(e.getMessage(), e);
	    }
    }
    
    public void setPort(int port) {
    	this.port = port;
    }
    
    public void setWebapps(String webapps) {
		this.webapps = webapps.replace("${server.home}", serverHome).replace("\\", "/");
    }
    
    public void setWork(String work) {
		this.work = work.replace("${server.home}", serverHome).replace("\\", "/").replaceAll("/work$", "");
    }
}
