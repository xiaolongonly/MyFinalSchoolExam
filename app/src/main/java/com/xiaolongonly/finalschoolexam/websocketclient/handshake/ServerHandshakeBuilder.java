package com.xiaolongonly.finalschoolexam.websocketclient.handshake;

public interface ServerHandshakeBuilder extends HandshakeBuilder, ServerHandshake {
	public void setHttpStatus(short status);
	public void setHttpStatusMessage(String message);
}
