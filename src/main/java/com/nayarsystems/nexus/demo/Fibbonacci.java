package com.nayarsystems.nexus.demo;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.NexusError;
import com.nayarsystems.nexus.Task;
import com.nayarsystems.nexus.network.Connection;
import net.minidev.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class Fibbonacci {

    public static void main(String[] args) throws URISyntaxException {
        NexusClient client = new NexusClient(new URI("ws://localhost"));

        client.login("root", "root", (x) -> {
            System.out.println("Logged in");

            client.pullTask("demo", null, (Task task) -> {
                System.out.println("Request received");
                if (task.getMethod().equalsIgnoreCase("echo")) {
                    task.sendResult(task.getParameters().get("message"));
                } else {
                    task.sendError(NexusError.MethodNotFound, "Unknown method", null);
                }
            });

            client.pushTask("demo.echo", ImmutableMap.of("message", "Hello Nexus!"), null, null, null, null, (response) -> {
                System.out.println("Response received: " + response.getResult());

                client.taskList("demo", 0, 0, (listResponse) -> {
                    JSONObject result = (JSONObject)listResponse.getResult();
                    System.out.println("Pending push/pulls: " + result.get("pushes") + " / " + result.get("pulls"));
                });

                client.close();
            });

        });
    }
}
