# 在 IDEA 里如何运行和验证

这篇是给你日常打开项目时直接照着走的。

## 1. 需要什么

- IntelliJ IDEA
- JDK 21 或更高
- Maven

## 2. 打开项目

直接在 IDEA 里 `Open` 当前目录：

`C:\Users\86187\Desktop\老桌面\学习笔记\Java学习\大三暑假\agent_demo\langgraph4j_demo`

## 3. 检查 JDK

把 Project SDK 设成 JDK 21。

这个项目 `pom.xml` 里也是按 Java 21 编译的。

## 4. 运行入口

运行这个类：

- [LangGraph4jDemoApplication.java](../../src/main/java/com/example/langgraph4jdemo/LangGraph4jDemoApplication.java)

## 5. 正常运行时怎么看

如果正常，你应该看到：

1. Spring Boot 启动成功
2. `LangGraph4j minimal graph start`
3. `NodeOutput` 逐步打印
4. `LangGraph4j minimal graph end`
5. `Process finished with exit code 0`

## 6. 如果启动失败，先查什么

### 情况一：JDK 版本不对

如果 IDEA 还是用旧 JDK，就会编译或运行失败。

### 情况二：Maven 没重新导入

改完 `pom.xml` 后，要让 IDEA 重新加载 Maven。

### 情况三：控制台中文乱码

这个项目里已经尽量用 ASCII 输出关键信息了。

## 7. 怎么判断图真的跑通

只看三个点就够：

- 节点顺序对不对
- `messages` 有没有累积
- 退出码是不是 `0`
