package com.nayarsystems.nexus.core.components;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.function.Consumer;
import java.util.function.Function;
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
        this.client.exec("pipe.read", ImmutableMap.of("pipeid", this.pipeId, "max", max, "timeout", timeout), cb);
    }

    public String getId() {
        return this.pipeId;
    }

    /**
     * This method keeps reading messages from the pipe until the callback handler returns true for any of the processed messages
     * @param callback
     */
    public void readUntil(Function<JSONObject, Boolean> callback) {
        this.read(1, 0, (res) -> {
            if (((JSONArray) res.get("msgs")).size() == 0) {
                this.readUntil(callback);
            } else {
                if (!callback.apply(res)) {
                    this.readUntil(callback);
                }
            }
        });
    }
}
