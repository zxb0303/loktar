# Spring Boot 4.1.1 与 Jackson 3 升级

## 目标
将项目升级到 Spring Boot 4.1.1，应用 JSON/XML/Redis 全面使用 Jackson 3，保持现有业务协议。

## 用户批准
用户已批准完整计划并要求实施；原计划位于 C:/Users/zhaoxiaobin/.qoder/shared_client/cache/plans/Spring_Boot_Jackson_升级_02728a9a.md，不修改该文件。

## 要求与验收
- R1：Boot 4.1.1、MyBatis 4.1.0、springdoc 3.1.1，Jackson 3.1.5 由 Boot BOM 管理，Java 21 编译通过。
- R2：JSON/XML Builder、节点、异常、日期 API 迁移；命名策略、未知字段、空值与日期语义经隔离测试验证。
- R3：Redis 使用 GenericJacksonJsonRedisSerializer，新写入对象、集合、Map、日期字段、标量和 null 往返通过；StringRedisTemplate 职责不变。
- R4：旧 learn/jackson/JacksonTest.java 移到 jackson2，保留有效 package、其余逐行注释；jackson3/JacksonTest.java 可运行并覆盖旧四组示例与七种命名策略。
- R5：新增纯内存 JSON/XML/Redis、最小 Spring 上下文和 MockMvc 鉴权测试，使用实际配置而非重复实现。

## 排除项与限制
禁止任何 Git 命令。禁止读取敏感配置、密钥或证书。禁止连接真实 Redis/MySQL、调用外部业务服务、启动调度、运行 Generator、清库、打包部署。Dockerfile、Java、Azure SDK、无关依赖、数据库结构、鉴权和调度语义不变。第三方内部 Jackson 2 依赖保留，不引入 Boot Jackson 2 兼容模块。

## 未验证项
敏感配置的旧属性与真实环境联调不在当前验证范围；如成为阻塞，先请求授权。编译和测试成功不等同生产联调通过。
