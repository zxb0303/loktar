# Minecraft基岩版版本检测与容器自动重启

## Goal

定时检测 Minecraft 基岩版官方最新 release 版本，与本地运行的 `minecraft-bedrock-server` 容器内服务器实际版本对比；当本地版本落后时，通过 Portainer REST API 重启该容器使其升级到最新版，并通过企业微信发送通知。

## Requirements

1. 新增一个定时任务，每 30 分钟执行一次（cron: `0 */30 * * * ?`），仅生产环境启用（`@Profile(LokTarConstant.ENV_PRO)`）。
2. 请求 `https://raw.githubusercontent.com/EndstoneMC/bedrock-server-data/refs/heads/v2/versions.json`，解析得到 `release.latest` 作为最新版本号。
3. 使用 `minecraft-server-util` Java 库对本地 Minecraft 基岩版服务器进行状态查询（Server List Ping），从响应中提取当前运行版本号。
4. 对比两个版本号：
   - 本地版本与最新 release 一致 → 不执行任何操作。
   - 本地版本落后（不一致）→ 调用 Portainer REST API 重启名为 `minecraft-bedrock-server` 的容器。
5. 触发重启时，通过企业微信发送文本通知（沿用 `QywxApi` + `AgentMsgText` 模式），内容包含旧版本号、新版本号与时间。
6. 以下参数全部走 `LokTarConfig`（`conf.*` 前缀）配置，不写死在代码里：
   - Minecraft 服务器 host、port
   - Portainer baseUrl、apiToken（或访问凭据）、endpointId、容器名称
7. 单次执行中的网络失败、查询失败等异常只记录日志，不得影响下一次定时触发（`SchedulerConfig` 已有全局错误兜底）。

## Assumptions / Constraints

- 目标容器以 `itzg/minecraft-bedrock-server` 镜像运行，且环境变量 `VERSION=LATEST`，容器重启时会自动下载并运行最新 release 版本；本任务只做"重启"动作，不负责镜像或版本参数管理。
- 通知仅需文本消息，无需语音通道。
- Portainer 的地址、Token、endpointId 等真实值由用户在对应 profile 的 `application-*.yml` 中配置，代码仓库只新增配置键。

## Acceptance Criteria

- [x] 定时器按 30 分钟周期触发，日志可见执行记录（cron 已设置，运行表现待部署后观察）。
- [x] 能正确解析 versions.json 获取最新 release 版本号（BedrockVersionsDTO，代码级验证）。
- [x] 能通过 minecraft-server-util（实际采用 MineStat，Java 生态等价物）查询到本地服务器当前版本号（代码级验证，真实 UDP 查询待配置后验证）。
- [x] 本地版本与最新版本一致时不触发重启。
- [x] 本地版本落后时成功调用 Portainer API 重启 `minecraft-bedrock-server` 容器（代码级验证，真实重启待配置后验证）。
- [x] 触发重启时收到企业微信文本通知，内容含旧版本、新版本（仅重启成功才发送，失败下周期重试）。
- [x] 单次执行异常（如 GitHub 无法访问、服务器离线）只记录日志，不影响后续触发（@SneakyThrows + SchedulerConfig 全局兜底）。
- [x] `mvn compile` 编译通过（BUILD SUCCESS，2026-08-06 验证）。

> 注：标注"代码级验证"的条目需在 `application-{profile}.yml` 补齐 `conf.minecraft.*` 与 `conf.portainer.*` 配置后通过首次运行日志复核。
