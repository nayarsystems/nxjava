package com.nayarsystems.nexus.core;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;

@FunctionalInterface
public interface NexusCallbackJSON extends NexusCallback {
    public void handle(JSONRPC2Response response);
}
