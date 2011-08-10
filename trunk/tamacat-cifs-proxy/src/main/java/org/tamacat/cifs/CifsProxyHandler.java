package org.tamacat.cifs;

import java.util.Properties;

import jcifs.Config;
import jcifs.smb.SmbFile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
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

	private String baseUrl;
	private String username;
	private String password;

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
    public void setServiceUrl(ServiceUrl serviceUrl) {
    	super.setServiceUrl(serviceUrl);
		props = PropertyUtils.getProperties("velocity.properties", getClassLoader());
		listingPage = new SmbFileVelocityListingsPage(props);
		
		Properties properties = new Properties();
		//properties.setProperty("jcifs.netbios.wins", "192.168.0.1");
		properties.setProperty("jcifs.smb.client.username", getUsername());
		properties.setProperty("jcifs.smb.client.password", getPassword());
		Config.setProperties(properties);
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
		if (path.endsWith("/") && useDirectoryListings() == false) {
			path = path + welcomeFile;
		}
		SmbFile file;
		try {
			file = new SmbFile(baseUrl + getDecodeUri(path.replace(serviceUrl.getPath(), "")));
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
