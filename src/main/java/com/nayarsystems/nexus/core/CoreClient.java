package com.nayarsystems.nexus.core;

import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.core.components.Task;
import com.nayarsystems.nexus.network.Connection;
import com.thetransactioncompany.jsonrpc2.JSONRPC2ParseException;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import net.minidev.json.JSONObject;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class CoreClient {
    private final static Logger log = Logger.getLogger("nexus");

    private final Connection connection;
    private final Map<String, Consumer<JSONRPC2Response>> requestHandlers;
    private final Random idGenerator;

    public CoreClient(URI url) {
        this.connection = new Connection(url);
        this.requestHandlers = new HashMap<>();
        this.idGenerator = new Random();

        this.connection.registerCallback((json) -> this.handleMessage(json));
    }

    private String getId() {
        int id = this.idGenerator.nextInt();
        id = id < 0 ? id * -1 : id;
        return "" + id;
    }

    public void exec(String method, Map<String, Object> parameters, Consumer cb) {

        JSONRPC2Request request = new JSONRPC2Request(method, parameters, this.getId());

        if (cb != null) {

            this.requestHandlers.put((String) request.getID(), (JSONRPC2Response response) -> {
                Object result = response.getResult();

                Task task = null;
                try {
                    task = new Task((NexusClient)this, (JSONObject)result);
                } catch (Exception e) {
                    task = null;
                } finally {
                    if (task != null && task.getId() == null) {
                        task = null;
                    }
                }

                if (task != null) {
                    ((Consumer<Task>) cb).accept(task);
                } else {
                    cb.accept(result);
                }
            });

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
            Consumer<JSONRPC2Response> cb = this.requestHandlers.remove(id);
            if (cb != null) {
                cb.accept(response);
            }
        } catch (JSONRPC2ParseException e) {
            e.printStackTrace();
        }
    }
}