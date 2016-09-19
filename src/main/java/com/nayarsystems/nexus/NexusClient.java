package com.nayarsystems.nexus;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.core.CoreClient;
import com.nayarsystems.nexus.core.NexusCallbackJSON;
import com.nayarsystems.nexus.core.NexusCallbackTask;
import com.nayarsystems.nexus.core.actions.TaskActions;
import com.nayarsystems.nexus.core.actions.impl.TasksActionsImpl;

import java.net.URI;
import java.util.Map;
import java.util.logging.Logger;

public class NexusClient extends CoreClient implements TaskActions {
    private final static Logger log = Logger.getLogger("nexus");

    private Thread pingThread;

    private final TasksActionsImpl tasks;

    public NexusClient(URI url) {
        super(url);

        this.tasks = new TasksActionsImpl(this);

        this.launchPing();
    }

    private void launchPing() {
        Runnable ping = () -> {
            while(true) {
                this.exec("sys.ping", null);
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    return;
                }
            }
        };
        this.pingThread = new Thread(ping);
        this.pingThread.start();
    }

    public void login(String user, String password, NexusCallbackJSON cb) {
        this.exec("sys.login", ImmutableMap.of("user", user, "pass", password), cb);
    }

    public void close() {
        this.pingThread.interrupt();
        try {
            this.pingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void pullTask(String prefix, Integer timeout, NexusCallbackTask cb) {
        this.tasks.pullTask(prefix, timeout, cb);
    }

    @Override
    public void pushTask(String method, Map<String, Object> parameters, Integer timeout, Boolean detach, Long prio, Long ttl, NexusCallbackJSON cb) {
        this.tasks.pushTask(method, parameters, timeout, detach, prio, ttl, cb);
    }

    @Override
    public void taskList(String prefix, int limit, int skip, NexusCallbackJSON cb) {
        this.tasks.taskList(prefix, limit, skip, cb);
    }
}

