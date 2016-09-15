package com.nayarsystems.nexus;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.nayarsystems.nexus.core.NexusCallbackJSON;
import com.nayarsystems.nexus.core.NexusCallbackTask;
import com.nayarsystems.nexus.network.NexusConnection;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import net.minidev.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NexusClient {
    private final NexusConnection connection;
    private final Map<String, NexusCallbackJSON> requestHandlers;
    private final Random idGenerator;
    private Thread pingThread;

    public NexusClient(NexusConnection connection) {
        this.connection = connection;
        this.connection.registerCallback((json) -> this.handleMessage(json));
        this.requestHandlers = new HashMap<>();
        this.idGenerator = new Random();

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

    private String getId() {
        int id = this.idGenerator.nextInt();
        id = id < 0 ? id * -1 : id;
        return "" + id;
    }

    void exec(String method, Map<String, Object> parameters) {
        this.exec(method, parameters, (NexusCallbackJSON)null);
    }

    private void exec(String method, Map<String, Object> parameters, NexusCallbackTask cb) {
        this.exec(method, parameters, (JSONRPC2Response response) -> {
            JSONObject result = (JSONObject) response.getResult();

            Task task = new Task(
                    this,
                    (String)result.get("taskid"),
                    (String)result.get("path"),
                    (String)result.get("method"),
                    (JSONObject)result.get("params")
            );
            task.setPrio((Long)result.get("prio"));
            task.setDetach((Boolean)result.get("detach"));
            task.setUser((String)result.get("user"));
            task.setTags((JSONObject)result.get("tags"));

            cb.handle(task);
        });
    }

    private void exec(String method, Map<String, Object> parameters, NexusCallbackJSON cb) {
        JSONRPC2Request request = new JSONRPC2Request(method, parameters, this.getId());
        if (cb != null) {
            this.requestHandlers.put((String) request.getID(), cb);
        }

        String data = request.toJSONString();
        this.connection.send(data);
    }

    private void handleMessage(String jsonMessage) {
        try {
            JSONRPC2Response response = JSONRPC2Response.parse(jsonMessage);
            String id = (String)response.getID();
            NexusCallbackJSON cb = this.requestHandlers.remove(id);
            if (cb != null) {
                cb.handle(response);
            }
        } catch (JSONRPC2ParseException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        this.pingThread.interrupt();
        try {
            this.pingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pullTask(String prefix, Integer timeout, NexusCallbackTask cb) {
        Map<String, Object> params = ImmutableMap.of("prefix", prefix);
        if (timeout != null) {
            params.put("timeout", timeout);
        }
        this.exec("task.pull", params, cb);
    }

    public void pushTask(String method, Map<String, Object> parameters, Integer timeout, Boolean detach, Long prio, Long ttl, NexusCallbackJSON cb) {
        Map<String, Object> params = ImmutableMap.of("method", method, "params", parameters);
        if (timeout != null) params.put("timeout", timeout);
        if (detach != null) params.put("detach", detach);
        if (prio != null) params.put("prio", prio);
        if (ttl != null) params.put("ttl", ttl);
        this.exec("task.push", params, cb);
     }


}

