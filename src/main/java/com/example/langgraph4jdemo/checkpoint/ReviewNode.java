package com.example.langgraph4jdemo.checkpoint;

import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReviewNode implements NodeAction<CheckpointState> {

    @Override
    public Map<String, Object> apply(CheckpointState state) {
        System.out.println("ReviewNode executing. Current draft: " + state.draft());
        return Map.of(
                CheckpointState.MESSAGES_KEY, "reviewed: " + state.draft()
        );
    }
}
