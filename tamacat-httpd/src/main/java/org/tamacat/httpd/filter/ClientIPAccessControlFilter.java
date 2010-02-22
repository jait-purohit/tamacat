package org.tamacat.httpd.filter;

import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.ForbiddenException;
import org.tamacat.httpd.util.RequestUtils;

public class ClientIPAccessControlFilter implements RequestFilter {

	private HashMap<String, Integer> allows = new HashMap<String, Integer>();
	private HashMap<String, Integer> denies = new HashMap<String, Integer>();

	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context, ServiceUrl serviceUrl) {
		String client = RequestUtils.getRemoteIPAddress(context);
		String[] searchIp = client.split("\\."); 
		String[] matcher = new String[4];
		if (searchIp.length >= 4) {
			matcher[0] = searchIp[0] + ".";
			matcher[1] = matcher[0] + searchIp[1] + ".";
			matcher[2] = matcher[1] + searchIp[2] + ".";
			matcher[3] = client; //match full.
		}
		boolean isAllow = false;
		for (Entry<String, Integer> entry : allows.entrySet()) {
			String address = entry.getKey();
			int octet = entry.getValue();
			if (address.equals(matcher[octet-1]) || "*".equals(address)) {
				isAllow = true;
				break;
			}
		}
		if (isAllow == false) {
			//allows only -> denied all.
			if (denies.size() == 0) {
				throw new ForbiddenException();
			}
			//match denies -> denied.
			for (Entry<String, Integer> entry : denies.entrySet()) {
				String address = entry.getKey();
				int octet = entry.getValue();
				if (address.equals(matcher[octet-1]) || "*".equals(address)) {
					throw new ForbiddenException();
				}
			}
		}
	}

	@Override
	public void init(ServiceUrl serviceUrl) {}

	public void setAllow(String address) {
		setPattern(address, true);
	}
	
	public void setDeny(String address) {
		setPattern(address, false);
	}

	private void setPattern(String address, boolean isAllow) {
		if (address.indexOf(".*") >= 0) {
			String[] ip = address.split("\\.");
			StringBuilder pattern = new StringBuilder();
			for (int i=0; i<ip.length; i++) {
				if (pattern.length() > 0) {
					pattern.append(".");
				}
				if ("*".equals(ip[i])) {
					if (isAllow) {
						allows.put(pattern.toString(), i);
					} else {
						denies.put(pattern.toString(), i);
					}
					break;
				}
				pattern.append(ip[i]);
			}
		} else {
			//match full.
			if (isAllow) {
				allows.put(address, 4);
			} else {
				denies.put(address, 4);
			}
		}
	}
}
