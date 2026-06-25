# 日志记录规范

> 基于 loktar 项目真实代码提炼，`@Slf4j` + 参数化日志是唯一日志模式。

---

## 概述

项目使用 Lombok 的 `@Slf4j` 注解引入 SLF4J 日志门面，底层由 Spring Boot 默认的 Logback 实现。
所有日志均使用**参数化占位符** `"{}"` 格式，不使用字符串拼接。

---

## 日志库与配置

| 项目 | 值 |
|------|-----|
| 日志门面 | SLF4J（通过 Lombok `@Slf4j`） |
| 日志实现 | Logback（Spring Boot 默认） |
| 注解方式 | 类级 `@Slf4j`（Lombok） |
| 日志变量 | `log`（Lombok 自动生成） |

### 使用范围

- Controller 层：需要日志的控制器加 `@Slf4j`
- Service 层：Service 实现类加 `@Slf4j`
- Task 层：定时任务类加 `@Slf4j`
- Listener 层：Redis 监听器加 `@Slf4j`
- Util 层：部分工具类加 `@Slf4j`

---

## 日志级别

项目实际使用中仅出现 `info` 和 `error` 两个级别：

| 级别 | 使用场景 |
|------|---------|
| `log.info("{}", value)` | 任务开始/结束、数据同步进度、关键业务事件 |
| `log.error("msg", t)` | 定时任务全局错误兜底（`SchedulerConfig`） |

> 项目中未发现 `log.debug()` 或 `log.warn()` 的实际使用。

---

## 参数化日志模式

### 标准写法

```java
log.info("{}", value);
```

### 实际代码示例

> `src/main/java/com/loktar/web/land/LandController.java`

```java
@GetMapping("/update")
public void update() {
    String year = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_YEAR);
    log.info("{}", year);
    landService.updateLandData(year);
}
```

> `src/main/java/com/loktar/service/land/impl/LandServiceImpl.java`

```java
@Override
public void updateLandData(String year) {
    // ...
    log.info("{}", year + "年土拍数据开始删除，共" + num + "条");
    // ...
    log.info("{}", year + "年土拍数据更新完成，共" + lands.size() + "条");
}
```

> `src/main/java/com/loktar/task/land/LandTask.java`

```java
@Scheduled(cron = "0 5 0 * * ?")
private void updateLandData() {
    log.info("{}", "土拍定时器开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
    // ...
    landService.updateLandData(year);
    log.info("{}", "土拍定时器结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
}
```

### 定时任务全局错误日志

> `src/main/java/com/loktar/conf/SchedulerConfig.java`

```java
scheduler.setErrorHandler(t ->
    org.slf4j.LoggerFactory.getLogger("ScheduledError")
        .error("scheduled task error", t));
```

---

## 规则

| 规则 | 说明 |
|------|------|
| 使用 `@Slf4j` 注解 | 不手动创建 `LoggerFactory.getLogger()` |
| 使用参数化占位符 `"{}"` | 不使用字符串拼接传入 `log.info()` |
| 第一个参数为 `"{}"` | 业务变量通过第二个参数传入 |
| 日志消息用中文 | 与项目业务语义一致 |

---

## 禁止项

- 禁止使用 `System.out.println()`（已全量迁移至 SLF4J）
- 禁止使用字符串拼接传入日志方法（应使用 `"{}"` 占位符）
- 禁止在日志中输出密钥、Token、密码等敏感信息
- 禁止使用 `e.printStackTrace()`（应使用 `log.error("msg", e)`）
