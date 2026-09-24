# 迁移设计

## 依赖
Boot parent 4.1.1；web 改 webmvc；MyBatis starter 4.1.0；springdoc 3.1.1；XML group 改 tools.jackson.dataformat。测试增加 webmvc-test/security-test starters，均由 BOM 管理版本。保留第三方内部 Jackson 2，不增加 spring-boot-jackson2。

## JSON/XML 边界
业务各 Mapper 保持独立；JSON 使用 JsonMapper.builderWithJackson2Defaults() 后覆盖原有配置，XML Builder configureForJackson2() 后覆盖原有配置。注解 com.fasterxml.jackson.annotation 不动。JavaTimeModule/ParameterNamesModule 已内建；DateTimeFeature 替代旧日期 Feature。节点调用保证数字、布尔、null 文本原语义；JsonProcessingException 改 JacksonException，网络 IOException 重试不变。
企微 XML 静态 Builder 配置 UPPER_CAMEL_CASE，保持 AgentID、消息类型、去重、异步和草稿 TTL；ChatGPT 回调 JSON 注入 JsonMapper，避免 XML 歧义。RSS 忽略未知字段并保留无包装 item。保留 Jellyfin/Transmission/Portainer/企微命名和字段。
自动配置 Mapper 如需定制，仅针对原行为，不覆盖各集成 Mapper。

## Redis
GenericJacksonJsonRedisSerializer 替换 Jackson 2 版本；Builder 配置时间字符串、未知字段、多态类型信息、字段可见性。保持现有 NON_FINAL 多态范围及 validator，不扩大范围、不宣称 allowIfBaseType(Object.class) 是严格白名单。key/hash key 字符串，value/hash value 复用 serializer。StringRedisTemplate 路径保持。

## 学习与验证
旧示例逐行注释归档；新示例使用原生 Jackson 3 Builder，main 执行四组纯内存例子。新增 src/test 的 JUnit/Jupiter 测试，不扫描全应用、不加载 yaml、不建立外部连接，验证真实 Mapper/serializer 和最小 MVC/Security 配置。

## 风险与回滚
不可变 Mapper、默认字段行为、日期和非受检异常是主要迁移风险，以回归断言验证。旧 Redis 不兼容属于用户接受范围。用户自行处理 Git 和部署回滚，Agent 不执行 Git。
