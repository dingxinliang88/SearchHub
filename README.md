<h4 align="right"><strong>English</strong> | <a href="./README_CN.md">
简体中文</a></h4>

<p align="center">
    <img src=./assets/quick.png width=138  alt="quick"/>
</p>

<h1 align="center">QuickWeb</h1>
<p align="center"><strong>Build web application template, integrate Knife4j API documentation, customize error codes, global exception handlers, global logging (implemented using AOP), common response class and Logback logging</strong></p>

<p align="center">
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-3.8.3-blue.svg" alt="Maven Version"></a>
  <a href="https://www.oracle.com/java/technologies/javase-jdk11-downloads.html"><img src="https://img.shields.io/badge/JDK-11-orange.svg" alt="JDK Version"></a>
  <a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-2.7.x-green.svg" alt="Spring Boot Version"></a>
  <a href="https://doc.xiaominfo.com/"><img alt="Knife4j" src="https://raster.shields.io/badge/Knife4j-3.0+-orange.svg"/></a>  
  <a href="https://codejuzi.icu/"><img src="https://img.shields.io/badge/Blog-codejuzi.icu-yellowgreen.svg" alt="My Blog"></a>
</p>


> QuickWeb is a web starting project based on SpringBoot that can be used as a template for new projects. It uses
> MyBatis and MyBatis Plus as the ORM framework, Druid as the data source, MySQL as the database, and Knife4j as the API
> document generator. Additionally, this project includes features such as custom business exceptions, global logging (
> implemented using AOP), global exception handling, common response class, and Logback logging.

<h2 align='center'>📌Technology Stack</h2>

- java 11
- Spring Boot 2.7.11: A fast development framework that simplifies the process of setting up a Spring application.
- MyBatis & MyBatis Plus: A persistence framework that provides many convenient interfaces for CRUD operations.
- Druid: Alibaba's open-source database connection pool and monitoring component.
- MySQL: Open-source relational database.
- Knife4j: An API document generation tool based on Swagger.

<h2 align='center'>💪Features</h2>

- Custom business exceptions: throw custom exceptions when business errors occur and catch and handle them through
  global exception handlers.
- Global logging (implemented using AOP): Intercept all Controller methods through AOP and record relevant information
  about requests and responses before and after method execution.
- Global exception handling: Capture global exceptions and handle them uniformly, returning friendly error messages.
- Common response class: encapsulates some common response status codes and messages for quick construction of response
  bodies.
- Knife4j API documentation generation: Generates API documentation through annotations and configuration, making it
  easy for developers to view and debug interfaces.

<h2 align='center'>🏁Quick Start</h2>

1. Install MySQL and create a database.

2. Modify the database connection information in `application-dev.yml`.

3. Run the main class `MainApplication`.

4. Visit `http://localhost:8080/api/doc.html` to view the API documentation.

5. Integrate into your own project

    1. Modify the project name in `logback.xml` to your own

    2. Modify the database configuration

    3. Modify knife4j configuration (see [🔧Configuration Item Description](#🔧configuration-item-description))

    4. Modify the port number and `context-path` as needed

<h2 align='center'>📖Configuration File Description</h2>

- `application.yml`: Application configuration file, including database connection information, log configuration, etc.
- `application-dev.yml`: Development application configuration file, including database connection information, log
  configuration, etc.
- `application-prod.yml`: Online application configuration file, including database connection information, log
  configuration, etc.
- `logback.xml`: Logback configuration file, including parameters for logging file records, etc.

<h2 align='center'>🔧Configuration Item Description</h2>

Complete configuration items:

```yaml
knife4j:
  config:
    # Core configuration
    base-package: com.juzi.quickweb.controller
    # Secondary configuration
    title: QuickWeb API Document
    description: Helps to quickly build web applications
    version: 0.0.1
    contact-name: codejuzi
    contact-email: d1741530592@163.com
    contact-url: codejuzi.icu
    license: MIT
    license-url: https://mit-license.org/
```

<h2 align='center'>🧾Directory Structure Description</h2>

```
.
├── Dockerfile
├── README.md
├── logs
│   └── myapp.log   
├── pom.xml
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── juzi
    │   │           └── quickweb
    │   │               ├── MainApplication.java
    │   │               ├── aspect
    │   │               │   └── LogAspect.java              # Controller layer logging AOP
    │   │               ├── common
    │   │               │   ├── BaseResponse.java           # Common response class
    │   │               │   └── StatusCode.java             # Custom status codes
    │   │               ├── configuration
    │   │               │   ├── CorsConfig.java             # Global cross-domain configuration
    │   │               │   ├── DataSourceConfig.java       # Data source configuration
    │   │               │   ├── Knife4jConfig.java
    │   │               │   └── MyBatisPlusConfig.java
    │   │               ├── controller
    │   │               ├── exception
    │   │               │   ├── BusinessException.java      # Custom business exceptions
    │   │               │   └── GlobalExceptionHandler.java # Global exception handler
    │   │               └── util
    │   │                   ├── ResultUtils.java
    │   │                   └── ThrowUtils.java
    │   └── resources
    │       ├── application-dev.yml
    │       ├── application-prod.yml
    │       ├── application.yml
    │       └── logback.xml
    └── test
        └── java
            └── com
                └── juzi
                    └── quickweb
                        └── MainApplicationTest.java
```

<h2 align='center'>©️Copyright</h2>

Open-sourced under the MIT License. 
