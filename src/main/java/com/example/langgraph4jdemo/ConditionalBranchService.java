package com.example.langgraph4jdemo;

import jakarta.annotation.PostConstruct;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Service
public class ConditionalBranchService {

    private final ParityRouterNode parityRouterNode;
    private final EvenBranchNode evenBranchNode;
    private final OddBranchNode oddBranchNode;
    private org.bsc.langgraph4j.CompiledGraph<ConditionalState> compiledGraph;

    public ConditionalBranchService(ParityRouterNode parityRouterNode,
                                   EvenBranchNode evenBranchNode,
                                   OddBranchNode oddBranchNode) {
        this.parityRouterNode = parityRouterNode;
        this.evenBranchNode = evenBranchNode;
        this.oddBranchNode = oddBranchNode;
    }

    @PostConstruct
    void init() {
        try {
            var stateGraph = new StateGraph<>(ConditionalState.SCHEMA, ConditionalState::new)
                    .addNode("router", node_async(parityRouterNode))
                    .addNode("even", node_async(evenBranchNode))
                    .addNode("odd", node_async(oddBranchNode))
                    .addEdge(START, "router")
                    .addConditionalEdges(
                            "router",
                            edge_async(state -> state.number() % 2 == 0 ? "even" : "odd"),
                            Map.of(
                                    "even", "even",
                                    "odd", "odd"
                            )
                    )
                    .addEdge("even", END)
                    .addEdge("odd", END);

            this.compiledGraph = stateGraph.compile();
        } catch (GraphStateException e) {
            throw new IllegalStateException("Failed to initialize conditional branch graph", e);
        }
    }

    public void runDemo() {
        runFor(4);
        runFor(7);
    }

    private void runFor(int number) {
        System.out.println("=== Conditional branch demo start: " + number + " ===");
        for (var item : compiledGraph.stream(Map.of(ConditionalState.NUMBER_KEY, number))) {
            System.out.println(item);
        }
        System.out.println("=== Conditional branch demo end: " + number + " ===");
    }
}
