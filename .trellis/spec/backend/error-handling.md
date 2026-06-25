# 错误处理规范

> 基于 loktar 项目真实代码提炼。

---

## 概述

项目未使用全局异常处理器（`@ControllerAdvice` / `@ExceptionHandler`），
错误处理依赖以下两种机制：

1. **`@SneakyThrows`（Lombok）**：将受检异常包装为运行时异常向上抛出
2. **`SchedulerConfig` 全局错误兜底**：定时任务的 `ErrorHandler` 捕获并记录异常

---

## @SneakyThrows 模式

项目广泛使用 Lombok 的 `@SneakyThrows` 注解来处理受检异常（如 `IOException`、`InterruptedException` 等），
避免在方法签名上声明 `throws` 或编写 try-catch。

### 使用位置

| 层 | 使用情况 |
|----|---------|
| Controller | `FundNavController.testSync()`、`QywxController.download()` |
| Service | `LandServiceImpl.getData()`、`GithubServiceImpl` 多处、`NewHouseHangzhouServiceV3Impl` 多处 |
| Task | `FundNavTask.syncToday()` |

### 代码示例

> `src/main/java/com/loktar/service/land/impl/LandServiceImpl.java`

```java
@SneakyThrows
private List<Land> getData(String year) {
    HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(MessageFormat.format(URK_LIST, year)))
            .timeout(Duration.ofSeconds(30))
            .GET()
            .build();
    // httpClient.send() 抛出 IOException，由 @SneakyThrows 包装
    HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
    LandResultDTO landResultDTO = objectMapper.readValue(response.body(), LandResultDTO.class);
    // ...
}
```

> `src/main/java/com/loktar/web/qywx/QyWeixinCallbackController.java`

```java
@SneakyThrows
@GetMapping("receive")
public ResponseEntity<String> msgValid(...) {
    WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(...);
    String sEchoStr = wxcpt.VerifyURL(...);
    // ...
}
```

---

## 定时任务全局错误兜底

> `src/main/java/com/loktar/conf/SchedulerConfig.java`

```java
@Bean(destroyMethod = "shutdown")
public ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(8);
    scheduler.setThreadNamePrefix("scheduled-");
    scheduler.setWaitForTasksToCompleteOnShutdown(true);
    scheduler.setAwaitTerminationSeconds(30);
    scheduler.setRemoveOnCancelPolicy(true);
    scheduler.setErrorHandler(t ->
        org.slf4j.LoggerFactory.getLogger("ScheduledError")
            .error("scheduled task error", t));
    scheduler.initialize();
    return scheduler;
}
```

### 关键设计

| 配置 | 值 | 说明 |
|------|-----|------|
| `poolSize` | 8 | 同时可并行运行的任务数 |
| `threadNamePrefix` | `"scheduled-"` | 线程名前缀，便于日志排查 |
| `waitForTasksToCompleteOnShutdown` | `true` | 关闭时等待任务完成 |
| `awaitTerminationSeconds` | 30 | 关闭时最多等待 30 秒 |
| `removeOnCancelPolicy` | `true` | 取消的任务从队列移除 |
| `errorHandler` | 全局兜底 | 记录异常到 `"ScheduledError"` Logger |

---

## Redis Key 过期监听

> `src/main/java/com/loktar/listener/RedisKeyExpirationListener.java`

项目通过 Redis Key 过期事件实现超时任务处理，作为异步错误兜底机制。

---

## 规则

| 规则 | 说明 |
|------|------|
| 受检异常使用 `@SneakyThrows` | 不在方法签名上声明 `throws` |
| 定时任务异常由 `SchedulerConfig` 全局兜底 | 不需在 Task 方法内 try-catch |
| 无自定义异常类 | 项目未定义 `RuntimeException` 子类 |
| 无全局异常处理器 | 无 `@ControllerAdvice` |

---

## 禁止项

- 禁止使用 `e.printStackTrace()`（应让异常传播或使用 `log.error`）
- 禁止在定时任务方法中吞掉异常（全局 `ErrorHandler` 会记录）
- 禁止引入 `@ControllerAdvice` 而不与团队确认（当前项目无此模式）
