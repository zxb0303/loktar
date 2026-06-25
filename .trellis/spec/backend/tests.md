# 测试规范

> 基于 loktar 项目当前测试现状编写。

---

## 当前状态

**`src/test/` 目录为空，项目无任何单元测试或集成测试代码。**

```
src/
├── main/
│   ├── java/com/loktar/...
│   └── resources/...
└── test/
    └── (空)
```

---

## 说明

项目当前未建立测试体系。以下为事实记录，非推荐做法：

- 无 JUnit 测试类
- 无 Spring Boot Test 集成测试
- 无 Mockito 单元测试
- 无测试配置文件（`application-test.yml` 存在但未使用）
- 功能验证主要依赖手动调用 API 端点（通过 Controller 的 test 端点）

---

## 新增测试时的约定

当未来引入测试时，应遵循以下基础约定：

| 项目 | 约定 |
|------|------|
| 测试框架 | JUnit 5（`org.junit.jupiter`） |
| Spring 集成测试 | `@SpringBootTest` |
| Mock 框架 | Mockito（`@MockBean` / `@Mock`） |
| 测试目录 | `src/test/java/com/loktar/` |
| 测试命名 | `{ClassName}Test.java`（如 `LandServiceTest.java`） |

> 以上约定基于项目 Spring Boot 3 + Java 21 技术栈的默认能力，当前无实际代码示例验证。

---

## 禁止项

- 禁止在 `src/main/` 中编写测试代码（当前 `web/test/` 下的类是 API 端点，非单元测试）
- 禁止跳过 `src/test/` 直接在 Controller 中验证逻辑（仅作为临时手段）
