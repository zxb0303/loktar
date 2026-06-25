# 身份验证检查规范

> 基于 loktar 项目 `SecurityConfig.java` 真实代码提炼。

---

## 概述

项目使用 Spring Security 进行 HTTP 端点鉴权，采用**白名单模式**：
默认所有请求需要认证，仅 `PUBLIC_ENDPOINTS` 数组中列出的路径允许匿名访问。

---

## 鉴权架构

### 配置位置

> `src/main/java/com/loktar/conf/SecurityConfig.java`

- `@Configuration` + `@EnableWebSecurity`
- 单一 `SecurityFilterChain` Bean
- CSRF 已禁用（项目无前端表单，均为 API 调用）
- 认证方式：HTTP Basic

### 白名单机制

```java
private static final String[] PUBLIC_ENDPOINTS = {
    "/jellyfin/webhook",
    "/certimate/webhook",
    "/synology/sendMsg",
    "/github/notifyMsg",
    "/qywx/callback/**",
    "/patentpdf/**",
    "/patentpdfv2/**",
    "/patentdoc/**",
    "/tiktok/getTimeParams",
    "/test/**"
};
```

### 过滤链配置

```java
http
    .csrf(CsrfConfigurer::disable)
    .authorizeHttpRequests(auth -> auth
        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
        .anyRequest().authenticated())
    .httpBasic(Customizer.withDefaults());
```

---

## 白名单路径分类

| 路径 | 用途 |
|------|------|
| `/jellyfin/webhook` | Jellyfin 媒体服务器 webhook 回调 |
| `/certimate/webhook` | 证书管理 webhook |
| `/synology/sendMsg` | Synology NAS 消息推送 |
| `/github/notifyMsg` | GitHub 仓库推送通知 |
| `/qywx/callback/**` | 企业微信回调（消息接收 + URL 验证） |
| `/patentpdf/**`, `/patentpdfv2/**`, `/patentdoc/**` | 专利 PDF/文档接口 |
| `/tiktok/getTimeParams` | TikTok 参数获取 |
| `/test/**` | 测试端点 |

---

## 规则

| 规则 | 说明 |
|------|------|
| 白名单优先 | 新增公开端点必须加入 `PUBLIC_ENDPOINTS` 数组 |
| 白名单路径带前导斜杠 | 与 Controller 的 `@RequestMapping` 不同，此处路径以 `/` 开头 |
| 通配符 `**` 用于子路径 | 如 `/qywx/callback/**` 匹配所有子路径 |
| CSRF 全局禁用 | 项目无浏览器表单提交场景，均为 API 调用 |
| 默认拒绝 | 未在白名单中的路径一律需要 HTTP Basic 认证 |

---

## 新增端点的鉴权检查清单

1. 该端点是否需要被外部服务（如 webhook 提供方）匿名调用？
   - 是 → 将完整路径加入 `PUBLIC_ENDPOINTS`
   - 否 → 不需要额外操作，默认需要认证
2. 白名单路径是否使用通配符覆盖所有子路径？
3. 是否在 `SecurityConfig.java` 中修改后验证了鉴权行为？

---

## 禁止项

- 禁止在 Controller 层手动实现鉴权逻辑（统一由 `SecurityConfig` 管理）
- 禁止使用 `@PermitAll` 等注解绕过白名单机制
- 禁止在白名单中使用无前导斜杠的路径
