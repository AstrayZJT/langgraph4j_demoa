# LangGraph4j Demo

这是一个从零开始的 Spring Boot 学习项目，主题是 LangGraph4j。

当前阶段：`v0.2.0`

## 目前做到哪里

- `v0.1.0` 项目初始化
- `v0.2.0` LangGraph4j 最小图：`state` / `node` / `edge`

## 学习路线

请看 [docs/学习路线.md](./docs/学习路线.md)

## 版本记录

请看 [docs/版本记录](./docs/版本记录)

## 运行

```bash
mvn spring-boot:run
```

启动后会在控制台打印一次最小图的执行结果。

说明：我在本机看到 `java` 命令指向的是 Java 8，但 Maven 使用的是 Java 22。当前项目按 Java 21 编译，建议直接用 Maven 启动。
