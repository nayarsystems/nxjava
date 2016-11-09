package com.nayarsystems.nexus.core.actions.impl;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.core.actions.PipeActions;
import com.nayarsystems.nexus.core.components.Pipe;
import net.minidev.json.JSONObject;

import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class PipeActionsImpl implements PipeActions {
    private final static Logger log = Logger.getLogger("nexus.pipes");

    private final NexusClient client;

    public PipeActionsImpl(NexusClient nexusClient) {
        this.client = nexusClient;
    }

    @Override
    public Pipe pipeOpen(String id) {
        return new Pipe(client, id);
    }

    @Override
    public void pipeCreate(Integer length, Consumer<Pipe> cb) {
        Map<String, Object> params = null;
        if (length != null) {
            params = ImmutableMap.of("len", length);
        }
        this.client.exec("pipe.create", params, (response) -> {
            JSONObject json = (JSONObject) response;
            String pipeId = (String) json.get("pipeid");

            log.finer("Pipe " + pipeId + " created");
            if (cb != null) {
                cb.accept(new Pipe(client, pipeId));
            }
        });
    }
}
