# 执行计划 - 中证指数每日行情同步

## 检查清单（按顺序）

### 1. DDL
- [x] 在 `src/main/resources/zxb.sql` 中按字母序（紧跟 `equity_index_dividend_yield_daily` 之后、`file_name` 之前）插入 `equity_index_perf_daily` 建表段落，格式对齐现有段落（含 `-- Table structure for ...` 注释头与 `DROP TABLE IF EXISTS`），DDL 内容见 `design.md`

### 2. 用户操作卡点（阻塞项）
- [x] 用户在数据库执行建表 DDL
- [x] 用户修改 `mybatis-generator.properties`：`table.tableName=equity_index_perf_daily`（Agent 不修改该敏感文件）

### 3. MBG 生成
- [x] 用户已执行 MBG 生成（实体、Mapper、XML 已归位 investment 子包）
- [x] 生成文件位置与引用确认无误
  - （已由用户完成）`EquityIndexPerfDaily` → `com.loktar.domain.investment`
  - `EquityIndexPerfDailyMapper` → `com.loktar.mapper.investment`
  - XML → `src/main/java/com/loktar/mapper/investment/xml/`，并修正 namespace、parameterType/resultMap type 引用

### 4. 自定义 Mapper 方法
- [x] 接口补充：`EquityIndexPerfDaily selectByIndexCodeAndTradeDate(String indexCode, LocalDate tradeDate)`、`boolean existsByIndexCodeAndTradeDate(String indexCode, LocalDate tradeDate)`
- [x] XML 补充对应 SQL（参考 `EquityIndexDividendYieldDailyMapper`）

### 5. 定时任务
- [x] 新建 `com.loktar.task.investment.ChinaEquityIndexPerfTask`：
  - 构造器注入 `EquityIndexPerfDailyMapper`、`HttpClient`、`ObjectMapper`
  - 常量：`INDEX_CODES = List.of("930955")`、`INDEX_PERF_URL`（MessageFormat 占位）
  - `@Scheduled(cron = "0 0/10 15-18 * * *")`
  - 逻辑按 `design.md` 数据流：allMatch 防重 → 单指数 exists 跳过 → GET 请求（30s 超时、UA + Accept: application/json 头）→ JsonNode 解析（code 校验、空 data 跳过）→ 映射实体 → 二次校验后 insert（手动 createTime/updateTime）
  - 单指数 try/catch + `log.error`，不中断整体；日志使用参数化占位风格对齐现有任务

### 6. 验证
- [x] `mvn compile` 通过（项目无 mvnw.cmd，使用本机 mvn）
- [x] GetProblems 检查新增/修改文件无告警
- [x] 对照 prd.md 验收标准逐项确认

### 7. 手动触发与历史初始化（追加需求）
- [x] 任务类重构：抽取 `fetchAndSave(indexCode, startDate, endDate)` 私有方法，定时与手动共用
- [x] 任务类新增 `initHistory(LocalDate, LocalDate)`，逐指数拉取区间数据幂等入库
- [x] 新建 `com.loktar.web.investment.ChinaEquityIndexPerfController`（对齐 FundNavController 模式）：
  - `POST /chinaEquityIndexPerf/testSync` 手动触发当日同步
  - `POST /chinaEquityIndexPerf/initHistory?startDate=yyyyMMdd&endDate=yyyyMMdd` 历史数据初始化
  - 受 HTTP Basic 鉴权保护（不在 SecurityConfig 白名单），无需额外配置
- [x] 编译与 GetProblems 验证通过

## 回滚点

- 步骤 3 生成结果不符合预期：删除生成文件重跑（MBG 对已存在文件会覆盖/合并，注意检查）
- 全部回滚：删除新增 4 个文件 + 还原 zxb.sql 新增段落
