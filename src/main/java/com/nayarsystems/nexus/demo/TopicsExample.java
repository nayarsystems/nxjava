package com.nayarsystems.nexus.demo;

import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.core.components.Pipe;

import java.net.URI;
import java.net.URISyntaxException;

public class TopicsExample {
    static URI uri = null;

    static {
        try {
            uri = new URI("ws://localhost");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws URISyntaxException {

        Thread t1 = createConsumer("T1");
        Thread t2 = createConsumer("T2");
        Thread t3 = createConsumer("T3");

        new Thread(() -> {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            NexusClient client = new NexusClient(uri);

            client.login("root", "root", (r) -> {
                System.out.println("Publishing messages...");

                client.topicPublish("demo.topics.test", "Hello Nexus!");
                client.topicPublish("demo.topics.test", "This is awesome!");
                client.topicPublish("demo.topics.test", "Looking forward to see you again!");

                System.out.println("Publish done!");

                try {
                    t1.join();
                    t2.join();
                    t3.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    client.close();
                    System.out.println("Bye!");
                }

            });

        }).start();

    }

    private static Thread createConsumer(String prefix) throws URISyntaxException {

        Thread t = new Thread(() -> {

            NexusClient client = new NexusClient(uri);

            client.login("root", "root", (r) -> {
                client.pipeCreate(1, (pipe) -> {
                    System.out.println(prefix + ": Pipe created. Subscribing...");
                    client.topicSubscribe(pipe, "demo.topics.test");

                    fetchMessages(pipe, prefix, 30000);

                    client.close();
                });
            });

        });
        t.start();
        return t;
    }

    private static void fetchMessages(Pipe pipe, String prefix, int timeout) {
        pipe.read(1, timeout, (response) -> {
            System.out.println(prefix + ": " + response);

//            fetchMessages(pipe, prefix, timeout);
        });
    }
};
