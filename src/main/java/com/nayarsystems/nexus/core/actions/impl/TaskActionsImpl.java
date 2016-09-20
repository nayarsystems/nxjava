package com.nayarsystems.nexus.core.actions.impl;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.core.actions.TaskActions;
import com.nayarsystems.nexus.core.components.Task;
import net.minidev.json.JSONObject;

import java.util.Map;
import java.util.function.Consumer;

public class TaskActionsImpl implements TaskActions {

    private final NexusClient client;

    public TaskActionsImpl(NexusClient nexusClient) {
        this.client = nexusClient;
    }

    public void pullTask(String prefix, Integer timeout, Consumer<Task> cb) {
        Map<String, Object> params = ImmutableMap.of("prefix", prefix);
        if (timeout != null) {
            params.put("timeout", timeout);
        }
        this.client.exec("task.pull", params, cb);
    }

    public void pushTask(String method, Map<String, Object> parameters, Integer timeout, Boolean detach, Long prio, Long ttl, Consumer cb) {
        Map<String, Object> params = ImmutableMap.of("method", method, "params", parameters);
        if (timeout != null) params.put("timeout", timeout);
        if (detach != null) params.put("detach", detach);
        if (prio != null) params.put("prio", prio);
        if (ttl != null) params.put("ttl", ttl);
        this.client.exec("task.push", params, cb);
    }

    public void taskList(String prefix, int limit, int skip, Consumer<JSONObject> cb) {
        this.client.exec("task.list", ImmutableMap.of("prefix", prefix, "limit", limit, "skip", skip), cb);
    }
}
