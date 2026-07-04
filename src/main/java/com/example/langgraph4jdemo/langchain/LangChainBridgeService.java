package com.example.langgraph4jdemo.langchain;

import jakarta.annotation.PostConstruct;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Service
public class LangChainBridgeService {

    private final DraftWithLangChainNode draftNode;
    private final PolishWithLangChainNode polishNode;
    private org.bsc.langgraph4j.CompiledGraph<LangChainState> compiledGraph;

    public LangChainBridgeService(DraftWithLangChainNode draftNode,
                                  PolishWithLangChainNode polishNode) {
        this.draftNode = draftNode;
        this.polishNode = polishNode;
    }

    @PostConstruct
    void init() {
        try {
            var stateGraph = new StateGraph<>(LangChainState.SCHEMA, LangChainState::new)
                    .addNode("draft", node_async(draftNode))
                    .addNode("polish", node_async(polishNode))
                    .addEdge(START, "draft")
                    .addEdge("draft", "polish")
                    .addEdge("polish", END);

            this.compiledGraph = stateGraph.compile();
        } catch (GraphStateException e) {
            throw new IllegalStateException("Failed to initialize LangChain4j bridge graph", e);
        }
    }

    public void runDemo() {
        System.out.println("=== LangChain4j bridge demo start ===");
        for (var item : compiledGraph.stream(Map.of(LangChainState.TOPIC_KEY, "LangGraph4j and LangChain4j"))) {
            System.out.println(item);
        }
        System.out.println("=== LangChain4j bridge demo end ===");
    }
}
