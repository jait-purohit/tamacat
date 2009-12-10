package org.tamacat.servlet.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletInputStream;

public class ServletInputStreamImpl extends ServletInputStream {
	InputStream in;
	
	ServletInputStreamImpl(InputStream in) {
		this.in = in;
	}
	
	@Override
	public int read() throws IOException {
		return in.read();
	}

}
