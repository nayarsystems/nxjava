package com.nayarsystems.nexus;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
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

    void setPrio(Long prio) {
        this.prio = prio;
    }

    public Boolean getDetach() {
        return detach;
    }

    void setDetach(Boolean detach) {
        this.detach = detach;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    void setTags(Map<String, Object> tags) {
        this.tags = tags;
    }

    public String getUser() {
        return user;
    }

    void setUser(String user) {
        this.user = user;
    }

    public void sendResult(Object data) {
        this.nexusClient.exec("task.result", ImmutableMap.of("taskid", this.id, "result", data));
    }

    public void sendError(int code, String message, Object data) {
        this.nexusClient.exec("task.error", ImmutableMap.of("taskid", this.id, "code", code, "message", message, "data", data));
    }
}
