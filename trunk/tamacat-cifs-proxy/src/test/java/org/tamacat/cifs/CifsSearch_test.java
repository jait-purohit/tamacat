package org.tamacat.cifs;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class CifsSearch_test {
	
	public static void main(String[] args) throws Exception {
	    IndexReader reader = IndexReader.open(FSDirectory.open(new File("./src/test/resources/index/")));
	    IndexSearcher searcher = new IndexSearcher(reader);

	    Query query = new TermQuery(new Term("name", "pdf"));
	    TopDocs rs = searcher.search(query, null, 100);
	    System.out.println(rs.totalHits);
	    for (int i=0; i<rs.totalHits; i++) {
	    	Document firstHit = searcher.doc(rs.scoreDocs[i].doc);
	    	System.out.println(firstHit.get("folder")+firstHit.get("name"));
	    }
	}

}
