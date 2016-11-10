package com.nayarsystems.nexus.demo;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.core.components.Pipe;
import com.nayarsystems.nexus.core.components.Task;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;

import java.net.URI;
import java.net.URISyntaxException;

public class PipesExample {

    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("ws://localhost");

        NexusClient client = new NexusClient(uri);

        client.login("root", "root", (x, errorLogin) -> {

            if (errorLogin != null) {
                System.err.println("Error in login: " + errorLogin.toString());
                client.close();
            } else {

                client.pullTask("demo.pipes", null, (Task task, JSONRPC2Error error) -> {
                    if (error != null) {
                        System.err.println("Error in pull: " + error.toString());
                        client.close();
                    } else {

                        String pipeId = (String) task.getParameters().get("pipe");

                        Pipe pipe = client.pipeOpen(pipeId);

                        pipe.write("Processing...", (res, err) -> {

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            task.sendResult(ImmutableMap.of("res", "ok"), (r, e) -> client.close());
                        });
                    }
                });


                client.pipeCreate(1, (pipe) -> {
                    System.out.println("Pipe created with ID " + pipe.getId());

                    pipe.read(1, 1, (data, err) -> {
                        System.out.println(data);
                    });

                    client.pushTask("demo.pipes.test", ImmutableMap.of("pipe", pipe.getId()), null, null, null, null, (response, error) -> {
                        if (error != null) {
                            System.err.println("Error in push: " + error.toString());
                            client.close();
                        } else {

                            System.out.println(response);

                            pipe.close((r, e) -> client.close());
                        }
                    });

                });
            }
        });

    }
}
