package com.nayarsystems.nexus.core.actions.impl;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.core.actions.TaskActions;
import com.nayarsystems.nexus.core.components.Task;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class TaskActionsImpl implements TaskActions {

    private final NexusClient client;

    public TaskActionsImpl(NexusClient nexusClient) {
        this.client = nexusClient;
    }

    public void pullTask(String prefix, Integer timeout, BiConsumer<Task, JSONRPC2Error> cb) {
        Map<String, Object> params = new HashMap<>();
        params.put("prefix", prefix);
        if (timeout != null) {
            params.put("timeout", timeout);
        }
        this.client.exec("task.pull", params, cb);
    }

    public void pushTask(String method, Map<String, Object> parameters, Integer timeout, Boolean detach, Long prio, Long ttl, BiConsumer cb) {
        Map<String, Object> params = new HashMap<>();
        params.put("method", method);
        params.put("params", parameters);
        if (timeout != null) params.put("timeout", timeout);
        if (detach != null) params.put("detach", detach);
        if (prio != null) params.put("prio", prio);
        if (ttl != null) params.put("ttl", ttl);
        this.client.exec("task.push", params, cb);
    }

    public void taskList(String prefix, int limit, int skip, BiConsumer cb) {
        this.client.exec("task.list", ImmutableMap.of("prefix", prefix, "limit", limit, "skip", skip), cb);
    }
}
