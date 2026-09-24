# RedisTemplate 迁移执行与验证

## 已完成准备
- [x] 用户批准完整计划与异常向上抛出的决定。
- [x] 核对 12 个调用类、RedisConfig、TTL 边界及测试依赖。
- [x] 读取 backend index、api-routes、quality-guidelines、error-handling、logging-guidelines、tests 和两个 shared thinking guides。
- [x] 使用文件工具建立本任务材料及 JSONL 规范清单；其他任务与原计划文件不改。

## 执行清单
- [x] 按 design.md 迁移全部 12 个调用方，保持原业务分支。
- [x] 全部引用迁移后通过 DeleteFile 删除 RedisUtil.java。
- [x] 新增隔离单测：RedisController 对象和集合 TTL/异常；QywxApi 命中与动态 TTL；Jellyfin 两组集合的 TTL/移除/大小/异常与副作用顺序；PatentTask 永久计数及原锁分支；三个企业微信回调的去重失败及异常。
- [x] 验证原有 StringRedisTemplate 注入及调用未改变。
- [x] 源码范围检索 RedisUtil、redisUtil 零残留，使用 IDE GetProblems 与 Maven 编译/测试。
- [x] 全范围 trellis-check 验证；补充本任务验证结果。
- [x] 更新已有测试规范及索引中的测试现状，完成交付；不执行 Git 或归档脚本。

## 验证命令
已验证 Maven 3.9.12；显式使用 IDE 的 IBM Semeru Java 21.0.12，避免默认 JAVA_HOME 指向旧版 Java 21.0.9。

```powershell
$env:JAVA_HOME = 'E:\JDK\semeru-21.0.12'
& 'E:\Maven\apache-maven-3.9.12\bin\mvn.cmd' -B -f 'E:\Project\loktar\pom.xml' '-Dtest=RedisControllerTest,QywxApiTest,JellyfinWebhookControllerTest,PatentTaskTest,QyWeixinCallback*ControllerTest' compiler:compile compiler:testCompile surefire:test
```

只运行编译器与 Surefire 目标，不运行 resources、test 生命周期、Spring Boot、MyBatis Generator。显式指定本次 7 个测试类，避免扫描历史构建残留测试。测试直接构造对象且 mock 外部依赖，不接触已存在的敏感环境资源；未更改项目依赖版本。

## 工具约束与替代路径
- 当前 python --version 无可用输出、py 命令不存在，已知位置未找到 Python 安装。
- task.py create 的 task_store.py 会调用 get_developer/resolve_default_branch；task.py start 会在缺少 branch 时调用 current_branch_name，均可能执行被禁止的 Git 命令，因此不执行生命周期脚本。
- get_context.py --mode packages 的规范发现改为 Glob/Read：本项目单仓库，规范位于 .trellis/spec/backend/ 与 .trellis/spec/guides/。
- 显式向子代理传入本任务路径；不依赖活动任务运行时指针，不修改 Trellis 系统。
- 不通过 git diff/status 检查变更；以已读源码、工具编辑记录、引用检索与测试验证本次修改。

## 验证结果
- 完成全部 12 个生产调用类迁移并删除 RedisUtil.java；原 Key、值类型、JSON 序列化、路由、定时调度与业务判断均按设计保留。
- 生产及测试 Java 源码检索 RedisUtil/redisUtil 零残留；trellis-check 再次核实源码删除。另使用 Glob 检查 target/classes 中 RedisUtil*.class，零匹配，未清理其他构建产物。
- 首次 Java 21 验证编译 319 个生产文件及 7 个测试文件，43 项测试全部通过。
- 修正 QywxApiTest 的泛型 matcher 后，2026-09-24 最终复验：生产编译目标确认已是最新，重新编译 7 个测试文件；Tests run: 43, Failures: 0, Errors: 0, Skipped: 0；BUILD SUCCESS，耗时 7.159 秒。
- 用例分布：RedisController 8、QywxApi 5、Jellyfin 18、PatentTask 6、三个回调各 2。
- IDE GetProblems 检查全部 12 个生产修改文件和 7 个测试文件：No errors found。
- trellis-check 已检查全部调用方、测试及相关配置/数据流，未发现本次迁移需要修正的问题；确认动态非正 TTL、Jellyfin 集合成员与过期顺序、永久状态、30 秒去重、10 秒锁原分支及独立 StringRedisTemplate 契约。
- 最终构建不再报告 QywxApiTest 的 unchecked 警告；仍有 deprecated API 和 Mockito/Byte Buddy 动态 agent 加载提示，不阻断构建。本次未扩大修改相关依赖或 JVM 配置。

## 规范同步与交付边界
- 已同步 .trellis/spec/backend/tests.md 和 index.md 中由本次新增测试直接改变的现状，记录隔离模式、Redis 可执行契约、边界/错误断言及安全验证命令；未新增规范文件。
- 检查发现的其他历史规范版本、日志与错误处理说明不属于本次迁移，不扩展修改。
- 未运行 Spring、真实 Redis/数据库/HTTP 或模型服务；Mock 测试不代表真实序列化往返、业务联通性或回调成功异步路径验证。
- 未执行任何 Git 命令、生命周期/归档脚本；未读取敏感配置及凭据，未修改 RedisConfig、依赖、锁文件、.gitignore、其他任务或原批准计划文件。
- 本任务材料直接维护完成状态，保留在当前任务目录，不执行自动归档。
