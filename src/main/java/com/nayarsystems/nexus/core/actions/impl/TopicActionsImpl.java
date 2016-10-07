package com.nayarsystems.nexus.core.actions.impl;

import com.google.common.collect.ImmutableMap;
import com.nayarsystems.nexus.NexusClient;
import com.nayarsystems.nexus.core.actions.TopicActions;
import com.nayarsystems.nexus.core.components.Pipe;

public class TopicActionsImpl implements TopicActions {

    private final NexusClient client;

    public TopicActionsImpl(NexusClient client) {
        this.client = client;
    }

    @Override
    public void topicSubscribe(Pipe pipe, String topic) {
        this.client.exec("topic.sub", ImmutableMap.of("pipeid", pipe.getId(), "topic", topic), null);
    }

    @Override
    public void topicUnsubscribe(Pipe pipe, String topic) {
        this.client.exec("topic.unsub", ImmutableMap.of("pipeid", pipe.getId(), "topic", topic), null);
    }

    @Override
    public void topicPublish(String topic, Object data) {
        this.client.exec("topic.pub", ImmutableMap.of("topic", topic, "msg", data), null);
    }
}
