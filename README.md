# SpringBoot3+Java21 个人学习
## 1.项目搭建流程
### 1.1 idea通过spring initialize创建新工程
* spring-boot-starter:
  * 这是Spring Boot的核心起步依赖，包含了自动配置支持、日志库以及对使用Spring核心功能（如依赖注入）所需的基本组件。它是构建任何Spring Boot应用的基础，不特定于任何特定的应用类型。
  如果你的项目是一个简单的Spring应用或者不需要Web功能，使用这个起步依赖就足够了。
* spring-boot-starter-web:
  * 这个起步依赖包含了spring-boot-starter的所有内容，同时还添加了开发Web应用所需的依赖，如Spring MVC、Tomcat（作为默认的嵌入式servlet容器）、Jackson（用于JSON处理）等。
  使用这个起步依赖，你可以非常方便地开发RESTful应用或者传统的基于servlet的Web应用。它是构建Web层的基础，提供了大量开箱即用的特性来帮助你快速开发Web接口。
* spring-boot-starter-test:
  * Spring Boot 用于测试的起步依赖，它包含了开发者进行单元测试和集成测试时常用的一系列库。通过将这个依赖添加到你的项目中，你可以轻松地编写和运行测试，利用Spring Boot提供的测试支持功能。这个起步依赖专为测试环境设计，因此它的作用域被设置为 test，这意味着它只在测试编译和执行阶段可用，不会被包含进生产环境的构建中。
### 1.2.修改配置文件格式
* application.properties修改为application.yml， application-dev.yml， application-pro.yml
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
```angular2html
<dependency>
  <groupId>org.mybatis.spring.boot</groupId>
  <artifactId>mybatis-spring-boot-starter</artifactId>
  <version>3.0.3</version>
</dependency>

<dependency>
  <groupId>com.mysql</groupId>
  <artifactId>mysql-connector-j</artifactId>
  <version>8.3.0</version>
</dependency>

<dependency>
  <groupId>org.mybatis.generator</groupId>
  <artifactId>mybatis-generator-core</artifactId>
  <version>1.4.2</version>
</dependency>
```
* 1.4.2 在项目的 resources 目录下新建mybatis-generator-config.xml和mybatis-generator.properties
```angular2html
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
```angular2html
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
* 1.4.6 Mapper上不添加@Mapper注解，在Springboot启动类上面添加@MapperScan("com.loktar.mapper”)
* 1.4.7 使用@MapperScan时，在注入mapper的时候，idea会提示Could not autowire. No beans of ‘EmployeeMapper’ type found.需要在 Intellij Idea中设置一下：Settings - Editor - Inspections - Spring - Spring Core - Code – Incorrect autowiring in spring bean components
### 1.5 创建controller测试maven全流程，springboot启动
* 1.5.1 mvn test不通过<br />
在LoktarApplicationTests.java上添加注解@ActiveProfiles("dev")
* 1.5.2 mvn package 没有将mapper下的xml打包<br />
在pom.xml的build下添加资源配置
```angular2html
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
# 2.代码调整
## 2.1 @Deprecated调整
* 2.1.1 @autowired<br/>
改为使用构造器方式注入
* 2.1.2 StringUtils.isEmpty()<br/>
改为org.apache.commons.lang3.StringUtils
## 2.2 com.alibaba.fastjson->com.fasterxml.jackson
参考[JacksonTest.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Flearn%2Fjackson%2FJacksonTest.java)
## 2.3 org.apache.http.impl.client、org.springframework.web.client->java.net.http
参考[Http.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Flearn%2Fjdk11%2FHttp.java)
<br/>
**注：文件上传使用httpmime构建对象**
```angular2html
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpmime</artifactId>
    <version>4.5.14</version>
</dependency>
```
参考[QywxApi.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Futil%2Fwx%2Fqywx%2FQywxApi.java)

## 2.4 xml解析使用jackson-dataformat-xml
```angular2html
<dependency>
    <groupId>com.fasterxml.jackson.dataformat</groupId>
    <artifactId>jackson-dataformat-xml</artifactId>
    <version>2.15.4</version>
</dependency>
```
参考[QyWeixinCallbackController.java](src%2Fmain%2Fjava%2Fcom%2Floktar%2Fweb%2Fqywx%2FQyWeixinCallbackController.java)




