# RedisTemplate 直接调用设计

## 变更边界
原调用方 → RedisUtil → RedisConfig 提供的 RedisTemplate，改为原调用方 → 同一个 RedisTemplate。正常路径不改业务决策，失败路径仅移除工具层吞异常。

以下路径相对 src/main/java/com/loktar/，每个文件均需迁移构造器、字段、import 和操作调用：
- util/TransmissionUtil.java：会话读取与 28 * 60 秒写入。
- util/wx/qywx/QywxApi.java：AccessToken 对象读取及动态 expiresIn 写入。
- service/newhouse/impl/NewHouseHangzhouServiceV3Impl.java：Cookie 读取与 60 * 30 秒写入。
- task/audiobookshelf/AudioBookShelfTask.java：进度及通知档位，保留两天 TTL。
- task/patent/PatentTask.java：永久计数、开关删除、10 秒 setIfAbsent 锁。
- task/relx/RelxTask.java：开关及永久库存快照。
- web/qywx/QyWeixinCallbackController.java：30 秒消息去重、永久开关写入及删除。
- web/qywx/QyWeixinCallbackPatentController.java：30 秒消息去重。
- web/qywx/QyWeixinCallbackChatGPTController.java：30 秒消息去重；保留独立 StringRedisTemplate。
- web/jellyfin/JellyfinWebhookController.java：远程用户与设备播放集合、TTL、计数及移除。
- web/redis/RedisController.java：现有对象缓存和集合手动入口。
- web/test/TestController.java：库存快照读写。

## Redis 操作映射
- 注入类型为 RedisTemplate<String, Object>，字段与参数均命名 redisTemplate。
- get → opsForValue().get，保留调用点类型转换。现有 Key 来自常量、拼接或必填请求参数，不复制通用 null-Key 防护。
- set(key, value) 及固定负 TTL 的 set → opsForValue().set(key, value)。
- 正 TTL 的 set → opsForValue().set(key, value, ttl, TimeUnit.SECONDS)。
- QywxApi 的 expiresIn 为 int；大于零使用带秒 TTL 的重载，否则使用不带 TTL 重载，保留旧工具语义。
- del → delete(key)，所有已发现调用为单 Key，不合并原删除步骤。
- setIfAbsent → opsForValue().setIfAbsent(key, value, ttl, TimeUnit.SECONDS)，保留单命令原子性及原 Boolean 拆箱行为。
- 专利 dealQywxPatentMsg 原 if (lock) return 保持原样，本次不修复该判断或锁归属问题。
- getExpire → getExpire(key, TimeUnit.SECONDS)，保留 Redis -1/-2 哨兵值，不错误解释为永久缓存的零。
- sSetAndTime → opsForSet().add，然后仅在 ttl > 0 时 expire(key, ttl, TimeUnit.SECONDS)。
- Jellyfin 两处先查询旧 TTL，使用 Math.max(剩余播放秒数, 旧 TTL)，add 后仅正数时 expire。保持集合元素：远程集合使用用户名，全部播放集合使用设备 ID。
- sGetSetSize → opsForSet().size；setRemove → opsForSet().remove。不额外引入 null-to-zero 或错误回退。
- 不复制旧工具的 try/catch、printStackTrace、false/0 回退；原有业务外层 catch 不扩大修改。

## 已核实证据（迁移前行号）
- RedisUtil.java:85-116：set 吞异常，非正 TTL 无期限；135-159：集合 add/expire 与 size 吞异常。
- QywxApi.java:91-115：缓存读取及动态 TTL 写入；dto/wx/AccessToken.java:12：expiresIn 为 int。
- PatentTask.java:64、88-90：永久计数与原锁判断。
- JellyfinWebhookController.java:75-86、148-175：两类集合与 TTL/后续副作用的顺序。
- QyWeixinCallbackController.java:86、212、225：去重及永久开关。
- RedisConfig.java:23-55：两个模板 Bean 的原序列化契约，本次不修改。

## 测试与运行安全
使用 JUnit 与 Mockito，直接构造被测对象，mock RedisTemplate/ValueOperations/SetOperations 及其他业务依赖。动态 HTTP 场景仅 mock HttpClient，不发送请求。必要时用 ReflectionTestUtils 验证私有业务流程，不为测试改生产接口。不使用 SpringBootTest 或读取任何环境资源。

## 兼容性与回退
不清空缓存，不修改 Key 或序列化器。修改期间不运行服务；源码迁移和工具删除作为同一变更交付。失败只修复本任务文件，禁止通过 Git 回退或覆盖用户其他修改。历史任务、备份和旧构建产物不计入源码零引用标准。
