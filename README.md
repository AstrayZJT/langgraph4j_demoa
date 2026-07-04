# LangGraph4j Demo

这是一个从零开始的 Spring Boot 学习项目，主题是 LangGraph4j。

## 当前阶段

- `v0.2.0` LangGraph4j 最小图：`state` / `node` / `edge`

## 现在做到哪里

- `v0.1.0` 项目初始化
- `v0.2.0` 跑通最小图

## 学习进度

- [LangGraph4j 学习进度清单](./docs/学习进度/README.md)

## 教材文档

- [LangGraph4j 教材目录](./docs/LangGraph4j教材/README.md)

## 快速教程

- [第一个可运行示例：最小图教程](./docs/学习进度/01-第一个可运行示例-最小图教程.md)
- [在 IDEA 里如何运行和验证](./docs/LangGraph4j教材/10-在IDEA里如何运行和验证.md)

## 运行

```bash
mvn spring-boot:run
```

启动后会在控制台打印一次最小图的执行结果。

说明：我在本机看到 `java` 命令指向的是 Java 8，但 Maven 使用的是 Java 22。当前项目按 Java 21 编译，建议直接用 Maven 启动。
