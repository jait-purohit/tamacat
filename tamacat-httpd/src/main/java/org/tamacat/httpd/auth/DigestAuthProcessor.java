/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.UnauthorizedException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.util.StringUtils;
import org.tamacat.util.UniqueCodeGenerator;

/**
 * <p>Implements of Digest authentication.
 */
public class DigestAuthProcessor extends AbstractAuthProcessor implements RequestFilter {

	static final String AUTHORIZATION = "Authorization";
	static final String WWW_AUTHENTICATE = "WWW-Authenticate";

	private String realm = "Authentication required";

	private String algorithm = "MD5";
	private String qop = "auth";
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context, ServiceUrl serviceUrl) {
		try {
			String remoteUser = checkUser(request, context);
			context.setAttribute(remoteUserKey, remoteUser);
		} catch (UnauthorizedException e) {
			response.setStatusCode(HttpStatus.SC_UNAUTHORIZED);
			setWWWAuthenticateHeader(response);
			throw e;
		}
	}

	protected String checkUser(HttpRequest request, HttpContext context)
			throws UnauthorizedException {
		Header digestAuthLine = request.getFirstHeader(AUTHORIZATION);
		if (digestAuthLine != null && StringUtils.isNotEmpty(digestAuthLine.getValue())) {
			String line = digestAuthLine.getValue().replaceFirst("Digest ", "");
			Digest digest = new Digest(line);

			if (authComponent != null) {
				AuthUser user = authComponent.getAuthUser(digest.getUsername(),	context);
				String hashedPassword = null;
				if (user == null) new UnauthorizedException();
				if (user.isEncrypted() == false) {
					//A1 = username:realm:password
					String a1 = user.getAuthUsername() + ":"
						+ realm + ":" + user.getAuthPassword();
					String hash1 = encode(getMD5(a1));
					
					//A2 = Method:URI
					String a2 = request.getRequestLine().getMethod()	+ ":" + request.getRequestLine().getUri();
					String hash2 = encode(getMD5(a2));

					//Digest = A1:nonce:nonce-count:cnonce:qop:A2
					String digestPassword = hash1 + ":" + digest.getNonce() 
					  + ":" + digest.getNc() + ":" + digest.getCnonce()
					  + ":" + digest.getQop() + ":" + hash2;
					String hash3 = encode(getMD5(digestPassword));
					hashedPassword = hash3; //user.setAuthPassword(hash3);
				}
				String username = digest.getUsername();
				String password = digest.getResponse();
				if (username != null && password != null
						&& username.equals(user.getAuthUsername())
						&& password.equals(hashedPassword)) {
					return user.getAuthUsername();
				}
			}
		}
		throw new UnauthorizedException();
	}

	protected void setWWWAuthenticateHeader(HttpResponse response) {
		response.addHeader(WWW_AUTHENTICATE, "Digest realm=\"" + realm + "\", "
				+ "nonce=\"" + generateNonce() + "\", " + "algorithm=" + algorithm
				+ ", qop=\"" + qop + "\"");
	}

	protected String generateNonce() {
		return UniqueCodeGenerator.generate();
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public void setQop(String qop) {
		this.qop = qop;
	}

	static class Digest {
		private Map<String, String> params = new LinkedHashMap<String, String>();

		Digest(String line) {
			String[] params = line.split(",");
			for (String keyValue : params) {
				String[] param = keyValue.trim().split("=");
				if (param != null && param.length >= 2) {
					String key = param[0].trim();
		       		StringBuilder value = new StringBuilder(param[1]);
		       		if (param.length > 2) {
		       			for (int i=2; i<param.length; i++) {
		       				value.append("=" + param[i]);
		       			}
		       		}
		       		setParam(key, 
		       			value.toString().replaceFirst("^\"", "")
		       			.replaceFirst("\"$", ""));
				}
			}
		}
		
		public void setParam(String key, String value) {
			this.params.put(key, value);
		}

		public Map<String, String> getParams() {
			return params;
		}

		public String getUsername() {
			return params.get("username");
		}

		public String getRealm() {
			return params.get("realm");
		}

		public String getNonce() {
			return params.get("nonce");
		}

		public String getUri() {
			return params.get("uri");
		}

		public String getAlgorithm() {
			return params.get("algorithm");
		}

		public String getResponse() {
			return params.get("response");
		}

		public String getQop() {
			return params.get("qop");
		}

		public String getNc() {
			return params.get("nc");
		}

		public String getCnonce() {
			return params.get("cnonce");
		}
	}

    /**
     * https://svn.apache.org/repos/asf/httpcomponents/httpclient/trunk/module-client/src/main/java/org/apache/http/impl/auth/DigestScheme.java
     * Hexa values used when creating 32 character long digest in HTTP DigestScheme
     * in case of authentication.
     * 
     * @see #encode(byte[])
     */
    private static final char[] HEXADECIMAL = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 
        'e', 'f'
    };
    
    /**
     * https://svn.apache.org/repos/asf/httpcomponents/httpclient/trunk/module-client/src/main/java/org/apache/http/impl/auth/DigestScheme.java
     * 
     * Encodes the 128 bit (16 bytes) MD5 digest into a 32 characters long 
     * <CODE>String</CODE> according to RFC 2617.
     * 
     * @param binaryData array containing the digest
     * @return encoded MD5, or <CODE>null</CODE> if encoding failed
     */
    private static String encode(byte[] binaryData) {
        int n = binaryData.length; 
        char[] buffer = new char[n * 2];
        for (int i = 0; i < n; i++) {
            int low = (binaryData[i] & 0x0f);
            int high = ((binaryData[i] & 0xf0) >> 4);
            buffer[i * 2] = HEXADECIMAL[high];
            buffer[(i * 2) + 1] = HEXADECIMAL[low];
        }

        return new String(buffer);
    }
    
	public byte[] getMD5(String plainText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			return md.digest(plainText.getBytes());
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}

}
