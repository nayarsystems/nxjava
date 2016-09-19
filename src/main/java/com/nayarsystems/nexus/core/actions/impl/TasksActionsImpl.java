package com.nayarsystems.nexus.core.actions.impl;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.core.NexusCallbackJSON;
import com.nayarsystems.nexus.core.NexusCallbackTask;
import com.nayarsystems.nexus.core.actions.TaskActions;

import java.util.Map;

public class TasksActionsImpl implements TaskActions {

    private final NexusClient client;

    public TasksActionsImpl(NexusClient nexusClient) {
        this.client = nexusClient;
    }

    public void pullTask(String prefix, Integer timeout, NexusCallbackTask cb) {
        Map<String, Object> params = ImmutableMap.of("prefix", prefix);
        if (timeout != null) {
            params.put("timeout", timeout);
        }
        this.client.exec("task.pull", params, cb);
    }

    public void pushTask(String method, Map<String, Object> parameters, Integer timeout, Boolean detach, Long prio, Long ttl, NexusCallbackJSON cb) {
        Map<String, Object> params = ImmutableMap.of("method", method, "params", parameters);
        if (timeout != null) params.put("timeout", timeout);
        if (detach != null) params.put("detach", detach);
        if (prio != null) params.put("prio", prio);
        if (ttl != null) params.put("ttl", ttl);
        this.client.exec("task.push", params, cb);
    }

    public void taskList(String prefix, int limit, int skip, NexusCallbackJSON cb) {
        this.client.exec("task.list", ImmutableMap.of("prefix", prefix, "limit", limit, "skip", skip), cb);
    }
}
