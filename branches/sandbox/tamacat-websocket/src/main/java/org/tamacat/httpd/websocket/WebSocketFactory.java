package org.tamacat.httpd.websocket;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.HttpContext;
import org.tamacat.httpd.util.HeaderUtils;
import org.tamacat.httpd.util.RequestUtils;

public class WebSocketFactory {

	//Upgrade: WebSocket
	//Connection: Upgrade
	//Host: localhost
	//Origin: http://127.0.0.1
	public void upgrade(HttpRequest request, HttpResponse response, 
			HttpContext context, WebSocket websocket, String protocol) {
		HttpServerConnection conn = RequestUtils.getHttpServerConnection(context);
		WebSocketConnection connection = new WebSocketConnection(conn, websocket);
		String host = HeaderUtils.getHeader(request, "Host");
		String origin = checkOrigin(host, HeaderUtils.getHeader(request, "Origin"));
		
		String uri = request.getRequestLine().getUri();
		String wsUrl = "ws://" + host + uri;
		if (WebSocketUtils.isSecureWebSocket(request)) {
            String key1 = HeaderUtils.getHeader(request, "Sec-WebSocket-Key1");
            String key2 = HeaderUtils.getHeader(request, "Sec-WebSocket-Key2");
            connection.setHixieKeys(key1, key2);
            WebSocketUtils.setSecureResponseUpgradeHeader(response, origin, wsUrl, protocol);
		} else {
			WebSocketUtils.setResponseUpgradeHeader(response, origin, wsUrl, protocol);
		}
        connection.flush();

        websocket.onOpen(connection);

        context.setAttribute(
        	HttpServerConnection.class.getName()
        		+ ".__DO_NOT_CLOSED__", true);;
	}
	
    protected String checkOrigin(String host, String origin) {
        if (origin == null) {
        	origin = host;
        }
        return origin;
    }
}
