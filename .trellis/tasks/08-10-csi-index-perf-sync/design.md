# 技术设计 - 中证指数每日行情同步

## 模块边界

| 层 | 文件 | 说明 |
|----|------|------|
| DDL | `src/main/resources/zxb.sql` | 新增 `equity_index_perf_daily` 建表语句，按字母序插入（紧跟 `equity_index_dividend_yield_daily` 之后、`file_name` 之前） |
| Domain | `com.loktar.domain.investment.EquityIndexPerfDaily` | MBG 生成，Lombok `@Data` |
| Mapper | `com.loktar.mapper.investment.EquityIndexPerfDailyMapper` + `mapper/investment/xml/EquityIndexPerfDailyMapper.xml` | MBG 生成基础 CRUD，手工补充自定义查询；XML 位置与现有 investment 模块一致（src/main/java 下 xml 子包） |
| Task | `com.loktar.task.investment.ChinaEquityIndexPerfTask` | 新增定时任务 |

不新增 Service、Controller、DTO（响应直接用 `JsonNode` 解析，与 `FundNavTask` 模式一致）。

## 表结构

```sql
CREATE TABLE `equity_index_perf_daily`  (
    `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '自增主键',
    `index_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '指数代码',
    `index_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '指数中文简称',
    `trade_date` date NOT NULL COMMENT '交易日期',
    `open` decimal(14, 2) NULL DEFAULT NULL COMMENT '开盘点位',
    `high` decimal(14, 2) NULL DEFAULT NULL COMMENT '最高点位',
    `low` decimal(14, 2) NULL DEFAULT NULL COMMENT '最低点位',
    `close` decimal(14, 2) NULL DEFAULT NULL COMMENT '收盘点位',
    `change` decimal(14, 2) NULL DEFAULT NULL COMMENT '涨跌点数',
    `change_pct` decimal(6, 4) NULL DEFAULT NULL COMMENT '涨跌幅(%)',
    `trading_vol` double NULL DEFAULT NULL COMMENT '成交量',
    `trading_value` decimal(16, 2) NULL DEFAULT NULL COMMENT '成交额',
    `cons_number` int(11) NULL DEFAULT NULL COMMENT '成分股数量',
    `peg` decimal(8, 2) NULL DEFAULT NULL COMMENT '市盈率',
    `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`) USING BTREE,
    UNIQUE INDEX `uk_index_code_trade_date`(`index_code`, `trade_date`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '中证指数每日行情表' ROW_FORMAT = Dynamic;
```

- `change` 为 MySQL 保留字，MBG `autoDelimitKeywords=true` 会自动加反引号。
- `trade_date` 用 `date` 类型（对齐 `fund_nav.nav_date`），MBG 映射为 `LocalDate`。
- 唯一索引兜底防重。

## 数据流

```
@Scheduled 触发
  → 入口 allMatch 检查：全部指数当日数据已存在 → return（不发请求）
  → 遍历 INDEX_CODES：
      已存在 → continue
      GET index-perf?indexCode=&startDate=今日&endDate=今日（30s 超时，带 UA/Accept 头）
      JsonNode 解析：code != "200" 或 data 为空 → log 并 continue
      逐条映射 EquityIndexPerfDaily（tradeDate 按 yyyyMMdd 解析为 LocalDate）
      selectByIndexCodeAndTradeDate 二次校验：不存在则 insert（手动设置 createTime/updateTime）
  → 单指数异常 try/catch 记录日志，不中断其他指数
```

## 关键决策

| 决策 | 选择 | 理由 |
|------|------|------|
| 指数列表位置 | 任务类顶部 `static final List<String> INDEX_CODES` | 对齐 `FundNavTask.FUND_CODES` 与「请求参数常量定义规范」 |
| 响应解析 | 注入 `ObjectMapper` + `JsonNode` | 对齐 `FundNavTask`，无需新建 DTO |
| 已存在数据处理 | 跳过（不更新） | 当日行情收盘后固定，与 `FundNavTask` 现行逻辑一致 |
| 通知 | 无 | 用户明确要求仅入库 |
| cron | `0 0/10 15-18 * * *` | 用户指定 15-18 点，频率沿用同类任务 10 分钟 |
| 实体生成方式 | MBG（`mvnw.cmd compile mybatis-generator:generate`） | 项目规范禁止手写 Domain；MBG 前需 compile 保证 LombokPlugin 已编译 |

## MBG 执行流程（含用户操作）

1. 用户在数据库执行 DDL 建表。
2. 用户修改 `mybatis-generator.properties`（敏感文件，Agent 不触碰）：`table.tableName=equity_index_perf_daily`（及 schema）。
3. Agent 执行 `mvnw.cmd compile mybatis-generator:generate`。
4. 生成的文件按现有包结构移动到 `investment` 子包并修正 package 声明、XML namespace/type 引用。

## 兼容性 / 回滚

- 纯新增，不改任何存量代码；回滚即删除新增文件与数据表。
- `zxb.sql` 仅追加，不影响既有 DDL。
