package com.example.langgraph4jdemo.langchain;

import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PolishWithLangChainNode implements NodeAction<LangChainState> {

    private final WritingAssistant writingAssistant;

    public PolishWithLangChainNode(WritingAssistant writingAssistant) {
        this.writingAssistant = writingAssistant;
    }

    @Override
    public Map<String, Object> apply(LangChainState state) {
        System.out.println("PolishWithLangChainNode executing. Draft: " + state.draft());
        String finalAnswer = writingAssistant.polish(state.draft());
        System.out.println("LangChain4j final answer: " + finalAnswer);
        return Map.of(
                LangChainState.FINAL_KEY, finalAnswer,
                LangChainState.MESSAGES_KEY, "draft polished by LangChain4j"
        );
    }
}
