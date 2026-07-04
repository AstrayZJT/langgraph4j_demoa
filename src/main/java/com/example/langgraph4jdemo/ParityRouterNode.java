package com.example.langgraph4jdemo;

import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ParityRouterNode implements NodeAction<ConditionalState> {

    @Override
    public Map<String, Object> apply(ConditionalState state) {
        System.out.println("ParityRouterNode executing. Current number: " + state.number());
        return Map.of(ConditionalState.MESSAGES_KEY, "Router saw number " + state.number());
    }
}
