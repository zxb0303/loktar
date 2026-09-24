# 测试规范

> 基于 loktar 项目当前测试现状编写。

---

## 当前状态

RedisTemplate 迁移已新增 7 个隔离单元测试类，当前参数展开后共 43 项，位于 `src/test/java/com/loktar/`：

| 测试文件（相对上述目录） | 断言范围 |
|---|---|
| `web/redis/RedisControllerTest.java` | 对象写入、集合 TTL、操作顺序与异常 |
| `util/wx/qywx/QywxApiTest.java` | 缓存命中、动态 TTL、写入异常 |
| `web/jellyfin/JellyfinWebhookControllerTest.java` | 两类集合成员、TTL 边界、移除、计数与副作用 |
| `task/patent/PatentTaskTest.java` | 永久计数及原锁分支 |
| `web/qywx/QyWeixinCallbackControllerTest.java` | 普通回调去重 |
| `web/qywx/QyWeixinCallbackPatentControllerTest.java` | 专利回调去重 |
| `web/qywx/QyWeixinCallbackChatGPTControllerTest.java` | ChatGPT 回调去重且不触碰字符串缓存 |

---

## 隔离测试模式

- 使用 `@ExtendWith(MockitoExtension.class)`、`@Mock` 和直接构造器调用，不启动 Spring 或读取环境配置。
- 分别 mock `RedisTemplate`、`ValueOperations`、`SetOperations` 及业务依赖；测试中不得连接真实 Redis、数据库、HTTP 或模型服务。
- 私有流程可通过 `ReflectionTestUtils` 验证，不为测试扩大生产接口。参见 `JellyfinWebhookControllerTest`。
- HTTP 场景 mock `HttpClient.send`；构造器会改变静态映射时，测试前保存、测试后恢复，避免测试间污染。参见 `QywxApiTest`。
- 回调去重测试仅走重复消息和异常分支，不启动成功分支的虚拟线程；它们不替代解密或真实异步集成测试。

---

## 新增测试时的约定

新增隔离测试沿用已验证的模式：

| 项目 | 约定 |
|------|------|
| 测试框架 | JUnit Jupiter（`org.junit.jupiter`） |
| Spring 上下文 | 当前隔离测试不使用 `@SpringBootTest` |
| Mock 框架 | Mockito（`MockitoExtension` / `@Mock`） |
| 测试目录 | `src/test/java/com/loktar/` |
| 测试命名 | `{ClassName}Test.java`（如 `LandServiceTest.java`） |

## Redis 缓存回归契约

### 1. 范围与触发条件
修改对象缓存、集合 TTL、消息去重或专利任务锁时，维护上述测试。生产代码直接注入 `RedisTemplate<String, Object>`，不恢复 RedisUtil 或吞异常包装。

### 2. 调用签名与验证命令
- 对象操作：`opsForValue().get(key)`、`set(key, value)`、`set(key, value, ttl, TimeUnit.SECONDS)`。
- 原子去重/锁：`opsForValue().setIfAbsent(key, value, ttl, TimeUnit.SECONDS)`。
- 集合操作：`opsForSet().add/remove/size`；过期使用 `getExpire/expire` 且单位为 `TimeUnit.SECONDS`。
- 删除：`delete(key)`，保留已有删除顺序。

已验证的 Windows 命令（Java 21 / Maven 3.9.12）：

```powershell
$env:JAVA_HOME = 'E:\JDK\semeru-21.0.12'
& 'E:\Maven\apache-maven-3.9.12\bin\mvn.cmd' -B -f 'E:\Project\loktar\pom.xml' '-Dtest=RedisControllerTest,QywxApiTest,JellyfinWebhookControllerTest,PatentTaskTest,QyWeixinCallback*ControllerTest' compiler:compile compiler:testCompile surefire:test
```

直接调用编译器及 Surefire 目标，避开资源过滤；显式测试选择器避免运行历史构建残留测试。不运行应用或真实手动入口。

### 3. 数据契约
- 保留 Key、对象类型及 `RedisConfig` 的 JSON 序列化；已有 `StringRedisTemplate` 数据流独立保留。
- `QywxApi` 的 `expiresIn > 0` 使用秒 TTL，否则永久保存。
- Jellyfin 两组集合的 TTL 都取 `Math.max(剩余播放秒数, 当前TTL)`；先添加成员，仅结果大于零时设置过期。远程集合存用户名，全部播放集合存设备 ID。
- 三个回调保持 30 秒原子去重；专利锁保持 10 秒及原 `if (lock) return` 判断。本契约记录兼容性，不表示该锁策略适用于新实现。

### 4. 边界与错误矩阵
| 条件 | 应观察到的行为 |
|---|---|
| 令牌命中缓存 | 返回原对象，不调用 HTTP 或重写缓存 |
| `expiresIn` 为 120 / 0 / -1 | 分别使用 120 秒 / 永久 / 永久写入 |
| Jellyfin 旧 TTL 为 -1 或 -2 | 原值参与取最大值，不替换为零 |
| Jellyfin 最终 TTL <= 0 | 仍添加成员，不调用 `expire` |
| 集合添加抛出异常 | 原异常传播，不执行 `expire` 或依赖成功的后续操作 |
| 写入、过期、移除、计数抛出异常 | 进入已有上层异常处理，不返回伪造的 false/0 |
| 回调去重返回 false | HTTP 204，不启动异步处理 |
| 回调去重抛出异常 | 原异常传播，不启动异步处理 |

### 5. 正常、基础与错误用例
- 正常：远程播放剩余 120 秒、旧 TTL 300 秒，添加用户名后保留 300 秒。
- 基础：永久状态写入只调用 `set(key, value)`，不额外调用 `expire`。
- 错误：集合操作抛出异常时，不能将集合视为空并继续触发限速或 Homepage 切换。

### 6. 必须维护的断言
- 验证精确 Key、值、TTL 和秒单位，使用 `InOrder` 检查关键操作顺序。
- 使用 `assertSame(failure, assertThrows(...))` 验证原异常，使用 `never()` / `verifyNoInteractions` 验证失败后的操作没有执行。
- 保留零/负 TTL 参数化用例、永久计数写入、Jellyfin 两类成员与集合大小、专利锁 true/false 分支。
- 三个回调分别覆盖重复消息和 Redis 异常；ChatGPT 去重阶段验证不操作独立 `StringRedisTemplate`。

### 7. 错误与正确示例
将负数 TTL 直接交给带过期重载，不能表达原永久缓存语义：

```java
// 错误：将永久缓存标记直接作为过期时间。
redisTemplate.opsForValue().set(key, value, -1, TimeUnit.SECONDS);

// 正确：永久缓存使用无 TTL 重载。
redisTemplate.opsForValue().set(key, value);
```

Mock 测试验证调用契约，不证明真实 Redis 序列化往返或外部服务联通性；需要集成验证时必须单独确定安全环境与执行授权。

---

## 禁止项

- 禁止在 `src/main/` 中编写测试代码（当前 `web/test/` 下的类是 API 端点，非单元测试）
- 禁止跳过 `src/test/` 直接在 Controller 中验证逻辑（仅作为临时手段）
