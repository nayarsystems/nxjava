package com.nayarsystems.nexus.core.actions;

import com.nayarsystems.nexus.core.components.Pipe;

public interface TopicActions {
    void topicSubscribe(Pipe pipe, String topic);
    void topicUnsubscribe(Pipe pipe, String topic);
    void topicPublish(String topic, Object data);
}
