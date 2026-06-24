# Loktar

> 基于 **SpringBoot 3 + Java 21** 的多功能自动化服务聚合项目，整合企业微信推送、爬虫、媒体处理、AI 对话、PT 下载管理、投资监控等能力，面向个人自动化场景。

---

## 目录

- [一、本项目功能介绍](#一本项目功能介绍)
- [二、项目搭建流程](#二项目搭建流程)
- [三、代码调整与版本升级要点](#三代码调整与版本升级要点)
- [四、打包发布](#四打包发布)
- [五、其他](#五其他)

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
- **A 股红利指数股息率**：[ChinaEquityIndexTask.java](src/main/java/com/loktar/task/investment/ChinaEquityIndexTask.java) 抓取指数股息率数据并播报。

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
- **Certimate 证书签发通知**：[CertimateController.java](src/main/java/com/loktar/web/certimate/CertimateController.java)。
- **Synology Webhook 转发**：[SynologyWebhookController.java](src/main/java/com/loktar/web/synology/SynologyWebhookController.java)。

### 1.10 一键搭建 Xray

通过 SSH 一键安装并配置 Xray：[VPSInitMain.java](src/main/java/com/loktar/web/test/VPSInitMain.java)。

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

部署后曾报错：`Failed to initialize platform (azure-c-shared)`。

参考 https://github.com/Azure-Samples/cognitive-services-speech-sdk/issues/2272 ，当时使用的 SDK 版本（1.34.x）仅支持 OpenSSL 1.x，因此必须选择自带 OpenSSL 1.x 的发行版作为基础镜像：

| Ubuntu LTS | 名称            |
|------------|-----------------|
| 24.04      | Noble Numbat    |
| 22.04      | Jammy Jellyfish |
| 20.04      | Focal Fossa     |
| 18.04      | Bionic Beaver   |

| Debian LTS | 名称       |
|------------|------------|
| 12         | Bookworm   |
| 11         | Bullseye   |
| 10         | Buster     |

当时将基础镜像由 `eclipse-temurin:21-jammy` 调整为 `ibm-semeru-runtimes:open-21-jre-focal`。

> **更新**：Azure Speech SDK 从 **1.38.0** 起已原生支持 OpenSSL 3.x（参考 https://github.com/Azure-Samples/cognitive-services-speech-sdk/issues/2048 ），当前项目使用的 1.50.0 同样支持。因此基础镜像不再受 OpenSSL 版本限制，可自由选择基于 Ubuntu 22.04+ / Debian 12+ 等自带 OpenSSL 3.x 的现代发行版，如 `ibm-semeru-runtimes:open-21-jre-noble`。

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
