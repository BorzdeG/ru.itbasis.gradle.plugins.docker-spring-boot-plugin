# Docker and Spring Boot module plugin
[![Build status](https://travis-ci.org/BorzdeG/ru.itbasis.gradle.plugins.docker-spring-boot-plugin.svg?branch=master)](https://travis-ci.org/BorzdeG/ru.itbasis.gradle.plugins.docker-spring-boot-plugin)

## Introduction

plugin apply Spring Boot plugin (`org.springframework.boot:spring-boot-gradle-plugin:latest.release`) and import as mavenBom `org.springframework.boot:spring-boot-dependencies`

Plugin append dependencies:

|scope|dependencies|
|:---:|---|
|compileOnly|org.springframework.boot:spring-boot-configuration-processor|
|testCompile|org.springframework.boot:spring-boot-starter-test|

The plugin explicitly indicates the dependency of the `compileJava` task on the `processResources`

## Using extends plugins
[ru.itbasis.gradle.plugins:java-plugin](https://github.com/BorzdeG/ru.itbasis.gradle.plugins.java-plugin)

[Spring Boot Gradle plugin](https://plugins.gradle.org/plugin/org.springframework.boot)

## Examples

* Custom Spring Boot version using extra-property: 1.5.2.RELEASE ([build.gradle](src/test/resources/boot-version-ext-1.5.2.gradle)), 1.5.3.BUILD-SNAPSHOT ([build.gradle](src/test/resources/boot-version-ext-1.5.3.snapshot.gradle))
* Custom Spring Boot version using properties file: 1.5.2.RELEASE ([build.gradle](src/test/resources/default.gradle) + [gradle.properties](src/test/resources/gradle-new-1.5.2.properties)), 1.5.3.BUILD-SNAPSHOT ([build.gradle](src/test/resources/default.snapshot.gradle) + [gradle.properties](src/test/resources/gradle-new-1.5.3.snapshot.properties))