package com.nayarsystems.nexus.core.components;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.NexusError;
import net.minidev.json.JSONObject;

import java.util.Map;

public class Task {

    private final NexusClient nexusClient;
    private final String id;
    private final String path;
    private final String method;
    private final Map<String, Object> parameters;
    private Map<String, Object> tags;
    private Long prio;
    private Boolean detach;
    private String user;

    public Task(NexusClient nexusClient, String id, String path, String method, Map<String, Object> parameters) {
        this.nexusClient = nexusClient;
        this.id = id;
        this.path = path;
        this.method = method;
        this.parameters = parameters;
    }

    public Task(NexusClient nexusClient, JSONObject result) {
        this(
                nexusClient,
                (String)result.get("taskid"),
                (String)result.get("path"),
                (String)result.get("method"),
                (JSONObject)result.get("params")
        );
        this.prio = (Long)result.get("prio");
        this.detach = (Boolean)result.get("detach");
        this.user = (String)result.get("user");
        this.tags = (JSONObject)result.get("tags");
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Long getPrio() {
        return prio;
    }

    public Boolean getDetach() {
        return detach;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    public String getUser() {
        return user;
    }

    public void sendResult(Object data) {
        this.nexusClient.exec("task.result", ImmutableMap.of("taskid", this.id, "result", data));
    }

    public void sendError(NexusError error, String message, Object data) {
        String msg = message != null ? error.getCode() + ":[" + message + "]" : null;

        this.nexusClient.exec("task.error", ImmutableMap.of("taskid", this.id, "code", error.getCode(), "message", msg, "data", data));
    }

    public void accept() {
        this.nexusClient.exec("task.result", ImmutableMap.of("taskid", this.id, "result", null));
    }

    public void reject() {
        this.nexusClient.exec("task.reject", ImmutableMap.of("taskid", this.id));
    }
}
