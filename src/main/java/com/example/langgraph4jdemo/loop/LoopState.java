package com.example.langgraph4jdemo.loop;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LoopState extends AgentState {

    public static final String REMAINING_KEY = "remaining";
    public static final String MESSAGES_KEY = "messages";

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            REMAINING_KEY, Channels.base(() -> 0),
            MESSAGES_KEY, Channels.appender(ArrayList::new)
    );

    public LoopState(Map<String, Object> initData) {
        super(initData);
    }

    public int remaining() {
        return this.<Integer>value(REMAINING_KEY).orElse(0);
    }

    public List<String> messages() {
        return this.<List<String>>value(MESSAGES_KEY).orElse(List.of());
    }
}
