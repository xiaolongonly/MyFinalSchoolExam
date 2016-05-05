package com.xiaolongonly.finalschoolexam.websocketclient.handshake;

public interface ServerHandshake extends Handshakedata {
	public short getHttpStatus();
	public String getHttpStatusMessage();
}
