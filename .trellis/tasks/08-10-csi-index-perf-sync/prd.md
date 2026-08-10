# 中证指数每日行情同步

## Goal

新增定时任务，调用中证指数官网指数行情接口（index-perf），将所配置指数当日的行情数据同步入库，保证幂等，不发送任何通知。

## Background

- 数据源：`https://www.csindex.com.cn/csindex-home/perf/index-perf?indexCode={indexCode}&startDate={yyyyMMdd}&endDate={yyyyMMdd}`
- 响应结构：`{ "code": "200", "msg": "Success", "data": [ { tradeDate, indexCode, indexNameCnAll, indexNameCn, open, high, low, close, change, changePct, tradingVol, tradingValue, consNumber, peg } ], "success": true }`
- 项目现有同类任务：`ChinaEquityIndexTask`（股息率 Excel 同步）、`FundNavTask`（基金净值同步），本任务参照其模式实现。
- 数据表尚未创建，需本次任务提供 DDL。

## Requirements

1. 支持多指数配置：指数代码以 `static final` 列表常量定义在任务类顶部，初始包含 `930955`（红利低波100）。
2. 定时调度：每天 15-18 点，每 10 分钟执行一次（cron：`0 0/10 15-18 * * *`），与现有投资类任务风格一致。
3. 每次执行按 `startDate=endDate=今日` 请求当日数据；非交易日接口返回空 data 时正常跳过，不报错。
4. 幂等入库：
   - 入口处检查所有指数当日数据是否均已存在，全部存在则直接返回，不发起 HTTP 请求；
   - 循环内对单个指数做存在性检查，已存在则跳过；
   - 数据表建立 `(index_code, trade_date)` 唯一索引兜底。
5. 仅数据入库，不推送企业微信等任何通知。
6. 新建数据表 DDL 需追加至 `src/main/resources/zxb.sql`（按字母序插入对应位置）。
7. Domain 实体、Mapper 接口与 XML 通过 MyBatis Generator 生成（不手写实体类），自定义查询方法手工补充。
8. HTTP 请求使用项目单例 `HttpClient` Bean 与注入的 `ObjectMapper`，设置 30 秒超时。
9. 定时任务调度仅在 pro 环境生效（由现有 `SchedulerConfig` 的 `@Profile` 控制，无需额外处理）。
10. 提供 Controller 支持手动触发当日同步（dev 环境调试用，对齐 FundNavController 模式）；并提供历史数据初始化方法，支持按日期区间批量拉取入库（幂等）。

## Acceptance Criteria

- [ ] `zxb.sql` 中新增表 DDL，含 `create_time`/`update_time` 与 `(index_code, trade_date)` 唯一索引
- [ ] 用户建表后，MBG 成功生成 Domain、Mapper、XML，且编译通过
- [ ] 新增定时任务类，调度时间 15-18 点每 10 分钟，幂等同步当日行情
- [ ] 接口返回空数据、非 200、异常时任务不抛出未捕获异常导致中断（日志记录即可）
- [ ] `mvn compile` 编译通过，无 lint 错误

## Constraints / Out of Scope

- 不做历史数据回填的自动调度；历史数据通过 Controller 手动接口按日期区间初始化。
- 不提供查询接口。
- 不发送通知。
