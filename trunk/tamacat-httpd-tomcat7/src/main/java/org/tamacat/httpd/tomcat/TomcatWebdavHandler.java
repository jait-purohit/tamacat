package org.tamacat.httpd.tomcat;

import java.io.File;
import java.net.URL;

import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.DefaultReverseUrl;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.handler.ReverseProxyHandler;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * The reverse proxy handler using the embedded Tomcat. 
 */
public class TomcatWebdavHandler extends ReverseProxyHandler {

	static final Log LOG = LogFactory.getLog(TomcatWebdavHandler.class);
	
	protected int port = 8080;
	protected String webapps = "./webapps";
	protected String work = "./work";

	@Override
	public void handle(HttpRequest request, HttpResponse response, 
			HttpContext context) {
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
	    	StandardContext root = new StandardContext();
	    	root.setDefaultWebXml("web.xml");
	    	Tomcat.initWebappDefaults(root);
	    	
	    	String contextRoot = webapps + serviceUrl.getPath();

	    	//ProtectionDomain domain = TomcatHandler.class.getProtectionDomain();
			//URL location = domain.getCodeSource().getLocation();
	    	String baseDir = new File(contextRoot).getAbsolutePath();
	    	StandardContext ctx = (StandardContext)tomcat.addWebapp(serviceUrl.getPath().replaceAll("/$", "/"), baseDir);
	    	//Wrapper w = ctx.createWrapper();
			//System.out.println(serviceUrl.getPath().replaceAll("/$", "/"));
	    	//WebdavServlet servlet = new WebdavServlet();
	    	//w.addInitParameter("listings", "true");
	    	
	    	//Tomcat.addServlet(ctx, "webdav", servlet);
	    	//ctx.addServletMapping("/*", "webdav");

    	} catch (Exception e) {
	    	e.printStackTrace();
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
