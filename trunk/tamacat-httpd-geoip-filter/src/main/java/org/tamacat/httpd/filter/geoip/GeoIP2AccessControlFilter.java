package org.tamacat.httpd.filter.geoip;

import java.io.File;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.config.ServiceUrl;
import org.tamacat.httpd.exception.ForbiddenException;
import org.tamacat.httpd.filter.RequestFilter;
import org.tamacat.httpd.util.RequestUtils;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.ClassUtils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.model.CountryResponse;

/**
 * <p>Requested IP address AccessControlFilter using GeoIP Country database.
 * http://dev.maxmind.com/geoip/legacy/geolite/
 * 
 * <p>This product includes GeoLite2 data created by MaxMind, available from
 * <a href="http://www.maxmind.com">http://www.maxmind.com</a>.
 * <p>The GeoLite2 databases are distributed under the Creative Commons
 *  Attribution-ShareAlike 3.0 Unported License.  
 */
public class GeoIP2AccessControlFilter implements RequestFilter {
	static final Log LOG = LogFactory.getLog(GeoIP2AccessControlFilter.class);
	
    static final Pattern PRIVATE_IP_RANGE = Pattern.compile(
        	"(^127\\.0\\.0\\.1)|(^192\\.168\\.)|(^10\\.)|(^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.)|"
          + "(^100\\.6[4-9]\\.)|(^100\\.[7-9][0-9]\\.)|(^100\\.1[0-1][0-9]\\.)|(^100\\.12[0-7]\\.)");
    
	protected ServiceUrl serviceUrl;
	DatabaseReader reader;
	
	Map<String, Boolean> countries = new LinkedHashMap<>();
	boolean defaultPermit;
	boolean allowPrivateIpAddress = true;

	@Override
	public void init(ServiceUrl serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	@Override
	public void doFilter(HttpRequest request, HttpResponse response, HttpContext context) {
		String ip = RequestUtils.getRemoteIPAddress(context);
		if (isAccessAllowed(ip) == false) throw new ForbiddenException();
	}

	protected String getCountry(String ip) throws AddressNotFoundException {
		try {
			CountryResponse response = reader.country(InetAddress.getByName(ip));
			return response.getCountry().getIsoCode();
		} catch (Exception e) {
			throw new ForbiddenException(e);
		}
	}
    
	protected boolean isAccessAllowed(String ip) {
		if (PRIVATE_IP_RANGE.matcher(ip).find()) return allowPrivateIpAddress;
		try {
			Boolean result = countries.get(getCountry(ip));
			if (result != null) return result;
			else return defaultPermit;
		} catch (AddressNotFoundException e) {
			return defaultPermit;
		}
	}
	
	public void setDefaultPermit(boolean defaultPermit) {
		this.defaultPermit = defaultPermit;
	}
	
	public void setAllowPrivateIpAddress(boolean allowPrivateIpAddress) {
		this.allowPrivateIpAddress = allowPrivateIpAddress;
	}
	
	/** 
	 * Set the file of GeoIP.dat
	 * Download GeoLite Country binary database (GeoIP.dat)
	 *   http://dev.maxmind.com/geoip/legacy/geolite/
	 * @param geoipFile
	 */
	public void setGeoIpFile(String geoipFile) {
		try {
			reader = new DatabaseReader.Builder(
				new File(ClassUtils.getURL(geoipFile).toURI())).build();
		} catch (Exception e) {
			LOG.warn(e.getMessage());
			LOG.trace(e);
		}
	}
	
	/**
	 * @param country US,JP,AU (Comma-Separated Values)
	 */
	public void setAllowCountry(String country) {
		addCountry(country, true);
	}
	
	/**
	 * @param country US,JP,AU (Comma-Separated Values)
	 */
	public void setDenyCountry(String country) {
		addCountry(country, false);
	}
	
	/**
	 * 
	 * @param country US,JP,AU (Comma-Separated Values)
	 * @param mode
	 */
	protected void addCountry(String country, boolean mode) {
		if (country.indexOf(',')>=0) {
			for (String val : country.split(",")) {
				countries.put(val.trim().toUpperCase(), mode);
			}
		} else {
			countries.put(country.trim().toUpperCase(), mode);
		}
	}
}