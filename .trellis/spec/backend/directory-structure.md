# 目录结构规范

> 基于 loktar 项目真实包结构提炼。

---

## 概述

项目采用按业务模块分包的组织方式，每个模块包含独立的 Controller、Service、Mapper、Domain、DTO 层。

---

## 目录布局

```
src/main/java/com/loktar/
├── conf/              ← 配置类（SecurityConfig、RedisConfig、SchedulerConfig 等）
├── domain/            ← 数据库实体（MyBatis Generator 生成，Lombok 化）
│   ├── common/
│   ├── cxy/
│   ├── github/
│   ├── investment/
│   ├── lottery/
│   ├── newhouse/
│   ├── patent/
│   ├── qywx/
│   ├── second/
│   └── transmission/
├── dto/               ← 数据传输对象
│   ├── bandwagonhost/
│   ├── cxy/
│   ├── docker/
│   ├── jellyfin/
│   ├── lottery/
│   ├── newhouse/
│   ├── openai/
│   ├── patent/
│   ├── rss/
│   ├── transmission/
│   └── wx/
├── mapper/            ← MyBatis Mapper 接口
│   ├── common/
│   ├── cxy/
│   ├── github/
│   ├── investment/
│   ├── lottery/
│   ├── newhouse/
│   ├── patent/
│   ├── qywx/
│   ├── second/
│   └── transmission/
├── service/           ← 业务逻辑层（接口 + impl 子包）
│   ├── common/
│   ├── github/
│   ├── land/
│   ├── lottery/
│   ├── newhouse/
│   ├── patent/
│   ├── second/
│   └── transmission/
├── task/              ← 定时任务（@Scheduled）
│   ├── car/
│   ├── common/
│   ├── cxy/
│   ├── github/
│   ├── investment/
│   ├── ip/
│   ├── land/
│   ├── lottery/
│   ├── patent/
│   ├── relx/
│   └── transmission/
├── util/              ← 工具类
│   └── wx/            ← 微信相关工具（qywx、aes 子包）
├── web/               ← Controller 层（HTTP 端点）
│   ├── azure/
│   ├── certimate/
│   ├── cxy/
│   ├── github/
│   ├── investment/
│   ├── jellyfin/
│   ├── land/
│   ├── lottery/
│   ├── newhouse/
│   ├── openai/
│   ├── patent/
│   ├── qywx/
│   ├── redis/
│   ├── rss/
│   ├── second/
│   ├── synology/
│   ├── test/
│   └── transmission/
├── listener/          ← 事件监听器（Redis Key 过期等）
├── learn/             ← JDK 版本特性学习代码（非生产代码）
└── LoktarApplication.java  ← 启动类
```

---

## 模块组织规则

| 层 | 包路径模式 | 说明 |
|----|-----------|------|
| Controller | `com.loktar.web.{module}` | HTTP 端点定义 |
| Service | `com.loktar.service.{module}` | 接口 + `impl/` 子包 |
| Mapper | `com.loktar.mapper.{module}` | MyBatis 接口 + XML 映射 |
| Domain | `com.loktar.domain.{module}` | 数据库实体类 |
| DTO | `com.loktar.dto.{module}` | 数据传输对象 |
| Task | `com.loktar.task.{module}` | 定时任务 |
| Config | `com.loktar.conf` | 全局配置类 |
| Util | `com.loktar.util` | 工具类（扁平结构） |

---

## 新增模块时的规则

1. 在 `web/`、`service/`、`mapper/`、`domain/` 下创建对应模块子包
2. Service 采用接口 + `impl/` 实现类的模式
3. Domain 实体通过 MyBatis Generator 生成，使用 Lombok 插件
4. 定时任务放在 `task/{module}/` 下

---

## 禁止项

- 禁止在 `learn/` 目录中编写生产代码（该目录仅用于 JDK 特性学习）
- 禁止跨模块直接引用其他模块的 Service 实现类（应通过接口）
- 禁止在 `web/` 中放置非 Controller 类
