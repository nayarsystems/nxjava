package com.nayarsystems.nexus.core.actions;

import com.nayarsystems.nexus.core.NexusCallbackJSON;
import com.nayarsystems.nexus.core.NexusCallbackTask;

import java.util.Map;

public interface TaskActions {
    void pullTask(String prefix, Integer timeout, NexusCallbackTask cb);
    void pushTask(String method, Map<String, Object> parameters, Integer timeout, Boolean detach, Long prio, Long ttl, NexusCallbackJSON cb);
    void taskList(String prefix, int limit, int skip, NexusCallbackJSON cb);
}
