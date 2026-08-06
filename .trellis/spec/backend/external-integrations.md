# 外部集成契约

> 基于 loktar 项目真实代码提炼。记录与外部系统集成的可执行契约。

---

## 场景：Portainer REST API 重启容器（PortainerUtil）

### 1. 范围 / 触发

- 触发：需要通过 Portainer 管理 Docker 容器（重启等），属于基础设施集成
- 实现：`src/main/java/com/loktar/util/PortainerUtil.java`
- 首个使用方：`src/main/java/com/loktar/task/minecraft/MinecraftTask.java`

### 2. 签名

```java
// 按容器名重启容器，返回是否真正重启成功（2xx）
// 失败场景只 log 不抛异常；网络/解析等受检异常由 @SneakyThrows 向上传播
public boolean restartContainerByName(String containerName)
```

### 3. 契约

**配置键**（`LokTarConfig`，yaml 中 `conf.` 前缀）：

| 键 | 说明 |
|----|------|
| `conf.portainer.baseUrl` | Portainer 地址，如 `http://192.168.x.x:9000`，不带尾部斜杠 |
| `conf.portainer.apiToken` | Portainer Access Token（`ptr_` 开头） |
| `conf.portainer.endpointId` | 目标 Docker endpoint 的数字 ID |

**HTTP 契约**：

| 步骤 | 请求 | 成功响应 |
|------|------|---------|
| 认证 | 所有请求带 header `X-API-Key: {apiToken}` | — |
| 查容器 | `GET {baseUrl}/api/endpoints/{endpointId}/docker/containers/json?all=true&filters={urlencode({"name":["/{containerName}"]})}` | 200 + JSON 数组 |
| 重启 | `POST {baseUrl}/api/endpoints/{endpointId}/docker/containers/{containerId}/restart`（空 body） | 204 |

**响应解析要点**：

- 容器列表数组元素字段为 Docker 风格大写开头：`Id`、`Names`
- `Names` 数组中的名字带**前导斜杠**（如 `/minecraft-bedrock-server`），匹配时必须拼 `"/" + containerName`
- `filters` 参数值必须 URLEncoder（UTF-8）编码，JSON 用 `ObjectMapper.createObjectNode()` 构建，不手写转义引号

### 4. 校验与错误矩阵

| 条件 | 行为 |
|------|------|
| 容器列表响应非 2xx（如 401 Token 失效） | `log.error` + 返回 `false` |
| 列表中精确匹配不到 `"/" + containerName` | `log.warn` + 返回 `false` |
| 重启响应 2xx | `log.info` + 返回 `true` |
| 重启响应非 2xx | `log.error` + 返回 `false` |
| 网络异常 / JSON 解析异常 | `@SneakyThrows` 传播，由调用方或 SchedulerConfig 全局兜底 |

### 5. Good / Base / Bad 用例

- Good：Token 有效、容器存在 → 返回 `true`，容器重启
- Base：容器名拼写错误 → 返回 `false`，日志提示未找到，不误报成功
- Bad：Token 失效（401）→ 返回 `false`；**不得**把 401 响应当 JSON 数组解析（会误判为"未找到容器"）

### 6. 测试要求

- 项目无测试目录（见 tests.md），以编译 + 首次运行日志核对代替：
  - 断言点 1：重启成功路径日志出现 `容器重启成功：{name}`
  - 断言点 2：调用方仅在返回 `true` 时执行成功后续动作（如发通知）

### 7. Wrong vs Correct

#### Wrong

```java
// 名字不带前导斜杠，永远匹配不到
if (containerName.equals(name.asText())) { ... }
// void 返回，调用方无法区分成功失败，失败也发"已重启"通知
public void restartContainerByName(String containerName) { ... }
```

#### Correct

```java
// Docker Names 带前导斜杠，必须拼 "/"
if (("/" + containerName).equals(name.asText())) { ... }
// 返回 boolean，调用方仅在 true 时走成功路径
public boolean restartContainerByName(String containerName) { ... }
```

---

## 场景：MineStat 查询 Minecraft 基岩版版本

### 1. 范围 / 触发

- 触发：需要查询 Minecraft **基岩版**（Bedrock/RakNet，UDP）服务器状态与版本号
- 首个使用方：`src/main/java/com/loktar/task/minecraft/MinecraftTask.java`

### 2. 签名与依赖

```xml
<dependency>
    <groupId>io.github.fragland</groupId>
    <artifactId>MineStat</artifactId>
    <version>3.0.6</version>
</dependency>
```

```java
import me.dilley.MineStat; // 包名是 me.dilley，不是旧文档的 land.Frag

// 构造器同步阻塞执行查询；timeout 单位为秒（内部 *1000）；第 5 参 isPortDefined 传 true
MineStat ms = new MineStat(host, port, 5, MineStat.Request.BEDROCK, true);
boolean online = ms.isServerUp();
String version = ms.getVersion(); // 服务器离线时为 null
```

### 3. 契约要点

- 显式指定 `MineStat.Request.BEDROCK`，避免默认多协议依次探测（TCP 协议全部白试一轮才到 UDP）
- 基岩版默认端口 19132（UDP）
- 超时建议 5 秒；该查询发生在 `@Scheduled` 线程中，阻塞由 `SchedulerConfig` 线程池（poolSize=8）消化
- **`getVersion()` 返回值不一定是纯版本号**：实测 itzg/minecraft-bedrock-server 返回 `"1.26.40 Synology-world (MCPE)"`（版本号 + 世界名 + 客户端标识），与官方 versions.json 的 `release.latest`（如 `"1.26.40"`）对比前必须先截取首个空格前的部分：`rawVersion.split(" ")[0]`

### 4. 校验与错误矩阵

| 条件 | 行为 |
|------|------|
| 服务器离线 / UDP 超时 | `isServerUp()` 返回 `false`，**不抛异常**，调用方自行 return |
| `getVersion()` 为 null | 调用方用 `ObjectUtils.isEmpty` 防护，跳过本次逻辑 |

### 5. Wrong vs Correct

#### Wrong

```java
// minecraft-server-util 是 Node.js 库，Java 生态不存在该坐标
// 按旧文档 import land.Frag.MineStat（2.x 旧包名，3.x 已改为 me.dilley）
// 不传 Request.BEDROCK，默认 Request.NONE 会先跑 4 种 TCP 协议探测，离线时要等 4 轮超时
```

#### Correct

```java
// Maven Central: io.github.fragland:MineStat:3.0.6，包名 me.dilley
MineStat ms = new MineStat(host, port, 5, MineStat.Request.BEDROCK, true);
if (!ms.isServerUp()) { log.warn("{}", "服务器离线"); return; }
```

---

## 相关规范

- [错误处理](./error-handling.md)：@SneakyThrows + SchedulerConfig 全局兜底
- [质量规范](./quality-guidelines.md)：HTTP 调用审查清单（HttpClient 单例 + timeout）
