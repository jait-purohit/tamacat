package org.tamacat.cifs;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.velocity.VelocityContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.core.AbstractHttpHandler;
import org.tamacat.httpd.exception.NotFoundException;
import org.tamacat.httpd.page.VelocityListingsPage;
import org.tamacat.httpd.page.VelocityPage;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.httpd.util.ResponseUtils;
import org.tamacat.io.RuntimeIOException;
import org.tamacat.util.PropertyUtils;
import org.tamacat.util.StringUtils;

public class CifsFileSearchHandler extends AbstractHttpHandler {

	public static final String CONTENT_TYPE = "ResponseHeader__ContentType__";
	protected String welcomeFile = "index";
	protected boolean listings;
	
	protected VelocityListingsPage listingPage;
	protected VelocityPage page;
	protected final Set<String> urlPatterns = new HashSet<String>();

	public void setUrlPatterns(String patterns) {
		for (String pattern : patterns.split(",")) {
			urlPatterns.add(pattern.trim());
		}
	}
	
	public boolean isMatchUrlPattern(String path) {
		if (urlPatterns.size() > 0) {
			for (String pattern : urlPatterns) {
				if (pattern.endsWith("/") && path.matches(pattern)) {
					return true;
				} else if (path.lastIndexOf(pattern) >= 0) {
					return true;
				}
			}
		} else if (path.lastIndexOf(".html") >= 0) {
			return true;
		}
		return false;
	}
	
	@Override
    public void setServiceUrl(ServiceUrl serviceUrl) {
    	super.setServiceUrl(serviceUrl);
    	Properties props = PropertyUtils.getProperties("velocity.properties", getClassLoader());
		listingPage = new VelocityListingsPage(props);
		page = new VelocityPage(props);
		page.init(this.docsRoot);
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
		if (listings) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected void doRequest(HttpRequest request, HttpResponse response,
			HttpContext context) {
		try {
			VelocityContext ctx = (VelocityContext) context.getAttribute(VelocityContext.class.getName());
			if (ctx == null) ctx = new VelocityContext();
			String path = RequestUtils.getRequestPath(request);
			ctx.put("url", path);
			ctx.put("param", RequestUtils.getParameters(context).getParameterMap());
			ctx.put("contextRoot", serviceUrl.getPath().replaceFirst("/$",""));
			
			//super.doRequest(request, response, context);
			String key = RequestUtils.getParameter(context, "key");
			String value = RequestUtils.getParameter(context, "q");
			if (StringUtils.isNotEmpty(key) && StringUtils.isNotEmpty(value)) {
				CifsFileSearch search = new CifsFileSearch();
				search.search(key, value);
			}
			setEntity(request, response, ctx, serviceUrl.getPath()+"search");

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeIOException(e);
		}
	}


	protected void setListFileEntity(HttpRequest request, HttpResponse response, File file) {
		try {
			String html = listingPage.getListingsPage(
					request, response, file);
			ResponseUtils.setEntity(response, getEntity(html));
			response.setStatusCode(HttpStatus.SC_OK);
		} catch (Exception e) {
			throw new NotFoundException(e);
		}
	}
	
	protected void setEntity(HttpRequest request, HttpResponse response, VelocityContext ctx, String path) {
		//Do not set an entity when it already exists.
		if (response.getEntity() == null) {
			String html = page.getPage(request, response, ctx, path);
			Object contentType = ctx.get(CONTENT_TYPE);
			if (contentType != null && contentType instanceof String) {
				ResponseUtils.setEntity(response, getEntity(html, (String)contentType));
			} else {
				ResponseUtils.setEntity(response, getEntity(html));
			}
		}
	}
	
	protected void setFileEntity(HttpRequest request, HttpResponse response, String path) {
		//Do not set an entity when it already exists.
		if (response.getEntity() == null) {
			try {
				File file = new File(docsRoot + getDecodeUri(path));//r.toURI());
				if (file.exists() == false) {
					throw new NotFoundException();
				}
				ResponseUtils.setEntity(response, getFileEntity(file));
			} catch (Exception e) {
				throw new NotFoundException(e);
			}
		}
	}

	protected HttpEntity getEntity(String html, String contentType) {
		try {
			StringEntity entity = new StringEntity(html, encoding);
			entity.setContentType(contentType);
			return entity;
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}
	
	@Override
	protected HttpEntity getEntity(String html) {
		try {
			StringEntity entity = new StringEntity(html, encoding);
			entity.setContentType(DEFAULT_CONTENT_TYPE);
			return entity;
		} catch (UnsupportedEncodingException e1) {
			return null;
		}
	}
	
	protected HttpEntity getFileEntity(File file, String contentType) {
		FileEntity body = new FileEntity(file, ContentType.create(contentType, encoding));
        return body;
	}
	
	@Override
	protected HttpEntity getFileEntity(File file) {
		FileEntity body = new FileEntity(file,  ContentType.create(getContentType(file)));
        return body;
	}
}
