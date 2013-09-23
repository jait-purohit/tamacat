package org.tamacat.cifs;

import java.io.StringWriter;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.tamacat.httpd.handler.page.VelocityListingsPage;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;

public class FileSearchVelocityListingsPage extends VelocityListingsPage {

	static final Log LOG = LogFactory.getLog(FileSearchVelocityListingsPage.class);

	public FileSearchVelocityListingsPage(Properties props) {
		super(props);
		setListingsPage("search");
	}
	
	public String getListingsPage(
			HttpRequest request, HttpResponse response, 
			VelocityContext context, List<SearchResult> files) {
		context.put("url", request.getRequestLine().getUri());

		try {
			
//			Set<Sea> list = new LinkedHashSet<CifsFile>();
//			for (SearchResult f : files) {
//				CifsFile cifs = new CifsFile();
//				cifs.setDirectory(false);
//				cifs.setName(f.getFolder()+f.getName());
//				cifs.setLength(StringUtils.parse(f.get("length"), 0L));
//				cifs.setLastModified(StringUtils.parse(f.get("lastModified"), 0L));
//				list.add(cifs);
//				System.out.println(f.get("length"));
//			}
//			CifsFile[] cifsFiles = list.toArray(new CifsFile[list.size()]);
//			//Arrays.sort(cifsFiles);
//			
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
