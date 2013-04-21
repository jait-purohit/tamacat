package org.tamacat.httpd.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ReverseUrl;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.HttpException;
import org.tamacat.httpd.exception.ServiceUnavailableException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.filter.ResponseFilter;
import org.tamacat.httpd.util.ReverseUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

@Deprecated
public class PoolingReverseProxyHandler extends AbstractHttpHandler {

	static final Log LOG = LogFactory.getLog(ReverseProxyHandler.class);

	protected static final String HTTP_CONN_KEEPALIVE = "http.proxy.conn-keepalive";
    protected static final String DEFAULT_CONTENT_TYPE = "text/html; charset=UTF-8";
    protected static final String CHECK_INFINITE_LOOP
    	= ReverseProxyHandler.class.getName() + "_CHECK_INFINITE_LOOP";

//	protected HttpProcessor httpproc;
//	protected HttpParamsBuilder builder = new HttpParamsBuilder();
//	protected HttpProcessorBuilder procBuilder = new HttpProcessorBuilder();
	protected String proxyAuthorizationHeader = "X-ReverseProxy-Authorization";

	protected ReverseUrl reverseUrl;
	PoolingClientConnectionManager cm;
	DefaultHttpClient httpClient;
	
	/**
	 * <p>Default constructor.
	 */
	public PoolingReverseProxyHandler() {
//		setDefaultHttpRequestInterceptor();
	}
	
	/**
	 * <p>Get the backend server configuration parameters
	 * from the server.properties.
	 * 
	 * <p> default value is:
	 * <pre>
	 * BackEndSocketTimeout=5000
	 * BackEndConnectionTimeout=10000
	 * BackEndSocketBufferSize=8192
	 * </pre>
	 */
	@Override
    public void setServiceUrl(ServiceUrl serviceUrl) {
    	super.setServiceUrl(serviceUrl);
//    	builder.socketTimeout(serviceUrl.getServerConfig().getParam("BackEndSocketTimeout", 5000))
//    	  .connectionTimeout(serviceUrl.getServerConfig().getParam("BackEndConnectionTimeout", 10000))
//          .socketBufferSize(serviceUrl.getServerConfig().getParam("BackEndSocketBufferSize", (8*1024)));
    	
    	reverseUrl = serviceUrl.getReverseUrl();
        if (reverseUrl == null) {
        	throw new ServiceUnavailableException("reverseUrl is null.");
        }
        
        SchemeSocketFactory factory = reverseUrl.getReverse().getProtocol().equals("http")?
        		PlainSocketFactory.getSocketFactory() : SSLSocketFactory.getSocketFactory();
    	Scheme http = new Scheme(reverseUrl.getReverse().getProtocol(),
    			reverseUrl.getTargetAddress().getPort(), factory);
    	SchemeRegistry schemeRegistry = new SchemeRegistry();
    	schemeRegistry.register(http);
    	cm = new PoolingClientConnectionManager(schemeRegistry);
    	cm.setMaxPerRoute(new HttpRoute(new HttpHost(reverseUrl.getTargetAddress().getHostName())), maxPerRoute);
    	cm.setMaxTotal(maxTotal);
    	
    	httpClient = new DefaultHttpClient(cm);
    	if (keepAlive) {
	        httpClient.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
	            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
	                // Honor 'keep-alive' header
//	                HeaderElementIterator it = new BasicHeaderElementIterator(
//	                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
//	                while (it.hasNext()) {
//	                    HeaderElement he = it.nextElement();
//	                    String param = he.getName(); 
//	                    String value = he.getValue();
//	                    if (value != null && param.equalsIgnoreCase("timeout")) {
//	                        try {
//	                            return Long.parseLong(value) * 1000;
//	                        } catch(NumberFormatException ignore) {
//	                        }
//	                    }
//	                }
	                // Keep alive for 5 seconds only
	                return keepAliveDuration;
	            }
	        });
    	}
        //httpClient.addRequestInterceptor(new RequestContent());
        //httpClient.addRequestInterceptor(new RequestTargetHost());
        //httpClient.addRequestInterceptor(new RequestConnControl());
        //httpClient.addRequestInterceptor(new RequestUserAgent());
        //httpClient.addRequestInterceptor(new RequestExpectContinue());
        
        for (HttpRequestInterceptor interceptor : requestInterceptors) {
        	httpClient.addRequestInterceptor(interceptor);
        }
        for (HttpResponseInterceptor interceptor : responseInterceptors) {
        	httpClient.addResponseInterceptor(interceptor);
        }
	}
	
	
	int maxPerRoute = 10;
	int maxTotal = 20;
	
	public void setMaxPerRoute(int maxPerRoute) {
		this.maxPerRoute = maxPerRoute;
	}
	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}
	
	@Override
	public void handle(HttpRequest request, HttpResponse response, 
			HttpContext context) {
		//Bugfix: java.lang.IllegalStateException: Content has been consumed
		//RequestUtils.setParameters(request, context, encoding);
		try {
			for (RequestFilter filter : requestFilters) {
				filter.doFilter(request, response, context);
			}
			doRequest(request, response, context);
		} catch (Exception e) {
			LOG.trace(e.getMessage());
			handleException(request, response, e);
		} finally {
			for (ResponseFilter filter : responseFilters) {
				filter.afterResponse(request, response, context);
			}
		}
	}
	
    @Override
    public void doRequest(
    		HttpRequest request, HttpResponse response, 
    		HttpContext context) throws HttpException, IOException {

        // Access Backend server //
        HttpResponse targetResponse = forwardRequest(request, response, context);

        //(ReverseUrl) context.getAttribute("reverseUrl");
        ReverseUtils.copyHttpResponse(targetResponse, response);
        ReverseUtils.rewriteContentLocationHeader(request, response, reverseUrl);
        
        // Location Header convert. //
        ReverseUtils.rewriteLocationHeader(request, response, reverseUrl);
        
        // Set-Cookie Header convert. //
        ReverseUtils.rewriteSetCookieHeader(request, response, reverseUrl);
        
        // Set the entity and response headers from targetResponse.
        response.setEntity(targetResponse.getEntity());
        
        // Get the target server Connection Keep-Alive header. //
        boolean keepalive = true;//..connStrategy.keepAlive(targetResponse, context);
        LOG.trace("Keep-Alive: " + keepalive);
        context.setAttribute(HTTP_CONN_KEEPALIVE, new Boolean(keepalive));
    }

    /**
     * <p>Request forwarding to backend server.
     * @param request
     * @param response
     * @param context
     * @return {@code HttpResponse}
     */
	protected HttpResponse forwardRequest(
			HttpRequest request, HttpResponse response, HttpContext context) {
//		this.httpproc = procBuilder.build();
        LOG.trace(">> Request URI: " + request.getRequestLine().getUri());

//		Object loop = context.getAttribute(CHECK_INFINITE_LOOP);
//		if (loop == null) {
//			context.setAttribute(CHECK_INFINITE_LOOP, Boolean.TRUE);
//		} else {
//        	throw new ServiceUnavailableException("reverseUrl is infinite loop.");
//		}
		
		try {
	        context.setAttribute("reverseUrl", reverseUrl);
	        ReverseUtils.setXForwardedFor(request, context);
	        ReverseUtils.setXForwardedHost(request);
	        
	        ReverseHttpRequest targetRequest = null;
	        if (request instanceof HttpEntityEnclosingRequest) {
	        	targetRequest = new ReverseHttpEntityEnclosingRequest(request, context, reverseUrl);
	        } else {
	        	targetRequest = new ReverseHttpRequest(request, context, reverseUrl);
	        }
	        reverseUrl.countUp();
	        
	        //forward remote user.
	        ReverseUtils.setReverseProxyAuthorization(targetRequest, context, proxyAuthorizationHeader);
	        try {
	        	HttpHost host = new HttpHost(reverseUrl.getTargetAddress().getHostName());
	        	HttpResponse targetResponse = httpClient.execute(host, targetRequest, context);
		        return targetResponse;
	        } catch (Exception e) {
	        	throw e;
	        } finally {
		        reverseUrl.countDown();
		        if (LOG.isDebugEnabled()) {
		        	LOG.debug(">> "+ reverseUrl.getReverse() + ", connections=" + reverseUrl.getActiveConnections());
		        }
	        }
		} catch (SocketException e) {
			throw new ServiceUnavailableException(
				BasicHttpStatus.SC_GATEWAY_TIMEOUT.getReasonPhrase()
				+ " URL=" + reverseUrl.getReverse());
		} catch (RuntimeException e) {
			handleException(request, response, e);
			return response;
		} catch (Exception e) {
			handleException(request, response, e);
			return response;
		} finally {
			//IOUtils.close(conn);
			//IOUtils.close(outsocket);
		}
	}
	
//	/**
//	 * <p>Preset the HttpRequestInterceptor.
//	 */
//	protected void setDefaultHttpRequestInterceptor() {
//		procBuilder.addInterceptor(new RequestContent())
//        .addInterceptor(new RequestTargetHost())
//        .addInterceptor(new RequestConnControl())
//        .addInterceptor(new RequestUserAgent())
//        .addInterceptor(new RequestExpectContinue());
//	}
//	
	ArrayList<HttpRequestInterceptor> requestInterceptors = new ArrayList<HttpRequestInterceptor>();
	ArrayList<HttpResponseInterceptor> responseInterceptors = new ArrayList<HttpResponseInterceptor>();
	
	public void addHttpRequestInterceptor(HttpRequestInterceptor interceptor) {
		requestInterceptors.add(interceptor);
	}
	
	public void addHttpResponseInterceptor(HttpResponseInterceptor interceptor) {
		responseInterceptors.add(interceptor);
	}
	
	/**
	 * Set the header name of Reverse Proxy Authorization.
	 * default: "X-ReverseProxy-Authorization"
	 * @param proxyAuthorizationHeader
	 */
	public void setProxyAuthorizationHeader(String proxyAuthorizationHeader) {
		this.proxyAuthorizationHeader = proxyAuthorizationHeader;
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
	
	@Override
	protected HttpEntity getFileEntity(File file) {
		FileEntity body = new FileEntity(file, ContentType.create(getContentType(file)));
        return body;
	}
	
	boolean keepAlive = true;
	public void setKeepAlive(boolean keepAlive) {
		this.keepAlive = keepAlive;
	}
	
	long keepAliveDuration = 5 * 1024;
	public void setKeepAliveDuration(long keepAliveDuration) {
		this.keepAliveDuration = keepAliveDuration;
	}
}
