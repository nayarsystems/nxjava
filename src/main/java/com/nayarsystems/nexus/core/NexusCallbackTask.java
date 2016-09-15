package com.nayarsystems.nexus.core;

import com.nayarsystems.nexus.Task;

@FunctionalInterface
public interface NexusCallbackTask extends NexusCallback {
    public void handle(Task task);
}
