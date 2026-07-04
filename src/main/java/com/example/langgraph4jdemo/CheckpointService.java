package com.example.langgraph4jdemo;

import jakarta.annotation.PostConstruct;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.GraphInput;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.checkpoint.MemorySaver;
import org.bsc.langgraph4j.state.StateSnapshot;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Service
public class CheckpointService {

    private final DraftNode draftNode;
    private final ReviewNode reviewNode;
    private org.bsc.langgraph4j.CompiledGraph<CheckpointState> compiledGraph;

    public CheckpointService(DraftNode draftNode, ReviewNode reviewNode) {
        this.draftNode = draftNode;
        this.reviewNode = reviewNode;
    }

    @PostConstruct
    void init() {
        try {
            var stateGraph = new StateGraph<>(CheckpointState.SCHEMA, CheckpointState::new)
                    .addNode("draft", node_async(draftNode))
                    .addNode("review", node_async(reviewNode))
                    .addEdge(START, "draft")
                    .addEdge("draft", "review")
                    .addEdge("review", END);

            this.compiledGraph = stateGraph.compile(
                    CompileConfig.builder()
                            .graphId("checkpoint-demo")
                            .checkpointSaver(new MemorySaver())
                            .interruptBefore("review")
                            .releaseThread(false)
                            .build()
            );
        } catch (GraphStateException e) {
            throw new IllegalStateException("Failed to initialize checkpoint graph", e);
        }
    }

    public void runDemo() {
        System.out.println("=== Checkpoint demo start ===");

        RunnableConfig runnableConfig = RunnableConfig.builder()
                .graphId("checkpoint-demo")
                .threadId("checkpoint-thread-1")
                .build();

        for (var item : compiledGraph.stream(
                Map.of(CheckpointState.TOPIC_KEY, "LangGraph4j checkpoint demo"),
                runnableConfig)) {
            System.out.println(item);
        }

        StateSnapshot<CheckpointState> snapshot = compiledGraph.getState(runnableConfig);
        System.out.println("Paused snapshot next node: " + snapshot.next());
        System.out.println("Paused snapshot state: " + snapshot.state());

        System.out.println("Checkpoint history:");
        for (var historyItem : compiledGraph.getStateHistory(runnableConfig)) {
            System.out.println(historyItem);
        }

        System.out.println("=== Resume checkpoint demo ===");
        for (var item : compiledGraph.stream(GraphInput.resume(), snapshot.config())) {
            System.out.println(item);
        }

        StateSnapshot<CheckpointState> finalSnapshot = compiledGraph.getState(runnableConfig);
        System.out.println("Final snapshot next node: " + finalSnapshot.next());
        System.out.println("Final snapshot state: " + finalSnapshot.state());
        System.out.println("=== Checkpoint demo end ===");
    }
}
