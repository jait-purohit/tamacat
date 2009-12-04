package org.tamacat.httpd.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.core.RequestParameters;

public class RequestUtils {

	static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	static final String REQUEST_PARAMETERS_CONTEXT_KEY = "RequestParameters";
	
	public static void setParameters(HttpRequest request, HttpContext context) throws IOException {
		String path = request.getRequestLine().getUri();
		//String path = docsRoot + request.getRequestLine().getUri();
		RequestParameters parameters =  new RequestParameters();
		if (path.indexOf('?') >= 0) {
			String[] requestParams = path.split("\\?");
			path = requestParams[0];
			//set request parameters for Custom HttpRequest.
			if (requestParams.length >= 2) {
				String params = requestParams[1];
				String[] param = params.split("&");
				for (String kv : param) {
					String[] p = kv.split("=");
					if (p.length >=2) {
						parameters.setParameter(p[0], p[1]);
					}
				}
			}
		}
		Header contentType = request.getFirstHeader(HTTP.CONTENT_TYPE);
		if (contentType != null && CONTENT_TYPE_FORM_URLENCODED.equalsIgnoreCase(contentType.getValue())) {
			if (request instanceof HttpEntityEnclosingRequest) {
				HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
				if (entity != null) {
					InputStream in = entity.getContent();
					BufferedReader reader = new BufferedReader(new InputStreamReader(in));
					String s;
					StringBuilder sb = new StringBuilder();
					while ((s = reader.readLine()) != null) {
						sb.append(s);
					}
					String[] params = sb.toString().split("&");
					for (String param : params) {
						String[] keyValue = param.split("=");
						if (keyValue.length >= 2) {
							parameters.setParameter(keyValue[0], keyValue[1]);
						}
					}
				}
			}
		}
		context.setAttribute(REQUEST_PARAMETERS_CONTEXT_KEY, parameters);
	}
	
	public static RequestParameters getParameters(HttpContext context) {
		return (RequestParameters) context.getAttribute(REQUEST_PARAMETERS_CONTEXT_KEY);
	}
	
	public static String getParameter(HttpContext context, String name) {
		RequestParameters params = getParameters(context);
		return params != null ? params.getParameter(name) : null;
	}
	
	public static String[] getParameters(HttpContext context, String name) {
		RequestParameters params = getParameters(context);
		return params != null ? params.getParameters(name) : null;
	}
	
	public static Set<String> getParameterNames(HttpContext context) {
		RequestParameters params = getParameters(context);
		return params != null ? params.getParameterNames() : null;
	}
}
