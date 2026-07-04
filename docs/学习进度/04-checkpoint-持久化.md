# checkpoint / 持久化

这一节要学的是：**图不只是能跑，还能把跑到哪一步记录下来，方便暂停、查看、恢复。**

这就是 LangGraph4j 走向“可恢复工作流”的关键一步。

---

## 1. 这一节对应哪些代码

- [CheckpointState.java](../../src/main/java/com/example/langgraph4jdemo/checkpoint/CheckpointState.java)
- [CheckpointService.java](../../src/main/java/com/example/langgraph4jdemo/checkpoint/CheckpointService.java)
- [DraftNode.java](../../src/main/java/com/example/langgraph4jdemo/checkpoint/DraftNode.java)
- [ReviewNode.java](../../src/main/java/com/example/langgraph4jdemo/checkpoint/ReviewNode.java)

---

## 2. 这一步想解决什么

如果图只是跑完就结束，那它还是“临时计算”。

但真实流程里，你常常会遇到这些情况：

- 跑到一半要人工确认
- 程序挂了，想接着上次继续
- 想查看当前状态和历史状态
- 想在某一步暂停下来，手工改一下再继续

这类问题就需要 checkpoint。

---

## 3. 这个示例的图长什么样

这次图很简单：

```text
START -> draft -> review -> END
```

但我们在 `review` 前面加了一个暂停点。

也就是说：

- 先生成草稿
- 草稿生成后先停住
- 查看当前 state 和历史
- 再恢复继续执行 review

---

## 4. State 里多了什么

[`CheckpointState`](../../src/main/java/com/example/langgraph4jdemo/checkpoint/CheckpointState.java) 里有三个字段：

```java
public static final String TOPIC_KEY = "topic";
public static final String DRAFT_KEY = "draft";
public static final String MESSAGES_KEY = "messages";
```

### `topic`

输入主题，普通值。

### `draft`

草稿内容，普通值。

### `messages`

执行日志，追加型列表。

---

## 5. 图是怎么建的

核心代码在 [`CheckpointService`](../../src/main/java/com/example/langgraph4jdemo/checkpoint/CheckpointService.java)：

```java
var stateGraph = new StateGraph<>(CheckpointState.SCHEMA, CheckpointState::new)
        .addNode("draft", node_async(draftNode))
        .addNode("review", node_async(reviewNode))
        .addEdge(START, "draft")
        .addEdge("draft", "review")
        .addEdge("review", END);
```

### 重点不在图本身，而在编译配置

```java
this.compiledGraph = stateGraph.compile(
        CompileConfig.builder()
                .graphId("checkpoint-demo")
                .checkpointSaver(new MemorySaver())
                .interruptBefore("review")
                .releaseThread(false)
                .build()
);
```

这里的几个点很重要：

- `checkpointSaver(new MemorySaver())`：开启 checkpoint
- `interruptBefore("review")`：在 `review` 前暂停
- `releaseThread(false)`：这次学习示例先保留线程记录，方便后面继续查看

---

## 6. `MemorySaver` 是什么

`MemorySaver` 就是一个内存版 checkpoint 保存器。

它会把每一步的 checkpoint 存在内存里。

所以它适合：

- 学习
- 本地验证
- 先把 checkpoint 机制跑通

但它不是持久化到数据库，程序一重启，内存就没了。

所以这一步先学“机制”，后面再换成 Postgres saver。

---

## 7. `RunnableConfig` 和 `threadId`

运行图时，我们给它加了：

```java
RunnableConfig.builder()
        .graphId("checkpoint-demo")
        .threadId("checkpoint-thread-1")
        .build();
```

### `threadId` 是干什么的

它相当于这次执行的会话 ID。

同一个 `threadId`，checkpoint saver 才知道要把状态存到哪一条线程上。

你可以把它理解成：

- 这个图是同一个
- 但这次运行是第几次会话，要靠 `threadId` 区分

---

## 8. 先跑到暂停点

第一次执行时，我们调用：

```java
compiledGraph.stream(
        Map.of(CheckpointState.TOPIC_KEY, "LangGraph4j checkpoint demo"),
        runnableConfig
)
```

这时图会：

1. 进入 `START`
2. 执行 `draft`
3. 在 `review` 前暂停

所以你会看到 `DraftNode` 跑完，但 `ReviewNode` 还没跑。

---

## 9. 怎么拿到当前快照

暂停后可以这样取状态：

```java
StateSnapshot<CheckpointState> snapshot = compiledGraph.getState(runnableConfig);
```

`snapshot` 里能看到：

- 当前 state
- 下一步该去哪
- 当前 checkpoint 的配置

你也可以看历史：

```java
compiledGraph.getStateHistory(runnableConfig)
```

它会把这条线程上已经保存过的 checkpoint 列出来。

---

## 10. 怎么恢复继续跑

恢复时我们用的是：

```java
compiledGraph.stream(GraphInput.resume(), snapshot.config())
```

意思是：

- 不重新喂一遍新的输入
- 直接从上次暂停的位置接着跑
- 继续执行 `review`

这就是 checkpoint 最核心的价值。

---

## 11. `Checkpoint` 和 `StateSnapshot` 是什么关系

你可以这样理解：

- `Checkpoint` 是保存下来的那一份记录
- `StateSnapshot` 是运行时看到的那一份状态快照

所以你学 checkpoint 时，最重要的不是死记类型名，而是理解：

**图在每一步都会把状态存起来，暂停后还能再读出来、继续跑。**

---

## 12. checkpoint、snapshot、resume、retry 的区别

这几个词很容易混，我单独拆一下：

### checkpoint

checkpoint 是“存档点”。

它记录的是图执行到某一步时的状态和位置。

### snapshot

snapshot 是“快照”。

它是你从 checkpoint 里读出来的那一刻的状态视图，所以你能看到：

- 当前 state
- 当前停在哪个节点
- 下一步该去哪

### resume

resume 是“从存档继续跑”。

你这版示例里恢复时用的是：

```java
compiledGraph.stream(GraphInput.resume(), snapshot.config())
```

它表示：

- 不重新开始
- 直接从暂停点继续执行

### retry

retry 是“失败后再试一次”。

这跟 resume 不是一回事。

- resume：流程没失败，只是暂停了
- retry：某一步失败了，再走一遍或者重试那一步

你现在这个示例验证的是 resume，不是 retry。

---

## 13. 怎么验证这一步成功

你在控制台里应该看到：

- `DraftNode executing...`
- `Paused snapshot next node: review`
- `Checkpoint history:`
- `ReviewNode executing...`
- `Final snapshot state:`

如果这些都出现了，就说明：

- checkpoint 已经存下来了
- snapshot 能读出来
- resume 能继续执行

---

## 14. 这一节学完后你应该明白什么

你应该真正理解这些事：

1. checkpoint 让图具备“可暂停、可恢复”的能力
2. `threadId` 是一条会话的标识
3. `getState()` 可以拿当前快照
4. `getStateHistory()` 可以看历史记录
5. `GraphInput.resume()` 可以从暂停点继续

---

## 15. 下一步学什么

下一节学 `和 LangChain4j 结合`。

到那一步，我们就不是只玩图了，而是把 LLM、工具调用、记忆这些能力接进图里，做真正的 agent workflow。
