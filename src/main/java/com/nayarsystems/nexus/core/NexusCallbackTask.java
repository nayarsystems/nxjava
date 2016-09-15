package com.nayarsystems.nexus.core;

import com.nayarsystems.nexus.NexusTask;

@FunctionalInterface
public interface NexusCallbackTask extends NexusCallback {
    public void handle(NexusTask nexusTask);
}
