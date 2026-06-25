# 前端表单规范

> 基于 loktar 项目当前前端代码现状编写。

---

## 当前状态

**本项目无任何前端代码。**

项目是一个纯后端 Spring Boot 应用，不包含：

- 无 HTML 模板（无 Thymeleaf / FreeMarker / JSP）
- 无前端框架（无 React / Vue / Angular）
- 无静态资源（无 `src/main/resources/static/`）
- 无 CSS / JavaScript 文件

---

## 模板文件说明

`src/main/resources/template/` 目录下仅有配置文件模板，**非前端页面**：

```
src/main/resources/template/
├── config.json       ← VPS 配置模板
├── nginx-vps.conf     ← Nginx 配置模板
├── proxies.yaml      ← 代理配置模板
└── sshd_config       ← SSH 配置模板
```

---

## 说明

项目所有功能通过以下方式触发，不依赖前端表单：

| 触发方式 | 说明 |
|---------|------|
| HTTP API 调用 | 通过 Controller 端点手动触发 |
| 定时任务 | `@Scheduled` cron 表达式自动执行 |
| Webhook 回调 | 第三方服务（Jellyfin、GitHub、企业微信等）回调 |
| 企业微信指令 | 通过企业微信消息指令交互 |

---

## 新增前端时的约定

当未来引入前端时，应遵循以下基础约定：

| 项目 | 约定 |
|------|------|
| 模板引擎 | Thymeleaf（Spring Boot 默认支持） |
| 静态资源目录 | `src/main/resources/static/` |
| 模板目录 | `src/main/resources/templates/` |
| CSRF | 需重新启用（当前在 `SecurityConfig` 中已禁用） |

> 以上约定基于 Spring Boot 默认能力，当前无实际代码示例验证。

---

## 禁止项

- 禁止在无前端的情况下臆造前端规范
- 禁止将 `template/` 目录下的配置文件模板误认为前端页面模板
