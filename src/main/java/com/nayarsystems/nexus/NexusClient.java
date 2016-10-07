package com.nayarsystems.nexus;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.core.CoreClient;
import com.nayarsystems.nexus.core.actions.PipeActions;
import com.nayarsystems.nexus.core.actions.TaskActions;
import com.nayarsystems.nexus.core.actions.TopicActions;
import com.nayarsystems.nexus.core.actions.impl.PipeActionsImpl;
import com.nayarsystems.nexus.core.actions.impl.TaskActionsImpl;
import com.nayarsystems.nexus.core.actions.impl.TopicActionsImpl;
import com.nayarsystems.nexus.core.components.Pipe;
import com.nayarsystems.nexus.core.components.Task;
import net.minidev.json.JSONObject;

import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class NexusClient extends CoreClient implements TaskActions, PipeActions, TopicActions {
    private final static Logger log = Logger.getLogger("nexus");
    private Thread pingThread;

    private final TaskActions tasks;
    private final PipeActions pipes;
    private final TopicActionsImpl topics;

    public NexusClient(URI url) {
        super(url);

        this.tasks = new TaskActionsImpl(this);
        this.pipes = new PipeActionsImpl(this);
        this.topics = new TopicActionsImpl(this);

        this.launchPing();
    }

    private void launchPing() {
        Runnable ping = () -> {
            while(true) {
                this.exec("sys.ping", null, null);
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

    public void login(String user, String password, Consumer<JSONObject> cb) {
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
    public void pullTask(String prefix, Integer timeout, Consumer<Task> cb) {
        this.tasks.pullTask(prefix, timeout, cb);
    }

    @Override
    public void pushTask(String method, Map<String, Object> parameters, Integer timeout, Boolean detach, Long prio, Long ttl, Consumer cb) {
        this.tasks.pushTask(method, parameters, timeout, detach, prio, ttl, cb);
    }

    @Override
    public void taskList(String prefix, int limit, int skip, Consumer<JSONObject> cb) {
        this.tasks.taskList(prefix, limit, skip, cb);
    }

    @Override
    public Pipe pipeOpen(String id) {
        return this.pipes.pipeOpen(id);
    }

    @Override
    public void pipeCreate(Integer length, Consumer<Pipe> cb) {
        this.pipes.pipeCreate(length, cb);
    }

    @Override
    public void topicSubscribe(Pipe pipe, String topic) {
        this.topics.topicSubscribe(pipe, topic);
    }

    @Override
    public void topicUnsubscribe(Pipe pipe, String topic) {
        this.topics.topicUnsubscribe(pipe, topic);
    }

    @Override
    public void topicPublish(String topic, Object data) {
        this.topics.topicPublish(topic, data);
    }
}

