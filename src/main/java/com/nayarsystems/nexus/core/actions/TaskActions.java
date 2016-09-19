package com.nayarsystems.nexus.core.actions;

import com.nayarsystems.nexus.core.components.Task;

import java.util.Map;
import java.util.function.Consumer;

public interface TaskActions {
    void pullTask(String prefix, Integer timeout, Consumer<Task> cb);
    void pushTask(String method, Map<String, Object> parameters, Integer timeout, Boolean detach, Long prio, Long ttl, Consumer cb);
    void taskList(String prefix, int limit, int skip, Consumer cb);
}
