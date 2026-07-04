package com.example.langgraph4jdemo;

import jakarta.annotation.PostConstruct;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Service
public class LoopService {

    private final CountdownNode countdownNode;
    private org.bsc.langgraph4j.CompiledGraph<LoopState> compiledGraph;

    public LoopService(CountdownNode countdownNode) {
        this.countdownNode = countdownNode;
    }

    @PostConstruct
    void init() {
        try {
            var stateGraph = new StateGraph<>(LoopState.SCHEMA, LoopState::new)
                    .addNode("counter", node_async(countdownNode))
                    .addEdge(START, "counter")
                    .addConditionalEdges(
                            "counter",
                            edge_async(state -> state.remaining() > 0 ? "continue" : "end"),
                            Map.of(
                                    "continue", "counter",
                                    "end", END
                            )
                    );

            this.compiledGraph = stateGraph.compile(
                    CompileConfig.builder()
                            .graphId("loop-demo")
                            .recursionLimit(20)
                            .build()
            );
        } catch (GraphStateException e) {
            throw new IllegalStateException("Failed to initialize loop graph", e);
        }
    }

    public void runDemo() {
        runFor(3);
        runFor(1);
    }

    private void runFor(int start) {
        System.out.println("=== Loop demo start: " + start + " ===");
        for (var item : compiledGraph.stream(Map.of(LoopState.REMAINING_KEY, start))) {
            System.out.println(item);
        }
        System.out.println("=== Loop demo end: " + start + " ===");
    }
}
