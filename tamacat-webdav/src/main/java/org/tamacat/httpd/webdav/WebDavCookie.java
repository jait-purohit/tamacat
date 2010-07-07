/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.webdav;

import java.util.Date;

import org.apache.http.cookie.SetCookie;

import com.bradmcevoy.http.Cookie;

public class WebDavCookie implements Cookie {

	private SetCookie cookie;
	
	WebDavCookie(org.apache.http.cookie.Cookie cookie) {
		if (cookie instanceof SetCookie) {
			this.cookie = (SetCookie)cookie;
		}
	}
	
	@Override
	public String getDomain() {
		return cookie.getDomain();
	}

	@Override //TODO
	public int getExpiry() {
		return 0;
	}

	@Override
	public String getName() {
		return cookie.getName();
	}

	@Override
	public String getPath() {
		return cookie.getPath();
	}

	@Override
	public boolean getSecure() {
		return cookie.isSecure();
	}

	@Override
	public String getValue() {
		return cookie.getValue();
	}

	@Override
	public int getVersion() {
		return cookie.getVersion();
	}

	@Override
	public void setDomain(String domain) {
		cookie.setDomain(domain);
	}

	@Override
	public void setExpiry(int maxAge) {
		cookie.setExpiryDate(new Date(new Date().getTime()+maxAge));
	}

	@Override
	public void setPath(String path) {
		cookie.setPath(path);
	}

	@Override
	public void setSecure(boolean secure) {
		cookie.setSecure(secure);
	}

	@Override
	public void setValue(String value) {
		cookie.setValue(value);
	}

	@Override
	public void setVersion(int version) {
		cookie.setVersion(version);
	}

}
