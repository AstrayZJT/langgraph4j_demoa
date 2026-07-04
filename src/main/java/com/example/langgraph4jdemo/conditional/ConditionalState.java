package com.example.langgraph4jdemo.conditional;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConditionalState extends AgentState {

    public static final String NUMBER_KEY = "number";
    public static final String MESSAGES_KEY = "messages";

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            NUMBER_KEY, Channels.base(() -> 0),
            MESSAGES_KEY, Channels.appender(ArrayList::new)
    );

    public ConditionalState(Map<String, Object> initData) {
        super(initData);
    }

    public int number() {
        return this.<Integer>value(NUMBER_KEY).orElse(0);
    }

    public List<String> messages() {
        return this.<List<String>>value(MESSAGES_KEY).orElse(List.of());
    }
}
