package com.example.langgraph4jdemo;

import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OddBranchNode implements NodeAction<ConditionalState> {

    @Override
    public Map<String, Object> apply(ConditionalState state) {
        System.out.println("OddBranchNode executing. Current messages: " + state.messages());
        return Map.of(ConditionalState.MESSAGES_KEY, state.number() + " is odd");
    }
}
