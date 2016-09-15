package com.nayarsystems.nexus;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.core.NexusCallback;
import com.nayarsystems.nexus.core.NexusCallbackJSON;
import com.nayarsystems.nexus.core.NexusCallbackTask;
import com.nayarsystems.nexus.network.Connection;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import net.minidev.json.JSONObject;

import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

public class NexusClient {
    private final static Logger log = Logger.getLogger("nexus");

    private final Connection connection;
    private final Map<String, NexusCallbackJSON> requestHandlers;
    private final Random idGenerator;
    private Thread pingThread;

    public NexusClient(URI url) {
        this.connection = new Connection(url);
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

    private void exec(String method, Map<String, Object> parameters, NexusCallback cb) {

        JSONRPC2Request request = new JSONRPC2Request(method, parameters, this.getId());

        if (cb != null) {
            NexusCallbackJSON callback = null;
            if (!NexusCallbackJSON.class.isInstance(cb)) {

                callback = (JSONRPC2Response response) -> {
                    Object result = response.getResult();
                    NexusTask nexusTask = new NexusTask(this, (JSONObject) result);
                    ((NexusCallbackTask) cb).handle(nexusTask);
                };

            } else {
                callback = (NexusCallbackJSON) cb;
            }

            this.requestHandlers.put((String) request.getID(), callback);

        }

        String data = request.toJSONString();
        log.fine("JSONRPC sent: " + data);
        this.connection.send(data);
    }

    private void handleMessage(String jsonMessage) {
        log.fine("JSONRPC received: " + jsonMessage);
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

    public void taskList(String prefix, int limit, int skip, NexusCallbackJSON cb) {
        this.exec("task.list", ImmutableMap.of("prefix", prefix, "limit", limit, "skip", skip), cb);
    }
}

