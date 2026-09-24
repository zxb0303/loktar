# RedisUtil 全项目迁移需求

## 目标
移除冗余 Redis 工具包装，让全项目原调用方直接使用现有 RedisTemplate，减少中间层，同时保持正常业务行为与缓存数据兼容。

## 已确认事实与决定
- 已检查实际源码：12 个生产调用类依赖 RedisUtil，当前 src/test 无 Java 测试文件；符号索引中已删除测试不可作为当前事实。
- RedisConfig 已提供 JSON 对象 RedisTemplate<String, Object> 和独立 StringRedisTemplate。
- 用户已明确选择 Redis 操作异常向上抛出，不保留旧工具类返回 false/0 的吞异常行为。
- 用户已批准全项目迁移计划并要求实施；不修改已批准计划原文件。

## 需求
- R1：删除 RedisUtil.java，迁移全部原调用方的 import、字段、构造器与调用，不新增替代包装类。
- R2：保持 Redis Key、值类型、JSON 序列化、TTL 数值及单位、永久缓存、集合成员和现有业务判断不变。
- R3：Redis 写入、过期和集合操作异常按原上层处理机制传播；不额外捕获并伪装成功或空集合，不扩大修改已有上层 catch。
- R4：已有 StringRedisTemplate 数据流、HTTP 路由、虚拟线程调度及定时任务配置保持原样。
- R5：提供隔离的 JUnit/Mockito 回归测试，不启动 Spring，不访问 Redis、数据库或外部业务系统。

## 验收标准
- AC1 / R1：生产源码及新增测试无 RedisUtil 类型、字段、调用残留；工具类源码删除，12 个调用类直接依赖现有对象 RedisTemplate。
- AC2 / R2：测试验证正数、零、负数 TTL；永久状态写入；Jellyfin 集合 TTL 取最大值且非正数不 expire；回调 30 秒去重及专利 10 秒锁原分支。
- AC3 / R3：写入与集合异常可观察地向上传递，不继续执行依赖该操作成功的后续逻辑。
- AC4 / R4：RedisConfig 和既有 StringRedisTemplate 用途不改；无缓存格式及 Key 迁移。
- AC5 / R5：Java 21 编译及隔离测试通过，并完成全范围引用检查与质量验证。

## 不在范围
不修复专利锁判断/释放、Transmission 重试等独立问题；不新增 Lua、事务、pipeline、缓存抽象；不更改依赖、配置、锁文件、.gitignore 或其他任务；不执行 Git 命令，不读取敏感文件或真实缓存数据。
