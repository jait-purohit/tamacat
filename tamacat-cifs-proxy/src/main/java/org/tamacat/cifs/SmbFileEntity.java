package org.tamacat.cifs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import org.apache.http.entity.AbstractHttpEntity;

public class SmbFileEntity extends AbstractHttpEntity {
	
    protected final SmbFile file;

    public SmbFileEntity(final SmbFile file, final String contentType) {
        if (file == null) {
            throw new IllegalArgumentException("File may not be null");
        }
        this.file = file;
        setContentType(contentType);
    }

    public boolean isRepeatable() {
        return true;
    }

    public long getContentLength() {
        try {
			return file.length();
		} catch (SmbException e) {
			return -1;
		}
    }

    public InputStream getContent() throws IOException {
        return file.getInputStream();
    }

    public void writeTo(final OutputStream outstream) throws IOException {
        if (outstream == null) {
            throw new IllegalArgumentException("Output stream may not be null");
        }
        InputStream instream = file.getInputStream();
        try {
            byte[] tmp = new byte[4096];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                outstream.write(tmp, 0, l);
            }
            outstream.flush();
        } finally {
            instream.close();
        }
    }
    
    public boolean isStreaming() {
        return false;
    }

    public Object clone() throws CloneNotSupportedException {
        //File instance is considered immutable No need to make a copy of it
        return super.clone();
    }
}
