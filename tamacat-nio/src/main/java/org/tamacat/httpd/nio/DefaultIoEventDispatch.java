/*
 * Copyright (c) 2009, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.nio;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.apache.http.impl.nio.DefaultServerIOEventDispatch;
import org.apache.http.nio.NHttpServiceHandler;
import org.apache.http.nio.reactor.EventMask;
import org.apache.http.nio.reactor.IOEventDispatch;
import org.apache.http.nio.reactor.IOSession;
import org.apache.http.params.HttpParams;

//for test
public class DefaultIoEventDispatch
		extends DefaultServerIOEventDispatch implements IOEventDispatch {
	private final ByteBuffer buffer = ByteBuffer.allocate(1024);
    
	public DefaultIoEventDispatch(
			NHttpServiceHandler handler,
			HttpParams params) {
		super(handler, params);
	}
	
	@Override
    public void connected(IOSession session) {
        System.out.println("connected");
        session.setEventMask(EventMask.READ);
        session.setSocketTimeout(20000);
    }

    public void inputReady(final IOSession session) {
        System.out.println("readable");
        try {
            this.buffer.compact();
            int bytesRead = session.channel().read(this.buffer);
            if (this.buffer.position() > 0) {
                session.setEventMask(EventMask.READ_WRITE);
            }
            System.out.println("Bytes read: " + bytesRead);
            if (bytesRead == -1) {
                session.close();
            }
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex.getMessage());
        }
    }

    public void outputReady(final IOSession session) {
        System.out.println("writeable");
        try {
            this.buffer.flip();
            int bytesWritten = session.channel().write(this.buffer);
            if (!this.buffer.hasRemaining()) {
                session.setEventMask(EventMask.READ);
            }
            System.out.println("Bytes written: " + bytesWritten);
        } catch (IOException ex) {
            System.err.println("I/O error: " + ex.getMessage());
        }
    }

    public void timeout(final IOSession session) {
        System.out.println("timeout");
        session.close();
    }
    
    public void disconnected(final IOSession session) {
        System.out.println("disconnected");
    }
}
