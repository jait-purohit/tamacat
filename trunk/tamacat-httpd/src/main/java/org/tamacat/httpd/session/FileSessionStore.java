package org.tamacat.httpd.session;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Set;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.IOUtils;

public class FileSessionStore implements SessionStore {

	static final Log LOG = LogFactory.getLog(FileSessionStore.class);
	
	private String directory = "./";
	private String fileNamePrefix = "";
	private String fileNameSuffix = ".ser";
	
	public void setDirectory(String directory) {
		if (! directory.endsWith("/")) directory = directory + "/";
		this.directory = directory;
	}

	public void setFileNamePrefix(String fileNamePrefix) {
		this.fileNamePrefix = fileNamePrefix;
	}
	
	public void setFileNameSuffix(String fileNameSuffix) {
		this.fileNameSuffix = fileNameSuffix;
	}
	
	@Override
	public void store(Session session) {
		synchronized (session) {
			ObjectOutputStream out = null;
			String name = directory + fileNamePrefix + session.getId() + fileNameSuffix; 
			try {
				out = new ObjectOutputStream(new FileOutputStream(name));
				out.writeObject(session);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.close(out);
			}
		}
	}

	@Override
	public Session load(String id) {
		ObjectInputStream in = null;
		String fileName = directory + fileNamePrefix + id + fileNameSuffix; 
		try {
			in = new ObjectInputStream(
					new FileInputStream(fileName));
			Session loaded = (Session) in.readObject();
			if (loaded != null) {
				return loaded;
			}
		} catch (IOException e) {
			LOG.warn(e.getMessage());
		} catch (ClassNotFoundException e) {
			LOG.warn(e.getMessage());
		} finally {
			IOUtils.close(in);
		}
		return null;
	}
	
	@Override
	public void delete(String id) {
		String fileName = directory + fileNamePrefix + id + fileNameSuffix; 
		try {
			File file = new File(fileName);
			file.deleteOnExit();
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		}
	}
	
	FilenameFilter getFileNameFilter(final String fileNameSuffix) {
		return new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(fileNameSuffix);
			}
		};
	}

	@Override
	public int getActiveSessions() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Set<String> getActiveSessionIds() {
		return null;
	}

	@Override
	public void release() {
		String[] files = new File(directory).list(
				getFileNameFilter(fileNameSuffix));
		for (String f : files) {
			try {
				File file = new File(f);
				file.deleteOnExit();
			} catch (Exception e) {
				LOG.warn(e.getMessage());
			}
		}
	}
}