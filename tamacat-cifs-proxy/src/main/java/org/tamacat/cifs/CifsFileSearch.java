package org.tamacat.cifs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.tamacat.io.RuntimeIOException;
import org.tamacat.util.IOUtils;
import org.tamacat.util.PropertyUtils;

public class CifsFileSearch {

	String indexDir;
	String url;
	String weburl;
	
	public CifsFileSearch() {
		Properties props = PropertyUtils.getProperties("crawler.properties");
		indexDir = props.getProperty("indexDir");
		url = props.getProperty("url");
		weburl = props.getProperty("weburl");
	}
	
	public List<SearchResult> search(String key, String value) {
		List<SearchResult> files = new ArrayList<>();
	    IndexReader reader = null;
	    try {
	    	reader = DirectoryReader.open(FSDirectory.open(new File(indexDir)));
	    
		    IndexSearcher searcher = new IndexSearcher(reader);
		    Analyzer analyzer = new CJKAnalyzer(Version.LUCENE_44);
		    
		    QueryParser parser = new QueryParser(Version.LUCENE_44, key, analyzer);
		    int max = 100;
		    //Query query = new TermQuery(new Term(key, value));
		    TopDocs rs = searcher.search(parser.parse(value), null, max);
		    //System.out.println("hits: "+rs.totalHits);
		    for (int i=0; i<rs.scoreDocs.length; i++) {
		    	//Query query = parser.parse("");
		    	Document firstHit = searcher.doc(rs.scoreDocs[i].doc);
		    	SearchResult file = new SearchResult();
		    	file.setUrl(firstHit.get("url").replace(url, weburl));
		    	file.setFolder(firstHit.get("folder"));
		    	file.setName(firstHit.get("name"));
		    	file.setLastModified(firstHit.get("lastModified"));
		    	file.setLength(firstHit.get("length"));	
		    	files.add(file);
		    	if (i>100) break;
		    }
	    } catch (IOException e) {
	    	throw new RuntimeIOException(e);
	    } catch (Exception e) {
	    	throw new RuntimeException(e);
	    } finally {
	    	IOUtils.close(reader);
	    }
	    return files;
	}
}
