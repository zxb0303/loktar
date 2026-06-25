# 后端开发规范

> 基于 loktar Spring Boot 3 + Java 21 项目真实代码提炼。
> 编写原则：**记录现实而非理想，仅覆盖有代码示例支持的模式。**

---

## 规范索引

| 规范 | 说明 | 状态 |
|------|------|------|
| [API 路由](./api-routes.md) | Controller 路由定义、注解模式、构造器注入 | 已填充 |
| [身份验证检查](./auth-checks.md) | SecurityConfig 白名单鉴权模式 | 已填充 |
| [日志记录](./logging-guidelines.md) | @Slf4j + 参数化日志模式 | 已填充 |
| [目录结构](./directory-structure.md) | 按业务模块分包的完整包结构 | 已填充 |
| [数据库规范](./database-guidelines.md) | MyBatis + MBG + Lombok Domain 模式 | 已填充 |
| [错误处理](./error-handling.md) | @SneakyThrows + SchedulerConfig 全局兜底 | 已填充 |
| [质量规范](./quality-guidelines.md) | 代码审查清单、必须模式、禁止项 | 已填充 |
| [测试规范](./tests.md) | src/test/ 为空的现状声明 | 已填充 |
| [前端表单](./frontend-forms.md) | 无前端代码的现状声明 | 已填充 |

---

## 技术栈速览

| 项目 | 值 |
|------|-----|
| 语言 | Java 21 |
| 框架 | Spring Boot 3 |
| ORM | MyBatis + MyBatis Generator 2.0.0 |
| 日志 | SLF4J (Lombok @Slf4j) + Logback |
| HTTP 客户端 | JDK 11 HttpClient (Spring 单例) |
| 缓存 | Redis |
| 定时任务 | Spring @Scheduled + ThreadPoolTaskScheduler |
| 安全 | Spring Security (HTTP Basic + 白名单) |
| 实体类 | Lombok (由 MBG 插件生成) |
| 测试 | 无（src/test/ 为空） |
| 前端 | 无（纯后端 API 服务） |

---

## 编写原则

1. **记录现实**：规范基于代码库实际模式，不引入理想化约定
2. **附代码引用**：每条规范附具体代码文件路径
3. **标注现状**：测试和前端等无代码的模式明确声明为空
4. **可验证**：所有模式均可在代码库中找到对应示例
