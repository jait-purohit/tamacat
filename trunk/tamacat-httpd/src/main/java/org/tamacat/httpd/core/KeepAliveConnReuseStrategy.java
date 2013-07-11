package org.tamacat.httpd.core;

import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.TokenIterator;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

/**
 * ConnectionReuseStrategy for Reverse Proxy.
 */
public class KeepAliveConnReuseStrategy extends DefaultConnectionReuseStrategy {

	public static final KeepAliveConnReuseStrategy INSTANCE = new KeepAliveConnReuseStrategy();
	static final Log LOG = LogFactory.getLog(KeepAliveConnReuseStrategy.class);

	static final String HTTP_IN_CONN = "http.in-conn";

	boolean disabledKeepAlive;
	boolean alwaysKeepAlive;
	int keepAliveTimeout = 5000;

	public boolean isAlwaysKeepAlive() {
		return alwaysKeepAlive;
	}

	/**
	 * Always Keep-Alive. (Priority is given over disabledKeepAlive.)
	 * @param alwaysKeepAlive
	 * @since 1.0.6
	 */
	public void setAlwaysKeepAlive(boolean alwaysKeepAlive) {
		this.alwaysKeepAlive = alwaysKeepAlive;
	}

	public boolean isDisabledKeepAlive() {
		return disabledKeepAlive;
	}

	/**
	 * force disabled Keep-Alive.
	 * @param disabledKeepAlive
	 * @since 1.0.5
	 */
	public void setDisabledKeepAlive(boolean disabledKeepAlive) {
		this.disabledKeepAlive = disabledKeepAlive;
	}

	/**
	 * <pre>
	 * 1) alwaysKeepAlive:true -> return true.
	 * 2) disabledKeepAlive:true -> return false.
	 * 3) return super.keepAlive(response, context)
	 * </pre>
	 */
	@Override
	public boolean keepAlive(HttpResponse response, HttpContext context) {
		if (alwaysKeepAlive) {
			return true;
		} else if (disabledKeepAlive) {
			return false;
		} else {
			boolean result = keepAliveCheck(response, context);
			if (result) {
				return !isKeepAliveTimeout(context);
			}
			return false;
		}
	}

	/**
	 * @see DefaultConnectionReuseStrategy#keepAlive(HttpResponse, HttpContext)
	 * @param response
	 * @param context
	 */
	public boolean keepAliveCheck(HttpResponse response, HttpContext context) {
		if (response == null) {
			throw new IllegalArgumentException
				("HTTP response may not be null.");
		}
		if (context == null) {
			throw new IllegalArgumentException
				("HTTP context may not be null.");
		}

		// Check for a self-terminating entity. If the end of the entity will
		// be indicated by closing the connection, there is no keep-alive.
		ProtocolVersion ver = response.getStatusLine().getProtocolVersion();
		Header teh = response.getFirstHeader(HTTP.TRANSFER_ENCODING);
		if (teh != null) {
			if (!HTTP.CHUNK_CODING.equalsIgnoreCase(teh.getValue())) {
				LOG.debug("Keep-Alive: false (Transfer-Encoding: not chunked)");
				return false;
			}
		} else {
			if (canResponseHaveBody(response)) {
				Header[] clhs = response.getHeaders(HTTP.CONTENT_LEN);
				// Do not reuse if not properly content-length delimited
				if (clhs.length == 1) {
					Header clh = clhs[0];
					try {
						int contentLen = Integer.parseInt(clh.getValue());
						if (contentLen < 0) {
							LOG.debug("Keep-Alive: false (Content-Length<0 ["+contentLen+"])");
							return false;
						}
					} catch (NumberFormatException ex) {
						LOG.debug("Keep-Alive: false ("+ex+")");
						return false;
					}
				} else {
					LOG.debug("Keep-Alive: false (Content-Length!=1 ["+clhs.length+"])");
					return false;
				}
			}
		}

		// Check for the "Connection" header. If that is absent, check for
		// the "Proxy-Connection" header. The latter is an unspecified and
		// broken but unfortunately common extension of HTTP.
		HeaderIterator hit = response.headerIterator(HTTP.CONN_DIRECTIVE);
		if (!hit.hasNext())
			hit = response.headerIterator("Proxy-Connection");

		// Experimental usage of the "Connection" header in HTTP/1.0 is
		// documented in RFC 2068, section 19.7.1. A token "keep-alive" is
		// used to indicate that the connection should be persistent.
		// Note that the final specification of HTTP/1.1 in RFC 2616 does not
		// include this information. Neither is the "Connection" header
		// mentioned in RFC 1945, which informally describes HTTP/1.0.
		//
		// RFC 2616 specifies "close" as the only connection token with a
		// specific meaning: it disables persistent connections.
		//
		// The "Proxy-Connection" header is not formally specified anywhere,
		// but is commonly used to carry one token, "close" or "keep-alive".
		// The "Connection" header, on the other hand, is defined as a
		// sequence of tokens, where each token is a header name, and the
		// token "close" has the above-mentioned additional meaning.
		//
		// To get through this mess, we treat the "Proxy-Connection" header
		// in exactly the same way as the "Connection" header, but only if
		// the latter is missing. We scan the sequence of tokens for both
		// "close" and "keep-alive". As "close" is specified by RFC 2068,
		// it takes precedence and indicates a non-persistent connection.
		// If there is no "close" but a "keep-alive", we take the hint.

		if (hit.hasNext()) {
			try {
				TokenIterator ti = createTokenIterator(hit);
				boolean keepalive = false;
				while (ti.hasNext()) {
					final String token = ti.nextToken();
					if (HTTP.CONN_CLOSE.equalsIgnoreCase(token)) {
						LOG.debug("Keep-Alive: false (Connection:Close)");
						return false;
					} else if (HTTP.CONN_KEEP_ALIVE.equalsIgnoreCase(token)) {
						// continue the loop, there may be a "close" afterwards
						LOG.debug("Keep-Alive: true (Connection:Keep-Alive)");
						keepalive = true;
					}
				}
				if (keepalive) {
					return true;
				}
				// neither "close" nor "keep-alive", use default policy

			} catch (ParseException px) {
				// invalid connection header means no persistent connection
				// we don't have logging in HttpCore, so the exception is lost
				LOG.debug("Keep-Alive: false ("+px+")");
				return false;
			}
		}

		// default since HTTP/1.1 is persistent, before it was non-persistent
		boolean result = !ver.lessEquals(HttpVersion.HTTP_1_0);
		LOG.debug("Keep-Alive: "+result+" ("+ver+")");
		return result;
	}

	boolean isKeepAliveTimeout(HttpContext context) {
		ServerHttpConnection conn = (ServerHttpConnection) context.getAttribute(HTTP_IN_CONN);
		if (conn != null) {
			long connStart = conn.getConnectionStartTime();
			long end = System.currentTimeMillis() - connStart;
			if (end > keepAliveTimeout) { //timeout
				conn.setSocketTimeout(1);
				LOG.debug("keep-alive timeout[" + end + " > " + keepAliveTimeout + " msec.]) - " + conn);
				return true;
			}
		}
		return false;
	}

	private boolean canResponseHaveBody(final HttpResponse response) {
		int status = response.getStatusLine().getStatusCode();
		return status >= HttpStatus.SC_OK
			&& status != HttpStatus.SC_NO_CONTENT
			&& status != HttpStatus.SC_NOT_MODIFIED
			&& status != HttpStatus.SC_RESET_CONTENT;
	}
}
