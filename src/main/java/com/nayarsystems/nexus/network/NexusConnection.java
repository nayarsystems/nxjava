package com.nayarsystems.nexus.network;

import java.net.URI;

public class NexusConnection {
    private WebSocketClient connection;

    public NexusConnection(URI url) {
        this.connection = new WebSocketClient(url);
    }

    public void registerCallback(MessageHandler cb) {
        this.connection.addMessageHandler(cb);
    }

    public void send(String data) {
        this.connection.sendMessage(data);
    }
}
