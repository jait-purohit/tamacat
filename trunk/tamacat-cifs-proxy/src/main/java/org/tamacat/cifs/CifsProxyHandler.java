package org.tamacat.cifs;

import java.util.ArrayList;
import java.util.List;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.apache.velocity.VelocityContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.LocalFileHttpHandler;
import org.tamacat.httpd.exception.ForbiddenException;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.httpd.util.ResponseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

public class CifsProxyHandler extends LocalFileHttpHandler {

	static final Log LOG = LogFactory.getLog(CifsProxyHandler.class);
	
	protected SmbFileVelocityListingsPage listingPage;
	protected FileSearchVelocityListingsPage searchPage;
	protected String baseUrl;
	protected String domain = "WORKGROUP";
	protected String username;
	protected String password;

	public CifsProxyHandler() {

	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
    public void setServiceUrl(ServiceUrl serviceUrl) {
    	super.setServiceUrl(serviceUrl);
		props = PropertyUtils.getProperties("velocity.properties", getClassLoader());
		listingPage = new SmbFileVelocityListingsPage(props);
		searchPage = new FileSearchVelocityListingsPage(props);
	}
	
	/**
	 * <p>Set the welcome file.
	 * This method use after {@link #setListings}.
	 * @param welcomeFile
	 */
	public void setWelcomeFile(String welcomeFile) {
		this.welcomeFile = welcomeFile;
	}
	
	/**
	 * <p>Should directory listings be produced
	 * if there is no welcome file in this directory.</p>
	 * 
	 * <p>The welcome file becomes unestablished when I set true.<br>
	 * When I set the welcome file, please set it after having
	 * carried out this method.</p>
	 * 
	 * @param listings true: directory listings be produced (if welcomeFile is null). 
	 */
	public void setListings(boolean listings) {
		this.listings = listings;
		if (listings) {
			this.welcomeFile = null;
		}
	}
	
	public void setListingsPage(String listingsPage) {
		listingPage.setListingsPage(listingsPage);
	}
	
	protected boolean useDirectoryListings() {
		if (listings && welcomeFile == null) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void doRequest(HttpRequest request, HttpResponse response, HttpContext context) {
		String path = RequestUtils.getRequestPath(request);
		if (path.startsWith(serviceUrl.getPath()+"search")) {
			String key = RequestUtils.getParameter(context, "key");
			String value = RequestUtils.getParameter(context, "q");
			//System.out.println("key="+key+",value="+value);
			VelocityContext ctx = new VelocityContext();
			ctx.put("param", RequestUtils.getParameters(context).getParameterMap());
			ctx.put("contextRoot", serviceUrl.getPath().replaceFirst("/$",""));
			ctx.put("key", key);
			ctx.put("q", value);
			
			List<SearchResult> files;
			if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
				CifsFileSearch search = new CifsFileSearch();
				files = search.search(key, value);
			} else {
				files = new ArrayList<SearchResult>();
			}
			ctx.put("hit", files.size());
			String html = searchPage.getListingsPage(request, response, ctx, files);
			//System.out.println(html);
			response.setStatusCode(HttpStatus.SC_OK);
			ResponseUtils.setEntity(response, getEntity(html));
			return;
		}
		if ("true".equals(RequestUtils.getParameter(context, "remake"))) {
			CrawlerThread thread = new CrawlerThread();
			new Thread(thread).start();
		}
		if (path.endsWith("/") && useDirectoryListings() == false) {
			path = path + welcomeFile;
		}
		SmbFile file;
		try {
			NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, username, password);
			
			file = new SmbFile(baseUrl + getDecodeUri(path.replace(serviceUrl.getPath(), "")), auth);
			///// 404 NOT FOUND /////
			if (!file.exists()) {
				LOG.trace("File " + file.getPath() + " not found");
				throw new NotFoundException();
			}
			///// 403 FORBIDDEN /////
			else if (!file.canRead() || file.isDirectory()) {
				if (file.isDirectory() && useDirectoryListings()) {
					String html = listingPage.getListingsPage(
							request, response, file);
					response.setStatusCode(HttpStatus.SC_OK);
					ResponseUtils.setEntity(response, getEntity(html));
				} else {
					LOG.trace("Cannot read file " + file.getPath());
					throw new ForbiddenException();
				}
			}
			///// 200 OK /////
			else {
				LOG.trace("File " + file.getPath() + " found");
				response.setStatusCode(HttpStatus.SC_OK);
				ResponseUtils.setEntity(response, getFileEntity(file));
				LOG.trace("Serving file " + file.getPath());
			}
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceUnavailableException(e);
		}
	}
	
	protected HttpEntity getFileEntity(SmbFile file) {
		SmbFileEntity body = new SmbFileEntity(file, getContentType(file));
        return body;
	}
	
	/**
	 * <p>The contents type is acquired from the extension. <br>
	 * The correspondence of the extension and the contents type is
	 *  acquired from the {@code mime-types.properties} file. <br>
	 * When there is no file and the extension cannot be acquired,
	 * an {@link DEFAULT_CONTENT_TYPE} is returned. 
	 * @param file
	 * @return contents type
	 */
    protected String getContentType(SmbFile file) {
    	if (file == null) return DEFAULT_CONTENT_TYPE;
    	String fileName = file.getName();
    	String ext = fileName.substring(fileName.lastIndexOf('.')+1, fileName.length());
    	String contentType =  getMimeTypes().getProperty(ext.toLowerCase());
    	return StringUtils.isNotEmpty(contentType)? contentType : DEFAULT_CONTENT_TYPE;
    }
}
