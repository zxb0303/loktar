# SpringBoot3+Java21 学习记录
## 1.项目搭建流程
### 1.1 idea通过spring initialize创建新工程
仅添加
* spring-boot-starter
* spring-boot-starter-web
* spring-boot-starter-test
### 1.2.修改配置文件格式
* application.properties修改为application.yml、 application-dev.yml、 application-pro.yml
* 参考[application.yml](src%2Fmain%2Fresources%2Fapplication.yml)
* 参考[application-test.yml](src%2Fmain%2Fresources%2Fapplication-test.yml)
### 1.3.创建目录结构
* src
  * com.loktar
    * conf 项目配置、常量类
    * domain 实体
    * dto 数据传输对象
    * learn 学习内容
    * mapper  dao接口
    * service 业务逻辑
    * task 定时器
    * util 工具类
    * web web服务
### 1.4 生成domain、mapper
* 1.4.1 添加依赖
```xml
<dependency>
  <groupId>org.mybatis.spring.boot</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
  <version>3.0.3</version>
</dependency>
```
```xml
<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <version>8.3.0</version>
</dependency>
```
```xml
<dependency>
  <groupId>org.mybatis.generator</groupId>
  <artifactId>mybatis-generator-core</artifactId>
  <version>1.4.2</version>
</dependency>
```
* 1.4.2 在项目的 resources 目录下新建mybatis-generator-config.xml和mybatis-generator.properties
```properties
#mybatis-generator.properties示例
datasource.url=jdbc:mysql://localhost：3306/database?&useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=Asia/Shanghai
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
* 1.4.3 在pom.xml的build部分添加plugins
```xml
<plugin>
    <groupId>org.mybatis.generator</groupId>
    <artifactId>mybatis-generator-maven-plugin</artifactId>
    <version>1.4.2</version>

    <configuration>
        <!--  引入 MyBatis-Generator 的配置文件 -->
        <configurationFile>./src/main/resources/mybatis-generator-config.xml</configurationFile>
        <!--  允许 MBG 将构建消息写入日志中  -->
        <verbose>true</verbose>
        <!--  再次运行 MBG 时，允许覆盖已生成的文件，但是不会覆盖 xml 文件  -->
        <overwrite>true</overwrite>
    </configuration>

    <dependencies>
        <!--  引入 mysql 的 JDBC 驱动，否则会报错找不到驱动  -->
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>8.3.0</version>
        </dependency>
    </dependencies>
</plugin>
```
* 1.4.4 执行maven->mybatis-generator生成
* 1.4.5 在application.yml添加mybatis配置驼峰转大小写以及mapper位置
* 1.4.6 Mapper上不添加单独每个Mapper添加@Mapper注解，直接创建config配置类</br>
参考[MybatisConfig.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Fconf%2FMybatisConfig.java)
* 1.4.7 使用@MapperScan时，在注入mapper的时候，idea会提示Could not autowire. No beans of ‘EmployeeMapper’ type found.需要在 Intellij Idea中设置一下：Settings - Editor - Inspections - Spring - Spring Core - Code – Incorrect autowiring in spring bean components
### 1.5 创建controller测试maven全流程，springboot启动
* 1.5.1 mvn test不通过<br />
在LoktarApplicationTests.java上添加注解@ActiveProfiles("dev")
* 1.5.2 mvn package 没有将mapper下的xml打包<br />
在pom.xml的build下添加资源配置
```xml
<resources>
    <resource>
        <directory>src/main/resources</directory>
        <includes>
            <include>**/*.yml</include>
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
**注：只加xml的话，yml文件在mav package时不会被打包**
* 1.5.3 idea通过springboot启动时不会打包xml
  * 方式一：先mvn compile 再启动
  * 方式二：修改启动命令，添加先mvn compile再编译
* 1.5.4 idea配置启动时指定配置文件
* 1.5.5 lombok继承类警告
  * 方式一：添加@EqualsAndHashCode(callSuper=true)
  * 方式二：添加lombok.config、并在pom.xml下添加插件
## 2.代码调整
### 2.1 @Deprecated调整
* 2.1.1 @autowired->使用构造器方式注入
```java
public class GithubController {
    private final GithubService githubService;
    private final QywxApi QywxApi;
    private final LokTarConfig lokTarConfig;
    public GithubController(GithubService githubService, com.loktar.util.wx.qywx.QywxApi qywxApi, LokTarConfig lokTarConfig) {
        this.githubService = githubService;
        this.QywxApi = qywxApi;
        this.lokTarConfig = lokTarConfig;
    }
}
```
* 2.1.2 org.springframework.util.StringUtils->org.apache.commons.lang3.StringUtils
### 2.2 com.alibaba.fastjson->com.fasterxml.jackson
参考[JacksonTest.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Flearn%2Fjackson%2FJacksonTest.java)
### 2.3 修改redis序列化
参考[RedisConfig.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Fconf%2FRedisConfig.java)
### 2.4 org.apache.http.impl.client、org.springframework.web.client->java.net.http
参考[Http.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Flearn%2Fjdk11%2FHttp.java)
<br/><br/>
**注：文件上传使用httpmime构建对象**
```xml
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpmime</artifactId>
    <version>4.5.14</version>
</dependency>
```
参考[QywxApi.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Futil%2Fwx%2Fqywx%2FQywxApi.java)
### 2.5 xml解析使用jackson-dataformat-xml
```xml
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.15.4</version>
</dependency>
```
参考[QyWeixinCallbackController.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Fweb%2Fqywx%2FQyWeixinCallbackController.java)
### 2.6 CompletableFuture.runAsync无法抛出异常
需要加trycatch
参考[QyWeixinCallbackController.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Fweb%2Fqywx%2FQyWeixinCallbackController.java)
### 2.7 服务需要使用到ffmpeg 方案调整
原方案：
* 在dockerfile中安装ffmpeg
* 而由于直接安装的ffmpeg没有amr格式处理能力故改用www.deb-multimedia.org的版本
* 又因为打包过程访问不便，改用阿里云仓库镜像
```dockerfile
FROM openjdk:8-jre
#用阿里云仓库镜像
RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list
#RUN apt-get update && apt-get install -y ffmpeg *直接装的ffmpeg没有amr格式处理能力 改用www.deb-multimedia.org的 访问不便 也用仓库镜像
RUN apt-get update && apt-get install -y wget
RUN echo deb http://mirrors.ustc.edu.cn/deb-multimedia/ bullseye main non-free >>/etc/apt/sources.list
RUN echo deb-src http://mirrors.ustc.edu.cn/deb-multimedia/ bullseye main non-free >>/etc/apt/sources.list
RUN wget https://mirrors.ustc.edu.cn/deb-multimedia/pool/main/d/deb-multimedia-keyring/deb-multimedia-keyring_2016.8.1_all.deb
RUN dpkg -i deb-multimedia-keyring_2016.8.1_all.deb
RUN apt-get update && apt-get install -y ffmpeg
ARG JAR_FILE
ADD $JAR_FILE /app.jar
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Shanghai", "/app.jar"]
```
新方案：
* 不在镜像中安装ffmpeg,选择单独部署并通过DockerEngineApi调用服务
* 部署tecnativa/docker-socket-proxy并配置好正确权限
* 部署jrottenberg/ffmpeg
* 编写[DockerEngineApiUtil.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Futil%2FDockerEngineApiUtil.java)实现DockerEngineApi的exec能力
* 调整业务代码
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
### 2.8 docker部署后提示Runtime error: Failed to initialize platform (azure-c-shared). Error
https://github.com/Azure-Samples/cognitive-services-speech-sdk/issues/2272 </br>
微软语音 SDK 需要 Open SSL 1.x 才能运行。目前支持的linux版本需要是Ubuntu 18.04/20.04 或者 Debian 10/11

| Ubuntu LTS 版本 | 名称              |
|---------------|-----------------|
| 24.04         | Noble Numbat    |
| 22.04         | Jammy Jellyfish |
| 20.04         | Focal Fossa     |
| 18.04         | Bionic Beaver   |

| Debian LTS 版本 | 名称       |
|---------------|----------|
| 12            | Bookworm |
| 11            | Bullseye |
| 10            | Buster   |

因个人习惯用ubuntu，基础镜像由eclipse-temurin:21-jammy调整为ibm-semeru-runtimes:open-21-jre-focal
## 3.打包发布
### 3.1 添加Dockerfile文件
```dockerfile
FROM ibm-semeru-runtimes:open-21-jre-focal
ARG JAR_EXPOSE=8080
EXPOSE $JAR_EXPOSE
ARG JAR_FILE
ADD $JAR_FILE /app.jar
ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Shanghai", "/app.jar"]
```
### 3.2 使用dockerfile-maven-plugin构建并推送镜像
* 3.2.1 idea及windows本地配置docker信息
* 3.2.2 pom.xml的build中添加plugin
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
**注：敏感信息写在/user/.m2/settings.xml**
### 3.3 使用github action构建并推送镜像
参考[action.yml](.github%2Fworkflows%2Faction.yml)
## 4.本项目功能介绍
### 4.1 Github关注的项目版本更新后推送企业微信消息
[GithubTask.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Ftask%2Fgithub%2FGithubTask.java)
### 4.2 Jellyfin用户播放后推送企业微信消息，并自动对Transmission进行限速及取消限速操作
[JellyfinWebhookController.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Fweb%2Fjellyfin%2FJellyfinWebhookController.java)
### 4.3 浙江省土地拍卖记录爬虫
[LandTask.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Ftask%2Fland%2FLandTask.java)
### 4.4 杭州市摇号数据爬虫
[LotteryTask.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Ftask%2Flottery%2FLotteryTask.java)
### 4.5 杭州市新房一房一价数据爬虫
[NewHouseV3Controller.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Fweb%2Fnewhouse%2FNewHouseV3Controller.java)
### 4.6 企业微信接入ChatGPT进行对话，使用Azure语音服务进行文本与语音互换
[QyWeixinCallbackChatGPTController.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Fweb%2Fqywx%2FQyWeixinCallbackChatGPTController.java)
### 4.7 企业微信消息接收，实现信息查询，例如Transmission下载列表查询，主动开启及关闭限速等操作
[QyWeixinCallbackController.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Fweb%2Fqywx%2FQyWeixinCallbackController.java)
### 4.8 杭州市二手房数据爬虫
[SecondController.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Fweb%2Fsecond%2FSecondController.java)
### 4.9 基于企业微信消息实现定时通知
[CommonTask.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Ftask%2Fcommon%2FCommonTask.java)
### 4.10 PT网站RSS订阅及自动推送Transmission
[RssTask.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Ftask%2Ftransmission%2FRssTask.java)
### 4.11 Transmission自动删除错误种子，自动根据硬盘大小删除保种数据
[TransmissionTask.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Ftask%2Ftransmission%2FTransmissionTask.java)
### 4.12 一键搭建xray
[VPSInitMain.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Fweb%2Ftest%2FVPSInitMain.java)
## 5.其他
### 5.1 Github单独删除某个文件的所有历史记录
https://blog.csdn.net/q258523454/article/details/83899911</br>
* git filter-branch --force --index-filter 'git rm --cached --ignore-unmatch src/main/resources/config/application-test.yml' --prune-empty --tag-name-filter cat -- --all
* git push origin --force --all
* git push origin --force --tags



