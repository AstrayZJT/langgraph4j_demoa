package com.example.langgraph4jdemo;

import jakarta.annotation.PostConstruct;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Service
public class SimpleGraphService {

    private final GreeterNode greeterNode;
    private final ResponderNode responderNode;
    private org.bsc.langgraph4j.CompiledGraph<SimpleState> compiledGraph;

    public SimpleGraphService(GreeterNode greeterNode, ResponderNode responderNode) {
        this.greeterNode = greeterNode;
        this.responderNode = responderNode;
    }

    @PostConstruct
    void init() {
        try {
            var stateGraph = new StateGraph<>(SimpleState.SCHEMA, SimpleState::new)
                    .addNode("greeter", node_async(greeterNode))
                    .addNode("responder", node_async(responderNode))
                    .addEdge(START, "greeter")
                    .addEdge("greeter", "responder")
                    .addEdge("responder", END);

            this.compiledGraph = stateGraph.compile();
        } catch (GraphStateException e) {
            throw new IllegalStateException("Failed to initialize LangGraph4j graph", e);
        }
    }

    public void runOnce() {
        System.out.println("=== LangGraph4j minimal graph start ===");
        for (var item : compiledGraph.stream(Map.of(SimpleState.MESSAGES_KEY, "Let's begin!"))) {
            System.out.println(item);
        }
        System.out.println("=== LangGraph4j minimal graph end ===");
    }
}
