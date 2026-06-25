# 质量规范

> 基于 loktar 项目真实代码模式提炼的代码审查清单。

---

## 概述

项目无 lint 工具配置（无 Checkstyle / SpotBugs / PMD），
代码质量依赖以下约定和人工审查。

---

## 必须遵循的模式

| 模式 | 说明 | 参考规范 |
|------|------|---------|
| 构造器注入 | 所有 Bean 通过构造器注入 | [api-routes.md](./api-routes.md) |
| `@Slf4j` + `log.info("{}", v)` | 参数化日志 | [logging-guidelines.md](./logging-guidelines.md) |
| `@SneakyThrows` 处理受检异常 | 不在方法签名声明 `throws` | [error-handling.md](./error-handling.md) |
| `@RestController` + `@RequestMapping` 无斜杠 | 类级路由不带前导斜杠 | [api-routes.md](./api-routes.md) |
| `@Profile(ENV_PRO)` 限定定时任务 | 定时任务仅在 PRO 环境运行 | — |
| JDK `HttpClient` 单例注入 | 不使用 `RestTemplate` 或 `new HttpClient()` | — |
| `DateTimeUtil` 统一日期格式化 | 不直接使用 `SimpleDateFormat` | — |

---

## 禁止项

| 禁止 | 原因 |
|------|------|
| `System.out.println()` | 已全量迁移至 SLF4J |
| `@Autowired` 字段注入 | 统一使用构造器注入 |
| `e.printStackTrace()` | 应使用 `log.error("msg", e)` 或 `@SneakyThrows` |
| 字符串拼接传入日志 | 应使用 `"{}"` 参数化占位符 |
| `new HttpClient()` | 应注入 Spring 管理的单例 |
| `SimpleDateFormat` | 应使用 `DateTimeUtil` 中的 `DateTimeFormatter` |
| 手写 Domain 实体 | 应通过 MBG 生成 |
| 在 `learn/` 中编写生产代码 | 该目录仅用于 JDK 特性学习 |

---

## 定时任务审查清单

- [ ] 类标注 `@Component` + `@Profile(LokTarConstant.ENV_PRO)` + `@Slf4j`
- [ ] 方法标注 `@Scheduled(cron = "...")`
- [ ] 数据库写入操作有幂等性检查（先查后插/更新）
- [ ] 日志记录任务开始和结束时间
- [ ] 依赖通过构造器注入

### 代码示例

> `src/main/java/com/loktar/task/land/LandTask.java`

```java
@Component
@Profile(LokTarConstant.ENV_PRO)
@Slf4j
public class LandTask {
    private final LandService landService;

    public LandTask(LandService landService) {
        this.landService = landService;
    }

    @Scheduled(cron = "0 5 0 * * ?")
    private void updateLandData() {
        log.info("{}", "土拍定时器开始：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
        String year = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_YEAR);
        landService.updateLandData(year);
        log.info("{}", "土拍定时器结束：" + DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_DATESECOND));
    }
}
```

---

## Service 层审查清单

- [ ] 接口 + `impl/` 实现类模式
- [ ] 实现类标注 `@Service` + `@Slf4j`
- [ ] 依赖通过构造器注入
- [ ] 受检异常使用 `@SneakyThrows`
- [ ] HTTP 调用使用注入的 `HttpClient` 单例并设置 `timeout`

---

## HTTP 调用审查清单

- [ ] 使用注入的 `HttpClient` 单例（Spring 管理）
- [ ] `HttpRequest` 设置 `.timeout(Duration.ofSeconds(30))`
- [ ] 不使用 `RestTemplate`（已迁移至 JDK HttpClient）

### 代码示例

> `src/main/java/com/loktar/service/land/impl/LandServiceImpl.java`

```java
HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(URI.create(MessageFormat.format(URK_LIST, year)))
        .timeout(Duration.ofSeconds(30))
        .header(LokTarConstant.HTTP_HEADER_USER_AGENT_NAME, LokTarConstant.HTTP_HEADER_USER_AGENT_VALUE)
        .header(LokTarConstant.HTTP_HEADER_ACCEPT_NAME, LokTarConstant.HTTP_HEADER_ACCEPT_VALUE_JSON)
        .GET()
        .build();
HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
```

---

## 新增 API 端点审查清单

- [ ] Controller 类标注 `@RestController` + `@RequestMapping("module")`（无前导斜杠）
- [ ] 方法路径带前导斜杠（如 `@GetMapping("/action")`）
- [ ] 依赖通过构造器注入
- [ ] 公开端点已加入 `SecurityConfig.PUBLIC_ENDPOINTS`
- [ ] 需要日志时标注 `@Slf4j`
