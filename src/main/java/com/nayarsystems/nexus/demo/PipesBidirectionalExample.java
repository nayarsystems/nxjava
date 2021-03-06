package com.nayarsystems.nexus.demo;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.core.components.Pipe;
import com.nayarsystems.nexus.core.components.Task;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;

/**
 * Created by Carles Aragon on 3/11/16.
 */
public class PipesBidirectionalExample {

    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("ws://localhost");

        NexusClient client = new NexusClient(uri);

        client.login("root", "root", (x, errorLogin) -> {
            if (errorLogin != null) {
                System.err.println("Error in login: " + errorLogin.toString());
                client.close();
            } else {

                client.pullTask("exchange.pipe", null, (Task task, JSONRPC2Error error) -> {
                    if (error != null) {
                        System.err.println("Error in pull: " + error.toString());
                        client.close();
                    } else {

                        String pipeAId = (String) task.getParameters().get("pipe");
                        Pipe pipeA = client.pipeOpen(pipeAId);

                        System.out.println("B1 - Task requested - PipeA with ID: " + pipeAId);

                        client.pipeCreate(10, (pipeB) -> {

                            System.out.println("B2 - PipeB created with ID: " + pipeB.getId());

                            pipeA.write(pipeB.getId(), (res, errWrite) -> {
                                System.out.println("B3 - PipeB sent to A " + res);

                                pipeB.readUntil((response, errorRead) -> {
                                    System.out.println("B(i) - Reading from PipeA" + response);

                                    if (getMSG(response).equals("END")) {
                                        System.out.println("B(end) - Close");
                                        pipeB.close((rc, errClose) -> {
                                            task.sendResult(ImmutableMap.of("res", "ok"), (r, e) -> client.close());
                                        });
                                        return true;
                                    } else {
                                        return false;
                                    }
                                });

                            });
                        });
                    }
                });

                client.pipeCreate(10, (pipeA) -> {
                    System.out.println("A1 - PipeA created with ID: " + pipeA.getId());

                    client.pushTask("exchange.pipe.test", ImmutableMap.of("pipe", pipeA.getId()), null, null, null, null, (response, error) -> {
                        if (error != null) {
                            System.err.println("Error in push: " + error.toString());
                            client.close();
                        } else {

                            System.out.println("A(end) - " + response);

                            pipeA.close((r, e) -> client.close());
                        }
                    });

                    pipeA.read(1, 0, (data, errRead) -> {
                        JSONArray msgs = (JSONArray) data.get("msgs");
                        JSONObject msg = (JSONObject) msgs.get(0);
                        String pipeB = msg.get("msg").toString();

                        Pipe pipeSend = client.pipeOpen(pipeB);
                        System.out.println("A2 - PipeB received with ID: " + pipeB);

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        pipeSend.write("Buffer 1", (res, err) -> {
                            System.out.println("A3 - Sending message to B: " + res);
                        });

                        pipeSend.write("END", (res, err) -> {
                            System.out.println("A4 - Sending final message to B" + res);
                        });
                    });


                });
            }
        });

    }

    private static String getMSG(JSONObject data){
        JSONArray msgs = (JSONArray) data.get("msgs");
        if (msgs.size() != 0) {
            JSONObject msg = (JSONObject) msgs.get(0);
            return msg.get("msg").toString();
        } else {
            return "TIMEOUT";
        }
    }
}
