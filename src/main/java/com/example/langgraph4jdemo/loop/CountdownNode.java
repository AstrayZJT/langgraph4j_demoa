package com.example.langgraph4jdemo.loop;

import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CountdownNode implements NodeAction<LoopState> {

    @Override
    public Map<String, Object> apply(LoopState state) {
        System.out.println("CountdownNode executing. Remaining before step: " + state.remaining());
        return Map.of(
                LoopState.REMAINING_KEY, state.remaining() - 1,
                LoopState.MESSAGES_KEY, "tick: " + state.remaining()
        );
    }
}
