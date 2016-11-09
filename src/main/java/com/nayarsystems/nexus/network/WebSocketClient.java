package com.nayarsystems.nexus.network;

import javax.websocket.*;
import java.net.URI;
import java.util.logging.Logger;

@ClientEndpoint
public class WebSocketClient {
    private static Logger log = Logger.getLogger("nexus.wsclient");

    Session userSession = null;
    private MessageHandler messageHandler;

    public WebSocketClient(URI endpointURI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, endpointURI);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Callback hook for Connection open events.
     *
     * @param userSession the userSession which is opened.
     */
    @OnOpen
    public void onOpen(Session userSession) {
        this.userSession = userSession;
    }

    /**
     * Callback hook for Connection close events.
     *
     * @param userSession the userSession which is getting closed.
     * @param reason the reason for connection close
     */
    @OnClose
    public void onClose(Session userSession, CloseReason reason) {
        this.userSession = null;
    }

    @OnError
    public void onError(Throwable exception, Session session) {
        log.severe("Error for client: " + session.getId() + " " + exception.toString());
        exception.printStackTrace(System.err);
    }

    @OnMessage
    public void onMessage(String message) {
        log.finest("<- " + message);
        if (this.messageHandler != null) {
            this.messageHandler.handleMessage(message);
        }
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.messageHandler = msgHandler;
    }

    public void sendMessage(String message) {
        log.finest("-> " + message);
        this.userSession.getAsyncRemote().sendText(message);
    }

}