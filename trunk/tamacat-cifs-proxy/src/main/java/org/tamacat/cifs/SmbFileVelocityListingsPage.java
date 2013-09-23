package org.tamacat.cifs;

import java.io.StringWriter;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.tamacat.httpd.handler.page.VelocityListingsPage;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class SmbFileVelocityListingsPage extends VelocityListingsPage {

	static final Log LOG = LogFactory.getLog(SmbFileVelocityListingsPage.class);

	public SmbFileVelocityListingsPage(Properties props) {
		super(props);
	}
	
	public String getListingsPage(
			HttpRequest request, HttpResponse response,
			SmbFile file) {
		VelocityContext context = new VelocityContext();
		return getListingsPage(request, response, context, file);
	}
	
	public String getListingsPage(
			HttpRequest request, HttpResponse response, 
			VelocityContext context, SmbFile file) {
		
		try {
			context.put("url", URLDecoder.decode(request.getRequestLine().getUri(),"UTF-8"));
			if (request.getRequestLine().getUri().lastIndexOf('/') >= 0) {
				context.put("parent", "../");
			}
			SmbFile[] smbfiles = file.listFiles(new SmbFileFilter() {
				@Override
				public boolean accept(SmbFile pathname) {
					try {
						return ! pathname.isHidden()
						&& ! pathname.getName().startsWith(".");
					} catch (SmbException e) {
						return false;
					}
				}
			});
			
			Set<CifsFile> list = new LinkedHashSet<CifsFile>();
			for (SmbFile f : smbfiles) {
				list.add(new CifsFile(f));
			}
			CifsFile[] files = list.toArray(new CifsFile[list.size()]);
			Arrays.sort(files);
			context.put("list", files);

   			Template template = getTemplate(listingsPage + ".vm");
   			StringWriter writer = new StringWriter();
   			template.merge(context, writer);
   			return writer.toString();
    	} catch (Exception e) {
    		LOG.trace(e.getMessage());
    		return DEFAULT_ERROR_500_HTML;
    	}
    }
}
