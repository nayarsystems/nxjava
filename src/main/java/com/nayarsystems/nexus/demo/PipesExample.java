package com.nayarsystems.nexus.demo;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.core.components.Pipe;
import com.nayarsystems.nexus.core.components.Task;

import java.net.URI;
import java.net.URISyntaxException;

public class PipesExample {

    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("ws://localhost");

        new Thread(() -> {
            NexusClient client = new NexusClient(uri);

            client.login("root", "root", (x) -> {

                client.pullTask("demo.pipes", null, (Task task) -> {
                    String pipeId = (String) task.getParameters().get("pipe");

                    Pipe pipe = client.pipeOpen(pipeId);

                    pipe.write("Processing...", (res) -> {

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        task.sendResult(ImmutableMap.of("res","ok"), (r) -> client.close());
                    });
                });

            });

        }).start();


        new Thread(() -> {
            NexusClient client = new NexusClient(uri);

            client.login("root", "root", (x) -> {

                client.pipeCreate(1, (pipe) -> {
                    System.out.println("Pipe created with ID " + pipe.getId());

                    pipe.read(1, 1, (data) -> {
                        System.out.println(data);
                    });

                    client.pushTask("demo.pipes.test", ImmutableMap.of("pipe", pipe.getId()), null, null, null, null, (response) -> {
                        System.out.println(response);

                        pipe.close((r) -> client.close());
                    });

                });
            });

        }).start();
    }
}
