package com.nayarsystems.nexus.demo;

import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.core.components.Pipe;

import java.net.URI;
import java.net.URISyntaxException;

public class TopicsExample {

    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("ws://localhost");

//        new Thread(() -> {
//
//            NexusClient client = new NexusClient(uri);
//
//            client.login("root", "root", (r) -> {
//                client.pipeCreate(1, (pipe) -> {
//                    client.topicSubscribe(pipe, "demo.topics.t1");
//
//                    fetchMessages(pipe, "T1", 500);
//                });
//            });
//
//        }).start();

        new Thread(() -> {

            NexusClient client = new NexusClient(uri);

            client.login("root", "root", (r) -> {
                client.pipeCreate(1, (pipe) -> {
                    client.topicSubscribe(pipe, "demo.topics.t1");

                    fetchMessages(pipe, "T2", 2000);

                });
            });

        }).start();


        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            NexusClient client = new NexusClient(uri);

            client.login("root", "root", (r) -> {
                client.topicPublish("demo.topics.t1", "Hello Nexus!");
                client.topicPublish("demo.topics.t1", "This is awesome!");
                client.topicPublish("demo.topics.t1", "Looking forward to see you again!");
            });
        }).start();


    }

    private static void fetchMessages(Pipe pipe, String prefix, int timeout) {
        while(true) {
            pipe.read(1, timeout, (response) -> {
                System.out.println(prefix + ": " + response);
            });
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    ;
};
