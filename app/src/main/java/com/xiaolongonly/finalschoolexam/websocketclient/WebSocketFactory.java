package com.xiaolongonly.finalschoolexam.websocketclient;

import com.xiaolongonly.finalschoolexam.websocketclient.drafts.Draft;

import java.net.Socket;
import java.util.List;


public interface WebSocketFactory {
	public WebSocket createWebSocket(WebSocketAdapter a, Draft d, Socket s);
	public WebSocket createWebSocket(WebSocketAdapter a, List<Draft> drafts, Socket s);

}
