package org.tamacat.servlet.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

public class PrintWriterImpl extends PrintWriter {

	public PrintWriterImpl(StringWriter out) {
		super(out);
	}
	
	public StringWriter getWriter() {
		return (StringWriter) super.out;
	}
}
