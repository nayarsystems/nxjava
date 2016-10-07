package com.nayarsystems.nexus.demo;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.NexusError;
import com.nayarsystems.nexus.core.components.Task;
import net.minidev.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class TasksExample {

    public static void main(String[] args) throws URISyntaxException {
        NexusClient client = new NexusClient(new URI("ws://localhost"));

        client.login("root", "root", (x) -> {
            System.out.println("Logged in");

            client.pullTask("demo.tasks", null, (Task task) -> {
                System.out.println("Request received");
                if (task.getMethod().equalsIgnoreCase("echo")) {
                    task.sendResult(task.getParameters().get("message"), null);
                } else {
                    task.sendError(NexusError.MethodNotFound, "Unknown method", null, null);
                }
            });

            client.pushTask("demo.tasks.echo", ImmutableMap.of("message", "Hello Nexus!"), null, null, null, null, (response) -> {
                System.out.println("Response received: " + response);

                client.taskList("demo", 0, 0, (listResponse) -> {
                    System.out.println("Pending push/pulls: " + listResponse.get("pushes") + " / " + listResponse.get("pulls"));
                });

                client.close();
            });

        });
    }
}
