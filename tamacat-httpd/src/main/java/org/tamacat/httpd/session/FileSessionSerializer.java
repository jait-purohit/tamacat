package org.tamacat.httpd.session;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.tamacat.log.Log;
import org.tamacat.log.LogFactory;
import org.tamacat.util.IOUtils;

public class FileSessionSerializer implements SessionSerializer {

	static final Log LOG = LogFactory.getLog(FileSessionSerializer.class);
	
	private static final String DEFAULT_FILE_NAME = "session.ser";
	private String fileName = DEFAULT_FILE_NAME;
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public void serialize(SessionFactory factory) {
		synchronized (factory) {
			ObjectOutputStream out = null;
			try {
				out = new ObjectOutputStream(new FileOutputStream(fileName));
				out.writeObject(factory);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} finally {
				IOUtils.close(out);
			}
		}
	}

	@Override
	public void deserialize(SessionFactory factory) {
		synchronized (factory) {
			ObjectInputStream in = null;
			try {
				in = new ObjectInputStream(new FileInputStream(fileName));
				SessionFactory loaded = (SessionFactory) in.readObject();
				if (loaded != null) {
					factory = loaded;
				}
			} catch (IOException e) {
				LOG.warn(e.getMessage());
			} catch (ClassNotFoundException e) {
				LOG.warn(e.getMessage());
			} finally {
				IOUtils.close(in);
			}
		}
	}
}
