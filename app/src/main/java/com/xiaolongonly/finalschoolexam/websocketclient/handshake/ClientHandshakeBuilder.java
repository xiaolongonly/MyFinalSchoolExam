package com.xiaolongonly.finalschoolexam.websocketclient.handshake;

public interface ClientHandshakeBuilder extends HandshakeBuilder, ClientHandshake {
	public void setResourceDescriptor(String resourceDescriptor);
}
