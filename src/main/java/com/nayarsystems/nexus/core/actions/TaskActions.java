package com.nayarsystems.nexus.core.actions;

import com.nayarsystems.nexus.core.components.Task;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import net.minidev.json.JSONObject;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface TaskActions {
    void pullTask(String prefix, Integer timeout, BiConsumer<Task, JSONRPC2Error> cb);
    void pushTask(String method, Map<String, Object> parameters, Integer timeout, Boolean detach, Long prio, Long ttl, BiConsumer cb);
    void taskList(String prefix, int limit, int skip, BiConsumer cb);
}
