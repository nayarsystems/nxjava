package com.nayarsystems.nexus.core.components;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import net.minidev.json.JSONObject;

import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class Pipe {
    private final static Logger log = Logger.getLogger("nexus.pipes");

    private final NexusClient client;
    private final String pipeId;

    public Pipe(NexusClient nexusClient, String pipeId) {
        this.client = nexusClient;
        this.pipeId = pipeId;
    }

    public void close(Consumer<JSONObject> cb) {
        this.client.exec("pipe.close", ImmutableMap.of("pipeid", this.pipeId), cb);
    }

    public void write(String data, Consumer<JSONObject> cb) {
        this.client.exec("pipe.write", ImmutableMap.of("pipeid", this.pipeId, "msg", data), cb);
    }

    public void read(int max, int timeout, Consumer<JSONObject> cb) {
        this.client.exec("pipe.read", ImmutableMap.of("pipeid", this.pipeId, "max", max, "timeout", timeout / 1000), cb);
    }

    public String getId() {
        return this.pipeId;
    }
}
