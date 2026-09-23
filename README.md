# Loktar

> 基于 **SpringBoot 3 + Java 21** 的多功能自动化服务聚合项目，整合企业微信推送、爬虫、媒体处理、AI 对话、PT 下载管理、投资监控等能力，面向个人自动化场景。

---

## 目录

- [一、本项目功能介绍](#一本项目功能介绍)
  - [1.1 企业微信中枢](#11-企业微信中枢)
  - [1.2 GitHub 项目更新推送](#12-github-项目更新推送)
  - [1.3 Jellyfin + Transmission 联动](#13-jellyfin--transmission-联动)
  - [1.4 Transmission 自动化运维](#14-transmission-自动化运维)
  - [1.5 房产/土地数据爬虫](#15-房产土地数据爬虫)
  - [1.6 投资监控](#16-投资监控)
  - [1.7 专利业务自动化](#17-专利业务自动化)
  - [1.8 AI 与多媒体能力](#18-ai-与多媒体能力)
  - [1.9 其他自动化通知](#19-其他自动化通知)
  - [1.10 一键搭建 Xray](#110-一键搭建-xray)
  - [1.11 Audiobookshelf 收听监控](#111-audiobookshelf-收听监控)
- [二、项目搭建流程](#二项目搭建流程)
  - [2.1 通过 IDEA Spring Initialize 创建新工程](#21-通过-idea-spring-initialize-创建新工程)
  - [2.2 修改配置文件格式](#22-修改配置文件格式)
  - [2.3 创建包结构](#23-创建包结构)
  - [2.4 生成 domain、mapper](#24-生成-domainmapper)
    - [2.4.1 添加依赖](#241-添加依赖)
    - [2.4.2 创建 mybatis-generator 配置](#242-创建-mybatis-generator-配置)
    - [2.4.3 在 pom.xml 的 build 中添加 plugin](#243-在-pomxml-的-build-中添加-plugin)
    - [2.4.4 在 mybatis-generator-config.xml 中启用 Lombok 插件](#244-在-mybatis-generator-configxml-中启用-lombok-插件)
    - [2.4.5 执行生成](#245-执行生成)
    - [2.4.6 在 application.yml 中配置 mybatis](#246-在-applicationyml-中配置-mybatis)
    - [2.4.7 统一开启 Mapper 扫描](#247-统一开启-mapper-扫描)
    - [2.4.8 IDEA 自动注入告警处理](#248-idea-自动注入告警处理)
  - [2.5 创建 Controller 测试 Maven 全流程](#25-创建-controller-测试-maven-全流程)
- [三、代码调整与版本升级要点](#三代码调整与版本升级要点)
  - [3.1 @Deprecated 调整](#31-deprecated-调整)
    - [3.1.1 字段注入改为构造器注入](#311-字段注入改为构造器注入)
    - [3.1.2 工具类替换](#312-工具类替换)
  - [3.2 JSON 库切换](#32-json-库切换)
  - [3.3 修改 Redis 序列化](#33-修改-redis-序列化)
  - [3.4 HTTP 客户端切换](#34-http-客户端切换)
    - [3.4.1 Audiobookshelf 内网 HTTP 响应头 EOF](#341-audiobookshelf-内网-http-响应头-eof)
  - [3.5 XML 解析使用 jackson-dataformat-xml](#35-xml-解析使用-jackson-dataformat-xml)
  - [3.6 CompletableFuture.runAsync 异常处理](#36-completablefuturerunasync-异常处理)
  - [3.7 FFmpeg 调用方案调整](#37-ffmpeg-调用方案调整)
  - [3.8 Azure 语音 SDK 在 Docker 中运行的基础镜像选型](#38-azure-语音-sdk-在-docker-中运行的基础镜像选型)
    - [当前已验证的组合](#当前已验证的组合)
    - [历史问题与 OpenSSL 支持演进](#历史问题与-openssl-支持演进)
    - [更换镜像时的检查项](#更换镜像时的检查项)
- [四、打包发布](#四打包发布)
  - [4.1 添加 Dockerfile](#41-添加-dockerfile)
  - [4.2 使用 dockerfile-maven-plugin 构建并推送镜像](#42-使用-dockerfile-maven-plugin-构建并推送镜像)
  - [4.3 使用 GitHub Action 构建并推送镜像](#43-使用-github-action-构建并推送镜像)
- [五、其他](#五其他)
  - [5.1 GitHub 单独删除某个文件的所有历史记录](#51-github-单独删除某个文件的所有历史记录)

---

## 一、本项目功能介绍

### 1.1 企业微信中枢

整个项目以企业微信作为通知与交互入口，统一封装在 [QywxApi.java](src/main/java/com/loktar/util/wx/qywx/QywxApi.java)，支持文本、文件、媒体上传等。

- **消息回调与命令路由**：[QyWeixinCallbackController.java](src/main/java/com/loktar/web/qywx/QyWeixinCallbackController.java)，可通过聊天指令查询 Transmission 下载列表、开启/关闭限速等。
- **ChatGPT 对话 + Azure 语音**：[QyWeixinCallbackChatGPTController.java](src/main/java/com/loktar/web/qywx/QyWeixinCallbackChatGPTController.java)，企业微信接入 LangChain4j（OpenAI 兼容协议）实现对话，结合 Azure Speech 完成文本↔语音互转。
- **专利相关回调**：[QyWeixinCallbackPatentController.java](src/main/java/com/loktar/web/qywx/QyWeixinCallbackPatentController.java)。
- **定时通知**：[CommonTask.java](src/main/java/com/loktar/task/common/CommonTask.java) 基于企业微信发送日程/提醒类定时消息。

### 1.2 GitHub 项目更新推送

监控关注的开源项目，新版本发布时推送到企业微信，已基于 `org.kohsuke:github-api` SDK 重写：[GithubTask.java](src/main/java/com/loktar/task/github/GithubTask.java)。

### 1.3 Jellyfin + Transmission 联动

Jellyfin 用户播放时通过 Webhook 通知企微，同时对 Transmission 自动开启/取消全局限速，避免播放卡顿：[JellyfinWebhookController.java](src/main/java/com/loktar/web/jellyfin/JellyfinWebhookController.java)。

### 1.4 Transmission 自动化运维

- **PT 站点 RSS 自动追剧**：[RssTask.java](src/main/java/com/loktar/task/transmission/RssTask.java)，定时拉取 RSS 并将种子推送至 Transmission。
- **种子自动清理**：[TransmissionTask.java](src/main/java/com/loktar/task/transmission/TransmissionTask.java)，自动剔除报错种子，并根据磁盘剩余空间智能清理保种数据。
- **手动操作入口**：[TransmissionController.java](src/main/java/com/loktar/web/transmission/TransmissionController.java)。

### 1.5 房产/土地数据爬虫

- **浙江省土地拍卖记录**：[LandTask.java](src/main/java/com/loktar/task/land/LandTask.java)。
- **杭州市摇号数据**：[LotteryTask.java](src/main/java/com/loktar/task/lottery/LotteryTask.java)。
- **杭州市新房一房一价**：[NewHouseV3Controller.java](src/main/java/com/loktar/web/newhouse/NewHouseV3Controller.java)。
- **杭州市二手房**：[SecondController.java](src/main/java/com/loktar/web/second/SecondController.java)。

### 1.6 投资监控

- **基金净值同步**：[FundNavTask.java](src/main/java/com/loktar/task/investment/FundNavTask.java) 定时拉取东方财富基金净值，结合持仓配置计算资产总额、盈亏比例并推送企微。
- **中证紅利指数股息率**：[ChinaEquityIndexTask.java](src/main/java/com/loktar/task/investment/ChinaEquityIndexTask.java) 抓取指数股息率数据并播报。
- **中证指数每日行情**：[ChinaEquityIndexPerfTask.java](src/main/java/com/loktar/task/investment/ChinaEquityIndexPerfTask.java) 定时同步中证指数官网行情数据（开高低收、涨跌幅、成交量、市盈率等），支持多指数配置、幂等入库；[ChinaEquityIndexPerfController.java](src/main/java/com/loktar/web/investment/ChinaEquityIndexPerfController.java) 提供手动触发当日同步与按日期区间初始化历史数据接口。

### 1.7 专利业务自动化

围绕专利申请的全流程自动化：监控状态变化、生成报价/合同 PDF、上传企微文件、发送短信通知。核心入口：

- 监控任务：[PatentTask.java](src/main/java/com/loktar/task/patent/PatentTask.java)。
- 业务接口：[PatentController.java](src/main/java/com/loktar/web/patent/PatentController.java)、[PatentPdfController.java](src/main/java/com/loktar/web/patent/PatentPdfController.java) 等。
- PDF 工具：[PatentPdfUtil.java](src/main/java/com/loktar/util/PatentPdfUtil.java)。

### 1.8 AI 与多媒体能力

- **ChatGPT 调用**：[ChatGPTController.java](src/main/java/com/loktar/web/openai/ChatGPTController.java)、[ChatGPTUtil.java](src/main/java/com/loktar/util/ChatGPTUtil.java)（基于 LangChain4j 1.x 的 `chat()` 接口）。
- **Azure 文档智能 + 语音合成/识别**：[AzureController.java](src/main/java/com/loktar/web/azure/AzureController.java)、[AzureVoiceUtil.java](src/main/java/com/loktar/util/AzureVoiceUtil.java)、[AzureDocIntelligenceUtil.java](src/main/java/com/loktar/util/AzureDocIntelligenceUtil.java)。
- **FFmpeg 调用**：[FfmpegController.java](src/main/java/com/loktar/web/ffmpeg/FfmpegController.java) 通过 Docker Engine API 调用单独部署的 ffmpeg 容器实现 wav↔amr 转换。

### 1.9 其他自动化通知

- **IP 变化通知**：[IpTask.java](src/main/java/com/loktar/task/ip/IpTask.java)。
- **Volvo 车机系统版本监控**：[CarTask.java](src/main/java/com/loktar/task/car/CarTask.java)。
- **Relx 库存监控**：[RelxTask.java](src/main/java/com/loktar/task/relx/RelxTask.java)。
- **Minecraft 基岩版版本监控与自动升级**：[MinecraftTask.java](src/main/java/com/loktar/task/minecraft/MinecraftTask.java)。
- **Certimate 证书签发通知**：[CertimateController.java](src/main/java/com/loktar/web/certimate/CertimateController.java)。
- **Synology Webhook 转发**：[SynologyWebhookController.java](src/main/java/com/loktar/web/synology/SynologyWebhookController.java)。

### 1.10 一键搭建 Xray

通过 SSH 一键安装并配置 Xray：[VPSInitMain.java](src/main/java/com/loktar/web/test/VPSInitMain.java)。

### 1.11 Audiobookshelf 收听监控

- **收听提醒**：[AudioBookShelfTask.java](src/main/java/com/loktar/task/audiobookshelf/AudioBookShelfTask.java) 定时监控单个用户，根据播放进度变化推送收听内容，跨当日时长档位时附加提醒。
- **用户管理**：通过企业微信菜单启用/禁用监控用户，每日 08:00 自动恢复可用状态。
- **手动测试入口**：[AudioBookShelfController.java](src/main/java/com/loktar/web/audiobookshelf/AudioBookShelfController.java)，支持触发监控、恢复用户状态及切换启用状态。

---

## 二、项目搭建流程

### 2.1 通过 IDEA Spring Initialize 创建新工程

仅添加最小依赖集：

- `spring-boot-starter`
- `spring-boot-starter-web`
- `spring-boot-starter-test`

### 2.2 修改配置文件格式

将 `application.properties` 拆分为按 profile 区分的 yml：

- [application.yml](src/main/resources/application.yml)
- `application-dev.yml`
- `application-test.yml`

### 2.3 创建包结构

```
com.loktar
├── conf      项目配置、常量类
├── domain    数据库实体
├── dto       数据传输对象
├── learn     不同 JDK 版本特性 / 第三方库学习示例
├── listener  Redis Key 过期监听等
├── mapper    MyBatis DAO 接口及 XML
├── service   业务逻辑
├── task      Spring 定时任务（@Scheduled）
├── util      通用工具类（HTTP、PDF、FFmpeg、Azure、企微等）
└── web       Controller / Webhook 入口
```

### 2.4 生成 domain、mapper

#### 2.4.1 添加依赖

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.5</version>
</dependency>
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>9.6.0</version>
</dependency>
<dependency>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-core</artifactId>
    <version>2.0.0</version>
</dependency>
<dependency>
    <groupId>com.softwareloop</groupId>
    <artifactId>mybatis-generator-lombok-plugin</artifactId>
    <version>1.0</version>
</dependency>
```

#### 2.4.2 创建 mybatis-generator 配置

在 `resources` 目录下创建 `mybatis-generator-config.xml` 和 `mybatis-generator.properties`。配置示例：

```properties
datasource.url=jdbc:mysql://localhost:3306/database?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai
datasource.driverClassName=com.mysql.cj.jdbc.Driver
datasource.username=root
datasource.password=root
source.targetProject=src/main/java
domain.package=com.loktar.domain.transmission
dao.package=com.loktar.mapper.transmission
xmlMapper.package=com.loktar.mapper.transmission.xml
table.schema=scheme
table.tableName=tr_torrent_tracker
```

#### 2.4.3 在 pom.xml 的 build 中添加 plugin

```xml
<plugin>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-maven-plugin</artifactId>
    <version>2.0.0</version>
    <configuration>
        <configurationFile>./src/main/resources/mybatis-generator-config.xml</configurationFile>
        <verbose>true</verbose>
        <overwrite>true</overwrite>
    </configuration>
    <dependencies>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>9.6.0</version>
        </dependency>
        <dependency>
            <groupId>com.softwareloop</groupId>
            <artifactId>mybatis-generator-lombok-plugin</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>
</plugin>
```

#### 2.4.4 在 mybatis-generator-config.xml 中启用 Lombok 插件

在 `<context>` 内添加插件声明，生成的 domain 类将自动带 `@Data` 注解，不再生成 getter/setter：

```xml
<plugin type="com.softwareloop.mybatis.generator.plugins.LombokPlugin" />
```

> **注**：2.0.0 起 JSR-310 类型（`LocalDate`、`LocalDateTime`）为默认行为，无需再配置 `useJSR310Types`；`<javaModelGenerator>` 已更名为 `<modelGenerator>`，`<javaClientGenerator>` 更名为 `<clientGenerator>`（旧名仍可用但会产生警告）。

#### 2.4.5 执行生成

执行 `maven -> mybatis-generator` 即可生成带 `@Data` 注解的 domain、mapper 接口与 XML。

#### 2.4.6 在 application.yml 中配置 mybatis

开启驼峰转下划线、指定 mapper XML 位置。

#### 2.4.7 统一开启 Mapper 扫描

不在每个 Mapper 上加 `@Mapper`，改为创建统一的配置类，参考 [MybatisConfig.java](src/main/java/com/loktar/conf/MybatisConfig.java)。

#### 2.4.8 IDEA 自动注入告警处理

使用 `@MapperScan` 后，注入时 IDEA 可能提示 `Could not autowire`，可在
`Settings - Editor - Inspections - Spring - Spring Core - Code - Incorrect autowiring in spring bean components` 中调整。

### 2.5 创建 Controller 测试 Maven 全流程

- **2.5.1** `mvn test` 不通过：在 `LoktarApplicationTests.java` 上添加 `@ActiveProfiles("dev")`。
- **2.5.2** `mvn package` 未将 mapper 下的 XML 打包，需在 build 中添加资源配置：

```xml
<resources>
    <resource>
        <directory>src/main/resources</directory>
        <includes>
            <include>**/*.yml</include>
            <include>**/*.txt</include>
        </includes>
        <filtering>true</filtering>
    </resource>
    <resource>
        <directory>src/main/java</directory>
        <includes>
            <include>**/*.xml</include>
        </includes>
        <filtering>false</filtering>
    </resource>
</resources>
```

> **注**：只配置 xml 时，yml 在 `mvn package` 时不会被打包，必须同时声明。

- **2.5.3** IDEA 通过 Spring Boot 启动时不会打包 xml：
  - 方式一：先 `mvn compile` 再启动；
  - 方式二：修改启动命令，添加 `compile` 前置步骤。
- **2.5.4** IDEA 配置启动时指定 profile。
- **2.5.5** Lombok 继承类警告：
  - 方式一：添加 `@EqualsAndHashCode(callSuper = true)`；
  - 方式二：添加 `lombok.config` 并在 pom.xml 中配置插件。

---

## 三、代码调整与版本升级要点

> **背景说明**：作者长期使用 JDK 8 进行开发，本项目升级到 JDK 21。在改造过程中尽可能优先选用 JDK 自带能力以及 Spring Boot 已经引入的依赖，减少对第三方库的引入。例如：
>
> - JSON 处理直接使用 Spring Boot 自带的 `jackson`，不再额外引入 `fastjson`；
> - HTTP 调用使用 JDK 11+ 自带的 `java.net.http.HttpClient`，替换 `Apache HttpClient` 与 `RestTemplate`；
> - XML 解析使用 `jackson-dataformat-xml`，与 JSON 体系保持一致；
> - 异步编程使用 JDK 自带的 `CompletableFuture`。
>
> 以下要点按主题列出本次升级中遇到的典型问题与改造方案。

### 3.1 @Deprecated 调整

#### 3.1.1 字段注入改为构造器注入

```java
@RestController
public class GithubController {
    private final GithubService githubService;
    private final QywxApi qywxApi;
    private final LokTarConfig lokTarConfig;

    public GithubController(GithubService githubService, QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.githubService = githubService;
        this.qywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }
}
```

#### 3.1.2 工具类替换

`org.springframework.util.StringUtils` -> `org.apache.commons.lang3.StringUtils`

### 3.2 JSON 库切换

`com.alibaba.fastjson` -> `com.fasterxml.jackson`，参考 [JacksonTest.java](src/main/java/com/loktar/learn/jackson/JacksonTest.java)。

### 3.3 修改 Redis 序列化

参考 [RedisConfig.java](src/main/java/com/loktar/conf/RedisConfig.java)。

### 3.4 HTTP 客户端切换

`org.apache.http.impl.client`、`org.springframework.web.client.RestTemplate` -> `java.net.http.HttpClient`（JDK 11+），参考 [Http.java](src/main/java/com/loktar/learn/jdk11/Http.java)。

参考 [QywxApi.java](src/main/java/com/loktar/util/wx/qywx/QywxApi.java)。

#### 3.4.1 Audiobookshelf 内网 HTTP 响应头 EOF

**现象**：ABS 收听监控通过内网 HTTP 查询用户时持续失败，异常出现在 `AudioBookShelfUtil.get()` 的 `HttpClient.send()`，还未进入播放进度判断：

```text
java.io.IOException: HTTP/1.1 header parser received no bytes
Caused by: java.io.EOFException: EOF reached while reading
```

同一内网入口使用普通 curl 请求能够返回 HTTP 响应，而 Java 改用 HTTPS 域名入口后可以正常调用。不能仅凭这段异常认定 HTTP 不支持、配置未生效或连接池复用了失效连接。

**定位方法与证据**：在本机 IBM Semeru 21.0.9 上，对同一内网地址的 `/api/users` 发送不带 Token 的 GET 请求，每次使用新客户端，只改变请求的 HTTP 版本偏好：

| 请求方式 | 实测结果 |
|----------|----------|
| 指定 `HTTP_1_1` | 返回 `401 Unauthorized`，正常收到 HTTP 响应 |
| 指定 `HTTP_2` | 出现相同的响应头 EOF |
| 不指定版本（JDK 默认偏好 HTTP/2） | 出现相同的响应头 EOF |

`401` 是未带凭据时的认证响应，证明已收到响应头，并不代表认证成功。新客户端也能复现，说明旧连接复用不是本次故障的必要条件。

可在能够访问 ABS 内网的 JDK 21 `jshell` 中执行以下对照（先替换 `ABS_HOST` 和端口；不需要实际 Token）：

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

class AbsHttpProbe {
    public static void main(String[] args) {
        URI uri = URI.create("http://ABS_HOST:6085/api/users");
        for (HttpClient.Version version : HttpClient.Version.values()) {
            try (HttpClient client = HttpClient.newHttpClient()) {
                HttpRequest request = HttpRequest.newBuilder(uri)
                        .version(version)
                        .timeout(Duration.ofSeconds(8))
                        .GET()
                        .build();
                HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
                System.out.println(version + " -> " + response.statusCode());
            } catch (Exception e) {
                System.out.println(version + " -> " + e);
            }
        }
    }
}
```

粘贴上述定义后，在 `jshell` 中执行 `AbsHttpProbe.main(new String[0]);` 查看结果。

**原因**：默认版本请求的实际日志中出现了以下升级头：

```http
Connection: Upgrade, HTTP2-Settings
Upgrade: h2c
```

JDK 在明文 HTTP 请求中尝试通过 `h2c` 升级到 HTTP/2；本次 ABS 内网入口在收到这类请求时未返回响应头就断开连接。升级从 HTTP/1.1 请求开始，因此异常中的 `HTTP/1.1` 并不意味着没有尝试 HTTP/2 升级。普通 curl 的 HTTP/1.1 请求没有这些升级头；HTTPS 入口使用 TLS ALPN 协商协议，且经过不同的服务入口，不能据其成功反推内网 HTTP 不可用。相同兼容性现象可参考 [OpenJDK JDK-8326420](https://bugs.openjdk.org/browse/JDK-8326420)。

**修复**：在 [AudioBookShelfUtil.java](src/main/java/com/loktar/util/AudioBookShelfUtil.java) 的 `get()` 和 `updateUserActive()` 两处请求构造中，局部指定 HTTP/1.1，并附中文说明：

```java
HttpRequest.Builder builder = HttpRequest.newBuilder()
        // ABS 内网入口无法兼容 JDK 默认的 h2c 升级，会在返回响应头前断连（EOF）。
        // 在请求级指定 HTTP/1.1，避免影响共享 HttpClient 的其他调用方。
        .version(HttpClient.Version.HTTP_1_1);
```

上例展示请求构造器的关键设置；项目中仍在原有链式调用中继续设置 URI、超时、认证头以及 GET/PATCH 方法。

- 保留内网 URL、共享 HttpClient 与现有业务逻辑，不修改全局协议偏好，不需要修改 Dockerfile 或设置 keep-alive JVM 参数。
- 本次修改已通过 Maven 编译，并经实际测试确认内网调用恢复。部署后可使用 [手动测试入口](#111-audiobookshelf-收听监控) 验证完整调用；状态切换接口会修改真实 ABS 用户状态。
- 若其他环境出现相同 EOF，应重新进行同环境、同地址、同路径的对照，不应把所有 EOF 都归因于 `h2c`。排查请求头时仅对不带凭据的探测请求启用详细日志，避免泄露 `Authorization`、Cookie 等信息。

### 3.5 XML 解析使用 jackson-dataformat-xml

```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.15.4</version>
</dependency>
```

参考 [QyWeixinCallbackController.java](src/main/java/com/loktar/web/qywx/QyWeixinCallbackController.java)。

### 3.6 CompletableFuture.runAsync 异常处理

`CompletableFuture.runAsync` 默认无法向外抛出异常，需要在内部加 try-catch 显式处理，参考 [QyWeixinCallbackController.java](src/main/java/com/loktar/web/qywx/QyWeixinCallbackController.java)。

### 3.7 FFmpeg 调用方案调整

**原方案**：在 Dockerfile 中安装 ffmpeg；由于官方 ffmpeg 不带 amr 编解码，改用 `www.deb-multimedia.org` 版本，并切换到镜像源加速：

```dockerfile
FROM openjdk:8-jre
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list
RUN apt-get update && apt-get install -y wget
RUN echo deb http://mirrors.ustc.edu.cn/deb-multimedia/ bullseye main non-free >>/etc/apt/sources.list
RUN echo deb-src http://mirrors.ustc.edu.cn/deb-multimedia/ bullseye main non-free >>/etc/apt/sources.list
RUN wget https://mirrors.ustc.edu.cn/deb-multimedia/pool/main/d/deb-multimedia-keyring/deb-multimedia-keyring_2016.8.1_all.deb
RUN dpkg -i deb-multimedia-keyring_2016.8.1_all.deb
RUN apt-get update && apt-get install -y ffmpeg
```

**新方案**：将 ffmpeg 单独部署为容器，通过 Docker Engine API 远程执行：

- 部署 `tecnativa/docker-socket-proxy` 暴露受限权限的 Docker socket；
- 部署 `jrottenberg/ffmpeg` 镜像；
- 编写 [DockerEngineApiUtil.java](src/main/java/com/loktar/util/DockerEngineApiUtil.java) 调用 `exec` 接口；
- 调整业务代码。

```yaml
version: '3'
services:
  jrottenberg-ffmpeg:
    restart: always
    image: jrottenberg/ffmpeg:4.1-ubuntu
    container_name: jrottenberg-ffmpeg
    entrypoint: /bin/sh -c "tail -f /dev/null"
    volumes:
      - /voicepath:/voicepath
    environment:
      TZ: Asia/Shanghai
    network_mode: "bridge"
```

```yaml
version: '3'
services:
  docker-socket-proxy:
    restart: always
    privileged: true
    image: tecnativa/docker-socket-proxy:0.1
    container_name: docker-socket-proxy
    ports:
      - "2375:2375"
    volumes:
      - /var/run/docker.sock:/var/run/docker.sock
    environment:
      CONTAINERS: 1
      EXEC: 1
      POST: 1
    network_mode: "bridge"
```

### 3.8 Azure 语音 SDK 在 Docker 中运行的基础镜像选型

#### 当前已验证的组合

当前项目使用 Azure Speech SDK **1.51.2**（见 [pom.xml](pom.xml)），搭配 [Dockerfile](Dockerfile) 中的基础镜像，已通过项目实际运行测试：

```dockerfile
FROM ibm-semeru-runtimes:open-21.0.12.10-jre-resolute
```

当前 Dockerfile 未额外添加系统依赖安装步骤。此处记录的是本项目已验证的 SDK 与镜像组合，不代表该发行版已获得 Azure 官方支持，也不代表任意 JRE 镜像都能直接运行。

#### 历史问题与 OpenSSL 支持演进

旧版 SDK（1.34.x）依赖 OpenSSL 1.x，项目部署时曾出现 `Failed to initialize platform (azure-c-shared)`。当时通过将基础镜像从 `eclipse-temurin:21-jammy` 换为 `ibm-semeru-runtimes:open-21-jre-focal` 解决，参考 [Issue #2272](https://github.com/Azure-Samples/cognitive-services-speech-sdk/issues/2272)。这一历史限制不应继续用于判断新版 SDK 的镜像选型。

- **1.38.0**：开始支持 OpenSSL 3，但初期仅支持 **3.0.x**，见 [Issue #2048 维护者说明](https://github.com/Azure-Samples/cognitive-services-speech-sdk/issues/2048#issuecomment-2161849523)。
- **1.40.0**：移除了仅限 OpenSSL 3.0.x 的内部限制，并修复 Linux arm64 上的 OpenSSL 3 检测问题，见 [Issue #2436 维护者说明](https://github.com/Azure-Samples/cognitive-services-speech-sdk/issues/2436#issuecomment-2292049452)。
- **当前官方要求**：Java/Linux SDK 支持 OpenSSL **1.x 或 3.x**，但没有提供逐个 OpenSSL 3.x 次版本的完整验证表，不能据此推断所有发行版和镜像组合均已验证。

#### 更换镜像时的检查项

Speech SDK 在 Linux 上动态使用容器内的 OpenSSL，镜像选型除 JDK 版本外，还需检查 CPU 架构、glibc、OpenSSL 共享库、CA 证书（`ca-certificates`）及 ALSA 等系统依赖。支持 OpenSSL 3.x 不等于镜像依赖齐全；遇到平台初始化或 TLS/WebSocket 连接失败时，应结合 SDK 日志确认原因，而不是直接归因为 OpenSSL 3 不兼容。

参考：[Java/Linux 平台要求](https://learn.microsoft.com/en-us/azure/ai-services/speech-service/quickstarts/setup-platform?tabs=linux&pivots=programming-language-java)、[Linux OpenSSL 与证书配置](https://learn.microsoft.com/en-us/azure/ai-services/speech-service/how-to-configure-openssl-linux)。

---

## 四、打包发布

### 4.1 添加 Dockerfile

```dockerfile
FROM ibm-semeru-runtimes:open-21-jre-focal
ARG JAR_EXPOSE=8080
EXPOSE $JAR_EXPOSE
ARG JAR_FILE
ADD $JAR_FILE /app.jar
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Shanghai", "/app.jar"]
```

### 4.2 使用 dockerfile-maven-plugin 构建并推送镜像

- 在 IDEA 与 Windows 本地配置好 Docker 客户端；
- 在 `pom.xml` 的 build 中添加 plugin：

```xml
<plugin>
    <groupId>com.spotify</groupId>
    <artifactId>dockerfile-maven-plugin</artifactId>
    <version>1.4.13</version>
    <configuration>
        <username>${my.username}</username>
        <password>${my.password}</password>
        <repository>${my.repository}/${project.artifactId}</repository>
        <tag>${project.version}</tag>
        <buildArgs>
            <JAR_FILE>target/${project.build.finalName}.jar</JAR_FILE>
        </buildArgs>
    </configuration>
</plugin>
```

> **注**：仓库凭证等敏感信息放在 `~/.m2/settings.xml` 中，禁止入库。

### 4.3 使用 GitHub Action 构建并推送镜像

参考 [action.yml](.github/workflows/action.yml)。

---

## 五、其他

### 5.1 GitHub 单独删除某个文件的所有历史记录

```bash
git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch src/main/resources/config/application-test.yml' --prune-empty --tag-name-filter cat -- --all
git push origin --force --all
git push origin --force --tags
```

**命令说明**：

- `git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch src/main/resources/config/application-test.yml' --prune-empty --tag-name-filter cat -- --all`
  - `git filter-branch`：重写 Git 历史记录的底层命令。
  - `--force`：在备份目录已存在时强制覆盖，避免重复执行报错。
  - `--index-filter '...'`：对每一个历史提交的索引执行指定脚本，不用 checkout 到工作区，速度较快。
  - `git rm --cached --ignore-unmatch src/main/resources/config/application-test.yml`：从索引中删除目标文件；`--cached` 仅删除索引不动工作区文件，`--ignore-unmatch` 避免某些提交中不包含该文件时报错。
  - `--prune-empty`：删除过滤后变成空的提交，保持历史整洁。
  - `--tag-name-filter cat`：对所有 tag 同步重写，`cat` 表示 tag 名保持不变。
  - `-- --all`：对所有分支与引用生效（不仅仅是当前分支）。
- `git push origin --force --all`：将重写后的**所有本地分支**强制推送到远程 `origin`，覆盖远程历史。
- `git push origin --force --tags`：将重写后的**所有 tag** 强制推送到远程，使远程 tag 与本地保持一致。

> **注意**：该操作会重写历史并覆盖远程，不可逆。执行前请确保本地仓库已备份，并提醒所有协作者重新 clone 或执行 rebase。
