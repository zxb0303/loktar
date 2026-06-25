# 数据库规范

> 基于 loktar 项目真实 MyBatis + MyBatis Generator 代码提炼。

---

## 概述

项目使用 MyBatis 作为 ORM 框架，通过 MyBatis Generator (MBG) 自动生成 Mapper 接口、XML 映射文件和 Domain 实体类。
实体类已全部 Lombok 化（通过 MBG Lombok 插件）。

---

## 技术栈

| 项目 | 值 |
|------|-----|
| ORM | MyBatis (Spring Boot Starter) |
| 代码生成 | MyBatis Generator 2.0.0 |
| 实体类 | Lombok 化（`@Data` / `@Builder` 等，由 MBG 插件生成） |
| 数据库 | MySQL |
| Mapper XML | 位于 `src/main/resources/mapper/{module}/` |

---

## MBG 配置

> `src/main/resources/mybatis-generator-config.xml`

- MBG 版本：2.0.0
- 集成 Lombok 第三方插件
- Domain 实体生成到 `com.loktar.domain.{module}`
- Mapper 接口生成到 `com.loktar.mapper.{module}`
- XML 映射文件生成到 `src/main/resources/mapper/{module}/`

### 执行命令

```bash
mvn mybatis-generator:generate
```

---

## Mapper 使用模式

### 构造器注入

Mapper 通过构造器注入到 Service 和 Controller 中：

> `src/main/java/com/loktar/web/lottery/LotteryController.java`

```java
public LotteryController(HZLotteryServiceV2 hZLotteryServiceV2,
                         LotteryHouseMapper lotteryHouseMapper,
                         LokTarConfig lokTarConfig) {
    this.hZLotteryServiceV2 = hZLotteryServiceV2;
    this.lotteryHouseMapper = lotteryHouseMapper;
    this.lokTarConfig = lokTarConfig;
}
```

### 常用操作

> `src/main/java/com/loktar/web/investment/FundNavController.java`

```java
// 查询：按主键
FundNav exist = fundNavMapper.selectByFundCodeAndNavDate(code, today);

// 插入
fundNav.setCreateTime(LocalDateTime.now());
fundNav.setUpdateTime(LocalDateTime.now());
fundNavMapper.insert(fundNav);

// 更新
fundNav.setId(exist.getId());
fundNav.setCreateTime(exist.getCreateTime());
fundNav.setUpdateTime(LocalDateTime.now());
fundNavMapper.updateByPrimaryKey(fundNav);
```

### 批量操作

> `src/main/java/com/loktar/service/land/impl/LandServiceImpl.java`

```java
int num = landMapper.deleteByDate(yearfirstDate);
log.info("{}", year + "年土拍数据开始删除，共" + num + "条");
List<Land> lands = getData(year);
landMapper.insertBatch(lands);
```

---

## 数据同步幂等性

> `src/main/java/com/loktar/task/investment/FundNavTask.java`

定时任务中实现双重数据库存在性检查，保证数据同步幂等：

```java
// 第一次检查：当日数据是否已存在
FundNav exist = fundNavMapper.selectByFundCodeAndNavDate(fundCode, today);
if (exist != null) {
    log.info("{}", fundCode + " 当日数据已存在，跳过");
    continue;
}

// ... 获取数据 ...

// 第二次检查：插入前再次确认
FundNav current = fundNavMapper.selectByFundCodeAndNavDate(fundNav.getFundCode(), fundNav.getNavDate());
if (current == null) {
    fundNavMapper.insert(fundNav);
} else {
    fundNav.setId(current.getId());
    fundNav.setCreateTime(current.getCreateTime());
    fundNav.setUpdateTime(LocalDateTime.now());
    fundNavMapper.updateByPrimaryKey(fundNav);
}
```

---

## 规则

| 规则 | 说明 |
|------|------|
| Mapper 通过构造器注入 | 不使用 `@Autowired` |
| Domain 实体由 MBG 生成 | 不手写实体类 |
| Domain 实体使用 Lombok | 由 MBG Lombok 插件自动添加注解 |
| `createTime` / `updateTime` 手动设置 | 不依赖数据库默认值或自动填充 |
| 数据同步需幂等性检查 | 插入前先查询是否存在 |

---

## 禁止项

- 禁止手写 Domain 实体类（应通过 MBG 生成）
- 禁止在 Mapper XML 中编写复杂 SQL 而不添加注释
- 禁止使用 `@Autowired` 注入 Mapper
- 禁止跳过幂等性检查直接 `insert`（定时任务场景）
