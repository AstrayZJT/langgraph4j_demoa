package com.example.langgraph4jdemo.checkpoint;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CheckpointState extends AgentState {

    public static final String TOPIC_KEY = "topic";
    public static final String DRAFT_KEY = "draft";
    public static final String MESSAGES_KEY = "messages";

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            TOPIC_KEY, Channels.base(() -> ""),
            DRAFT_KEY, Channels.base(() -> ""),
            MESSAGES_KEY, Channels.appender(ArrayList::new)
    );

    public CheckpointState(Map<String, Object> initData) {
        super(initData);
    }

    public String topic() {
        return this.<String>value(TOPIC_KEY).orElse("");
    }

    public String draft() {
        return this.<String>value(DRAFT_KEY).orElse("");
    }

    public List<String> messages() {
        return this.<List<String>>value(MESSAGES_KEY).orElse(List.of());
    }
}
