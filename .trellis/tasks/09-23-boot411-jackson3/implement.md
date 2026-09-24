# 实施与验证

1. 依赖与生产源码（不含 learn）迁移已完成。生产子代理核实替换方案，但无法使用精确编辑工具；实际由主代理落盘，无并行编辑。
2. 学习示例及基础隔离测试由独立子代理创建，主代理删除旧学习占位、补充迁移边界测试并统一构建。
3. Java 21 下运行 mvn -B -Dmaven.resources.skip=true test，修复范围内错误，运行学习示例。
4. 最终进行全范围质量检查，核查活动源码无 Jackson 2 core/databind/XML 残留，检查 JSON/XML/Redis/MVC 契约及敏感配置隔离。
5. 记录修改清单、实际测试结果、受限项，不提交 Git。

## 实施前边界
已经存在的行为差距是 Boot/Jackson 版本及已移除 API，不改变业务流程。修改 pom、Jackson 直接使用点、RedisConfig、RSS 注解、learn 和 src/test。保留命名、空值、未知字段、日期及异常边界，以测试证明等价。

## 运行限制
不执行任何 Git 命令；不运行可能读取 Git 的 task.py/get_context.py 生命周期脚本。task.py 的 start 会记录分支，因此本次用文件工具手动持久化任务状态并显式向子代理传入任务路径。不读取 application*.yml、.env、密钥证书或真实凭证文件。构建跳过资源，不运行 clean/generator/package/spring-boot:run。

## 已确认资料
Boot 4.1.1 BOM：Jackson 3.1.5、Jackson 2.21.5、Spring Data 2026.0.1、JUnit Jupiter 6.0.3。JsonMapper.builderWithJackson2Defaults 是 Jackson 3 API；GenericJacksonJsonRedisSerializer 接收 tools.jackson.databind.ObjectMapper。maven.resources.skip 是资源插件支持的属性。

## 完成记录（2026-09-23）
- 依赖：Boot 4.1.1、WebMVC Starter、MyBatis 4.1.0、springdoc 3.1.1、Jackson XML 3.1.5，新增 MVC/Security 测试 Starter。
- 生产：conf 下 JacksonConfig/RedisConfig、直接使用 Jackson 的 util/service/task/web、RSS XML 注解完成迁移；保持各集成独立 Mapper、命名/日期/空值和异常边界。未改鉴权、调度、Dockerfile、Java 21 或 Azure SDK。
- 学习：原 learn/jackson 文件已移除；jackson2 文件除 package 外全部逐行注释；jackson3 main 覆盖四组示例和七种命名策略。
- 测试：src/test/java/com/loktar/jackson 下 6 个测试类和 1 个辅助类；使用实际 Mapper、serializer、生产调用点与 mock 外部依赖。
- 规范：同步 backend/index.md、tests.md、external-integrations.md 的技术栈、隔离测试与 Jackson 3 契约。未修改用户原计划文件。

## 实际验证结果
- 最终命令：`mvn -B -ntp -Dmaven.resources.skip=true test`，JAVA_HOME 为 `E:/JDK/semeru-21.0.12`。
- Java 21 编译 322 个生产源文件、7 个测试/辅助源文件通过；70 项测试全部通过，0 failures / 0 errors / 0 skipped，BUILD SUCCESS。
- 明细：Boot/MVC/Security 5、学习示例 1、Portainer 8、生产 JSON/数值转换 40、XML 10、Redis 6。
- Redis 包含真实缓存 AccessToken 的类型和父类字段、私有日期字段、集合、Map、自然标量及 null；没有建立连接。
- dependency:tree 已确认 Jackson 3.1.5、Redis 4.1.1、MyBatis 4.1.0、springdoc 3.1.1；未引入 spring-boot-jackson2。Azure/docker-java/LangChain4j/swagger 内部保留 Jackson 2 属于预期。
- 定点扫描 conf/domain/dto/mapper/service/task/util 及安全 web 范围，未发现活动 Jackson 2 core/databind/XML 导入；IPUtil 只有注释中的旧 asText。QSNG 已在此前迁移，因其上下文含敏感信息，最终扫描明确排除且不再读取。
- 学习归档扫描仅剩 package 为活动行，learn/jackson* 中仅有 jackson2、jackson3 两个文件。
- IDE 检查迁移配置与新增测试未报告错误。仍有原有 deprecated API、Mockito 动态 agent 警告，不影响本次测试。

## 回归中澄清的边界
- Spring Framework 7 发现 swagger 带入 Jackson 2 YAML 时会注册 YAML converter；JSON/XML 均使用 Jackson 3，测试断言 YAML 不接管它们，不删除第三方依赖。
- StringRedisSerializer.serialize(null) 在 4.1.1 返回空字节数组；deserialize(null) 仍为 null。按目标 API 修正测试，未改生产 key serializer。
- Spring 对 StringRedisTemplate mock 的生命周期调用不是 Redis 操作；明确验证 setBeanClassLoader/afterPropertiesSet 后禁止其他交互。
- NON_FINAL 下根级小 Long 仍按自然数值推断为 Integer，本次不扩大多态范围。
- Portainer 的对象/数组 Id 保留旧空串语义和原有请求分支，未趁升级增加校验。

## 未验证项
未读取敏感应用配置，因此尚未核查其中旧 Boot/Jackson 属性；未运行完整应用、真实 MySQL/Redis/Azure/企微/Docker 联调或部署。基金 fallback、提醒草稿业务路径未单独作直接回归，已完成源码迁移及通用 JSON 异常测试。不声称隔离测试覆盖所有生产行为。无 Git 操作、旧 Redis 读取/迁移/清理、Generator 或打包发布。
