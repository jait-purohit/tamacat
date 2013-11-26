package org.tamacat.cifs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
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
import org.apache.pdfbox.lucene.LucenePDFDocument;
import org.tamacat.io.RuntimeIOException;
import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.IOUtils;

import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileFilter;

public class CifsCrawler {
	static final Log LOG = LogFactory.getLog(CifsCrawler.class);
	
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
	Analyzer analyzer = new CJKAnalyzer(Version.LUCENE_44);
	
	public CifsCrawler(String indexDir) {
		try {
			dir = FSDirectory.open(new File(indexDir));//new RAMDirectory();
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_44, analyzer);
			iwc.setOpenMode(OpenMode.CREATE);
			writer = new IndexWriter(dir, iwc);
		} catch (IOException e) {
			throw new RuntimeIOException(e);
		}
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
					LOG.info("add " + f.getCanonicalPath());
					
					if (file.getName().toLowerCase().endsWith(".pdf")) {
						try {
							writer.addDocument(
								getPDFDocument(cifsFile.getFile().getInputStream())
							);
						} catch (IOException e) {
						}
					}
					Document doc = getDocument(cifsFile);
					writer.addDocument(doc);
				}
			}
			CifsFile[] files = list.toArray(new CifsFile[list.size()]);
			Arrays.sort(files);
			cifsFiles.add(file.getPath(), files);
			LOG.info("add " + url);
		} catch (Exception e) {
			throw new CifsFileException(e);
		}
	}
	
	Document getDocument(CifsFile file) {
		Document doc = new Document();
		doc.add(new Field("url", getFolderUrl(file.getSmbFile())+file.getName(), Field.Store.YES,Field.Index.ANALYZED));
		doc.add(new Field("folder", getFolderUrl(file.getSmbFile()), Field.Store.YES,Field.Index.ANALYZED));
		doc.add(new Field("name", file.getName(), Field.Store.YES,Field.Index.ANALYZED));
		doc.add(new Field("length", file.getLength(), Field.Store.YES,Field.Index.NO));
		doc.add(new Field("lastModified", file.getLastModified(), Field.Store.YES, Field.Index.NO));
		return doc;
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
	
	String getFileUrl(String path) {
		if (baseUrl != null) {
			return path.replaceFirst(baseUrl, webUrl);
		} else {
			return path;
		}
	}
	
	String getFolderUrl(SmbFile file) {
		if (baseUrl != null) {
			return file.getParent().replaceFirst(baseUrl, webUrl);
		} else {
			return file.getParent();
		}
	}
	
	public void close() {
		IOUtils.close(writer);
	}
	
	Document getPDFDocument(InputStream is) throws IOException {
		Document doc = LucenePDFDocument.getDocument(is);
		return doc;
	}
}
