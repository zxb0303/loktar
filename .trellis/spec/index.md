# Trellis 规范索引

> loktar 项目开发规范总入口。
> 所有规范基于代码库真实模式提炼，非理想化约定。

---

## 规范目录

| 目录 | 说明 |
|------|------|
| [backend/](./backend/index.md) | 后端开发规范（API 路由、鉴权、日志、数据库、错误处理等） |
| [guides/](./guides/index.md) | 思维引导指南（跨层思考、代码复用） |

---

## 五大核心维度

| 维度 | 规范文件 | 现状 |
|------|---------|------|
| API 路由 | [backend/api-routes.md](./backend/api-routes.md) | `@RestController` + `@RequestMapping` 无斜杠 + 构造器注入 |
| 身份验证 | [backend/auth-checks.md](./backend/auth-checks.md) | `SecurityConfig` 白名单 + HTTP Basic |
| 日志记录 | [backend/logging-guidelines.md](./backend/logging-guidelines.md) | `@Slf4j` + `log.info("{}", v)` 参数化 |
| 测试 | [backend/tests.md](./backend/tests.md) | `src/test/` 为空，无测试代码 |
| 前端表单 | [backend/frontend-forms.md](./backend/frontend-forms.md) | 无前端代码，纯后端 API 服务 |

---

## 快速开始

开发新功能前，请先阅读：

1. [后端规范索引](./backend/index.md) — 了解技术栈和全部规范
2. [质量规范](./backend/quality-guidelines.md) — 代码审查清单
3. [思维引导](./guides/index.md) — 跨层思考和代码复用检查
