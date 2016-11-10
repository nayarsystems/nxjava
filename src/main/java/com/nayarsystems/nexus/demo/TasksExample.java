package com.nayarsystems.nexus.demo;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.NexusError;
import com.nayarsystems.nexus.core.components.Task;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class TasksExample {

    public static void main(String[] args) throws URISyntaxException {
        NexusClient client = new NexusClient(new URI("ws://localhost"));

        client.login("root", "root", (x, errorLogin) -> {
            if (errorLogin != null) {
                System.err.println("Error in login: " + errorLogin.toString());
                client.close();
            } else {
                System.out.println("Logged in");

                client.pullTask("demo.tasks", null, (Task task, JSONRPC2Error error) -> {
                    if (error != null) {
                        System.err.println("Error in pull: " + error.toString());
                        client.close();
                    } else {
                        System.out.println("Request received");
                        if (task.getMethod().equalsIgnoreCase("echo")) {
                            task.sendResult(task.getParameters().get("message"), null);
                        } else {
                            task.sendError(NexusError.MethodNotFound, "Unknown method", null, null);
                        }
                    }
                });

                client.pushTask("demo.tasks.echo", ImmutableMap.of("message", "Hello Nexus!"), null, null, null, null, (response, error) -> {
                    if (error != null) {
                        System.err.println("Error in push: " + error.toString());
                        client.close();
                    } else {
                        System.out.println("Response received: " + response);

                        client.taskList("demo", 0, 0, (taskListResponse, err) -> {
                            JSONArray listResponse = (JSONArray) taskListResponse;
                            System.out.println(listResponse);
                            listResponse.forEach((item) -> {
                                JSONObject task = (JSONObject) item;
                                System.out.println(task);
                            });

                            client.close();
                        });
                    }
                });
            }

        });
    }
}
