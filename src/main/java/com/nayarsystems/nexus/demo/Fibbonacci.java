package com.nayarsystems.nexus.demo;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.Task;
import com.nayarsystems.nexus.network.NexusConnection;

import java.net.URI;
import java.net.URISyntaxException;

public class Fibbonacci {

    public static void main(String[] args) throws URISyntaxException {
        NexusConnection connection = new NexusConnection(new URI("ws://localhost"));
        NexusClient client = new NexusClient(connection);

        client.login("root", "root", (x) -> {
            System.out.println("Logged in");

            client.pullTask("demo", null, (Task task) -> {
                System.out.println("Request received");
                if (task.getMethod().equalsIgnoreCase("echo")) {
                    task.sendResult(task.getParameters().get("message"));
                } else {
                    task.sendError(-1, "Unknown method", null);
                }
            });

            client.pushTask("demo.echo", ImmutableMap.of("message", "Hello Nexus!"), null, (response) -> {
                System.out.println("Response received: " + response.getResult());
                client.close();
            });

        });
    }
}
