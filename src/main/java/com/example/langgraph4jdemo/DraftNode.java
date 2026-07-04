package com.example.langgraph4jdemo;

import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DraftNode implements NodeAction<CheckpointState> {

    @Override
    public Map<String, Object> apply(CheckpointState state) {
        System.out.println("DraftNode executing. Topic: " + state.topic());
        String draft = "Draft for topic: " + state.topic();
        return Map.of(
                CheckpointState.DRAFT_KEY, draft,
                CheckpointState.MESSAGES_KEY, "draft created"
        );
    }
}
