# API 路由规范

> 基于 loktar 项目真实 Controller 代码提炼，记录现实而非理想。

---

## 概述

项目所有 HTTP 端点均使用 Spring MVC 的 `@RestController` + `@RequestMapping` 模式。
控制器按业务模块分包，位于 `com.loktar.web.{module}` 下。

---

## 路由定义模式

### 类级注解

- `@RestController` + `@RequestMapping("moduleName")`
- `@RequestMapping` 路径**不带前导斜杠**（如 `"land"` 而非 `"/land"`）
- 需要日志的控制器加 `@Slf4j`（Lombok）

### 依赖注入

- 统一使用**构造器注入**，不使用 `@Autowired`
- 注入 Service、Mapper、`QywxApi`、`LokTarConfig`、`HttpClient` 等

### 方法级注解

- `@GetMapping("/action")` 或 `@PostMapping("/action")`，方法路径带前导斜杠
- `@RequestBody` 用于接收 JSON 请求体（webhook 场景）
- `@RequestParam` 用于接收查询参数
- `@SneakyThrows` 标注在抛出受检异常的方法上

### 返回值

- 大多数端点返回 `void`（触发式任务，不返回数据给调用方）
- 回调验证端点返回 `ResponseEntity<Void>` 或 `ResponseEntity<String>`

---

## 代码示例

### 典型 GET 端点

> `src/main/java/com/loktar/web/land/LandController.java`

```java
@RestController
@RequestMapping("land")
@Slf4j
public class LandController {
    private final LandService landService;

    public LandController(LandService landService) {
        this.landService = landService;
    }

    @GetMapping("/update")
    public void update() {
        String year = DateTimeUtil.getDatetimeStr(LocalDateTime.now(), DateTimeUtil.FORMATTER_YEAR);
        log.info("{}", year);
        landService.updateLandData(year);
    }
}
```

### POST 端点（query 参数）

> `src/main/java/com/loktar/web/github/GithubController.java`

```java
@RestController
@RequestMapping("github")
public class GithubController {
    @PostMapping("/notifyMsg")
    public void notifyMsg(String version) {
        QywxApi.sendTextMsg(new AgentMsgText(
            lokTarConfig.getQywx().getNoticeZxb(),
            lokTarConfig.getQywx().getAgent002Id(),
            version + "已经推送到镜像仓库"));
    }
}
```

### Webhook 回调端点（@RequestBody）

> `src/main/java/com/loktar/web/jellyfin/JellyfinWebhookController.java`

```java
@RestController
@RequestMapping("jellyfin")
public class JellyfinWebhookController {
    @PostMapping("/webhook")
    public void webhook(@RequestBody Notification notification) {
        // 处理 webhook 逻辑
    }
}
```

### 企业微信回调端点（@RequestParam + @RequestBody）

> `src/main/java/com/loktar/web/qywx/QyWeixinCallbackController.java`

```java
@RestController
@RequestMapping("qywx/callback")
@Slf4j
public class QyWeixinCallbackController {
    @PostMapping("receive")
    public ResponseEntity<Void> receive(
            @RequestParam("msg_signature") String msgSignature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestBody String xml) {
        // ...
        return ResponseEntity.noContent().build();
    }

    @GetMapping("receive")
    public ResponseEntity<String> msgValid(
            @RequestParam("msg_signature") String msgSignature,
            @RequestParam("timestamp") String timestamp,
            @RequestParam("nonce") String nonce,
            @RequestParam("echostr") String echostr) {
        // ...
        return ResponseEntity.ok(sEchoStr);
    }
}
```

---

## 规则

| 规则 | 说明 |
|------|------|
| `@RequestMapping` 类级路径不带前导斜杠 | `"land"` 而非 `"/land"` |
| `@GetMapping`/`@PostMapping` 方法路径带前导斜杠 | `"/update"` |
| 构造器注入 | 不使用 `@Autowired` 字段注入 |
| 回调/webhook 端点需加入 `PUBLIC_ENDPOINTS` | 见 [auth-checks.md](./auth-checks.md) |

---

## 禁止项

- 禁止在 Controller 中直接操作数据库（应通过 Service/Mapper）
- 禁止使用 `@Autowired` 字段注入
- 禁止在 `@RequestMapping` 类级路径使用前导斜杠
