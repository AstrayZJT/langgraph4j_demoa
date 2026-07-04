package com.example.langgraph4jdemo;

import com.example.langgraph4jdemo.checkpoint.CheckpointService;
import com.example.langgraph4jdemo.conditional.ConditionalBranchService;
import com.example.langgraph4jdemo.loop.LoopService;
import com.example.langgraph4jdemo.langchain.LangChainBridgeService;
import com.example.langgraph4jdemo.minimal.SimpleGraphService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LangGraph4jDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LangGraph4jDemoApplication.class, args);
    }

    @Bean
    ApplicationRunner graphRunner(SimpleGraphService simpleGraphService,
                                 ConditionalBranchService conditionalBranchService,
                                 LoopService loopService,
                                 CheckpointService checkpointService,
                                 LangChainBridgeService langChainBridgeService) {
        return args -> {
            simpleGraphService.runOnce();
            conditionalBranchService.runDemo();
            loopService.runDemo();
            checkpointService.runDemo();
            langChainBridgeService.runDemo();
        };
    }
}
