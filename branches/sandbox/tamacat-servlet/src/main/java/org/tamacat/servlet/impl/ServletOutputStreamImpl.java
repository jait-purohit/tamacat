package org.tamacat.servlet.impl;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletOutputStream;

public class ServletOutputStreamImpl extends ServletOutputStream {

	InputStream in;
	int bufferSize;
	
	ServletOutputStreamImpl(InputStream in, int bufferSize) {
		this.in = in;
		this.bufferSize = bufferSize;
	}
	
	@Override
	public void write(int b) throws IOException {

	}

}
