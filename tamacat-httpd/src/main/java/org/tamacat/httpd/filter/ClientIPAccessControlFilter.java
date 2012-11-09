/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.ForbiddenException;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.httpd.util.SubnetUtils;
import org.tamacat.httpd.util.SubnetUtils.SubnetInfo;
import org.tamacat.util.StringUtils;

public class ClientIPAccessControlFilter implements RequestFilter {

	private HashMap<String, Integer> allows = new HashMap<String, Integer>();
	private HashMap<String, Integer> denies = new HashMap<String, Integer>();
	
	private List<SubnetInfo> allowNetmasks = new ArrayList<SubnetInfo>();
	private List<SubnetInfo> denyNetmasks = new ArrayList<SubnetInfo>();

	protected ServiceUrl serviceUrl;

	@Override
	public void doFilter(HttpRequest request, HttpResponse response,
			HttpContext context) {
		String client = RequestUtils.getRemoteIPAddress(context);
		boolean ipv6 = RequestUtils.isRemoteIPv6Address(context);
		String[] matcher = null;
		if (ipv6) {
			@SuppressWarnings("unused")
			String[] searchIp = client.split("\\:"); 
			matcher = new String[16];
			//TODO implements
		} else { //IPv4
			String[] searchIp = client.split("\\."); 
			matcher = new String[4];
			if (searchIp.length >= 4) {
				matcher[0] = searchIp[0] + ".";
				matcher[1] = matcher[0] + searchIp[1] + ".";
				matcher[2] = matcher[1] + searchIp[2] + ".";
				matcher[3] = client; //match full.
			}
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
			for (SubnetInfo allow : allowNetmasks) {
				if (allow.isInRange(client)) {
					isAllow = true;
					break;	
				}
			}
		}
		if (isAllow == false) {
			//allows only -> denied all.
			if (denies.size() == 0 && denyNetmasks.size() == 0) {
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
			for (SubnetInfo deny : denyNetmasks) {
				if (deny.isInRange(client)) {
					throw new ForbiddenException();
				}
			}
		}
	}

	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

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
		} else if (address.indexOf('/') >= 0) {
			String[] ipmask = address.split("/");
			if (ipmask.length == 2) {
				//String ip = ipmask[0];
				int netmask = StringUtils.parse(ipmask[1],0);
				if (netmask > 0) {
					//InetAddressUtils.isIPv4Address(address);
					if (isAllow) {
						allowNetmasks.add(new SubnetUtils(address).getInfo());
					} else {
						denyNetmasks.add(new SubnetUtils(address).getInfo());
					}
				}
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
