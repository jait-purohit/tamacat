package org.tamacat.cifs;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.tamacat.io.RuntimeIOException;
import org.tamacat.util.IOUtils;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;

public class CifsCrawler {
	
	private CifsFileManager cifsFiles = new CifsFileManager();
	private boolean recursive = true;
	private String domain = "WORKGROUP";
	private String username;
	private String password;
	String baseUrl;
	String webUrl;

	Directory dir;
	IndexWriter writer;
	IndexDeletionPolicy deletionPolicy = new KeepOnlyLastCommitDeletionPolicy(); 
	Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_35);
	
	public CifsCrawler(String indexDir) {
		try {
			dir = FSDirectory.open(new File(indexDir));//new RAMDirectory();
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
			writer = new IndexWriter(dir, iwc);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
	}
	
	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public void setRecursive(boolean recursive) {
		this.recursive = recursive;
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public CifsFileManager getCifsFileManager() {
		return cifsFiles;
	}
	
	public CifsFile[] getCifsFiles(String path) {
		return cifsFiles.getCifsFiles(path);
	}
	
	String getFolderPath(SmbFile file) {
		return baseUrl != null ? file.getParent().replace(webUrl, baseUrl) : file.getParent();
	}
	
	public void crawler(String url) {
		if (webUrl == null) webUrl = url;
		NtlmPasswordAuthentication auth = new NtlmPasswordAuthentication(domain, username, password);
		try {
			SmbFile file = new SmbFile(url, auth);
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
				if (recursive && f.isDirectory()) {
					crawler(f.getCanonicalPath());
				} else {
					CifsFile cifsFile = new CifsFile(f);
					list.add(cifsFile);
					
					System.out.println(getFolderPath(f));
					Document doc = getDocument(cifsFile);
					writer.addDocument(doc);
				}
			}
			CifsFile[] files = list.toArray(new CifsFile[list.size()]);
			Arrays.sort(files);
			cifsFiles.add(file.getPath(), files);
		} catch (Exception e) {
			throw new CifsFileException(e);
		}
	}
	
	Document getDocument(CifsFile file) {
		Document doc = new Document();
		doc.add(new Field("folder", getFolderPath(file.getSmbFile()), Field.Store.YES,Field.Index.NO));
		doc.add(new Field("name", file.getName(), Field.Store.YES,Field.Index.ANALYZED));
		doc.add(new Field("size", file.getLength(), Field.Store.YES,Field.Index.NO));
		doc.add(new Field("date", file.getLastModified(), Field.Store.YES,Field.Index.NOT_ANALYZED_NO_NORMS));
		return doc;
	}
	
	public void close() {
		IOUtils.close(writer);
	}
}
