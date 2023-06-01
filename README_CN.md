<h4 align="right">English | <a href="./README_CN.md">
<strong>简体中文</strong></a></h4>

<p align="center">
    <img src=./assets/quick.png width=138 alt="quick"/>
</p>

<h1 align="center">QuickWeb</h1>
<p align="center"><strong>构建Web应用模板，整合Knife4j接口文档，自定义错误码、全局异常处理器、全局日志记录（AOP实现）、全局异常处理器、通用返回类、Logback日志记录</strong></p>

<p align="center">
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-3.8.3-blue.svg" alt="Maven Version"></a>
  <a href="https://www.oracle.com/java/technologies/javase-jdk11-downloads.html"><img src="https://img.shields.io/badge/JDK-11-orange.svg" alt="JDK Version"></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-2.7.x-green.svg" alt="Spring Boot Version"></a>
  <a href="https://doc.xiaominfo.com/"><img alt="Knife4j" src="https://raster.shields.io/badge/Knife4j-3.0+-orange.svg"/></a>  
  <a href="https://codejuzi.icu/"><img src="https://img.shields.io/badge/Blog-codejuzi.icu-yellowgreen.svg" alt="My Blog"></a>
</p>


> 这是一个基于SpringBoot的Web起始项目，你可以将这个项目作为项目初始的模板项目。
>
> 本项目使用了MyBatis和MyBatis Plus作为ORM框架，使用Druid作为数据源，MySQL作为数据库，同时也使用了Knife4j作为API文档生成工具。
> 除此之外，该项目还实现了自定义业务异常、全局日志记录（AOP实现）、全局异常处理器、通用返回类、Logback日志记录等功能。

<h2 align='center'>📌技术栈</h2>

- Java 11
- Spring Boot 2.7.11：快速开发框架，简化了Spring应用的搭建过程。
- MyBatis & MyBatis Plus：持久层框架，提供了许多方便的增删改查接口。
- Druid：阿里巴巴开源的数据库连接池、监控组件。
- MySQL：开源关系型数据库。
- Knife4j：基于Swagger的API文档生成工具。

<h2 align='center'>💪功能</h2>

- 自定义业务异常：在业务出错时抛出自定义异常，并通过全局异常处理器进行捕获和处理。
- 全局日志记录（AOP实现）：通过AOP拦截所有Controller方法，在方法执行前后记录请求和响应的相关信息。
- 全局异常处理器：捕获全局异常并统一处理，返回友好的错误提示信息。
- 通用返回类：封装了一些常见的响应状态码和消息，方便快速构建响应体。
- Knife4j API文档生成：通过注解和配置生成API文档，方便开发者查看和调试接口。

<h2 align='center'>🏁快速开始</h2>

1. 安装MySQL并创建数据库。

2. 修改`application-dev.yml`中的数据库连接信息。

3. 运行主类`MainApplication`。

4. 访问`http://localhost:8080/api/doc.html`查看API文档。

5. 整合到自己的项目中

    1. 修改`logback.xml`中的项目名称为自己的

    2. 修改数据库配置

    3. 修改knife4j配置（详见[🔧配置项说明](#🔧配置项说明)）

    4. 根据需要修改端口号和`context-path`

<h2 align='center'>📖配置文件说明</h2>

- `application.yml`：应用配置文件，包括数据库连接信息、日志配置等。
- `application-dev.yml`：开发应用配置文件，包括数据库连接信息、日志配置等。
- `application-prod.yml`：上线应用配置文件，包括数据库连接信息、日志配置等。
- `logback.xml`：logback日志配置文件，包括日志文件记录参数配置等

<h2 align='center'>🔧配置项说明</h2>

完整配置项：

```yaml
knife4j:
  config:
    # 核心配置
    base-package: com.juzi.quickweb.controller
    # 次要配置
    title: QuickWeb接口文档
    description: 帮助快速构建web
    version: 0.0.1
    contact-name: codejuzi
    contact-email: d1741530592@163.com
    contact-url: codejuzi.icu
    license: MIT
    license-url: https://mit-license.org/
```

<h2 align='center'>🧾目录结构说明</h2>

```
.
├── Dockerfile
├── README.md
├── logs
│   └── quick-web.log   
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── juzi
    │   │           └── quickweb
    │   │               ├── MainApplication.java
    │   │               ├── aspect
    │   │               │   └── LogAspect.java              # controller层日志记录AOP
    │   │               ├── common
    │   │               │   ├── BaseResponse.java           # 通用返回类
    │   │               │   └── StatusCode.java             # 自定义状态码
    │   │               ├── configuration
    │   │               │   ├── CorsConfig.java             # 全局跨域配置
    │   │               │   ├── DataSourceConfig.java       # 数据源配置
    │   │               │   ├── Knife4jConfig.java
    │   │               │   └── MyBatisPlusConfig.java
    │   │               ├── controller
    │   │               ├── exception
    │   │               │   ├── BusinessException.java      # 自定义业务异常
    │   │               │   └── GlobalExceptionHandler.java # 全局异常处理器
    │   │               └── util
    │   │                   ├── ResultUtils.java
    │   │                   └── ThrowUtils.java
    │   └── resources
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       ├── application.yml
    │       └── logback.xml
    └── test
        └── java
            └── com
                └── juzi
                    └── quickweb
                        └── MainApplicationTest.java
```

<h2 align='center'>©️版权信息</h2>

遵循MIT开源协议。
