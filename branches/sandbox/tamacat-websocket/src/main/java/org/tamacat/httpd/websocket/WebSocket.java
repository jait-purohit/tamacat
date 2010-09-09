/*
 * Copyright (c) 2010, TamaCat.org
 * All rights reserved.
 */
package org.tamacat.httpd.websocket;

import java.io.IOException;

public interface WebSocket {

	ReadyState getReadyState();
	
	void onOpen(Outbound outbound);
    void onMessage(String data);
    void onError(Throwable err);
    void onClose();
    
    //INVALID_STATE_ERR
    //SYNTAX_ERR
    public interface Outbound {
        void sendMessage(String data) throws IOException;
        void sendMessage(byte frame,String data) throws IOException;
        void sendMessage(byte frame,byte[] data) throws IOException;
        void sendMessage(byte frame,byte[] data, int offset, int length) throws IOException;
        void disconnect();
        boolean isOpen();
    }
}
