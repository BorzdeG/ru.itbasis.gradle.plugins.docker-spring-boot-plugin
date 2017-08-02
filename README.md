# Spring Boot module plugin
[![Build status](https://travis-ci.org/itbasis/spring-boot-module-gradle-plugin.svg?branch=master)](https://travis-ci.org/itbasis/spring-boot-module-gradle-plugin)

## Introduction

plugin apply Spring Boot plugin (`org.springframework.boot:spring-boot-gradle-plugin`) and import as mavenBom `org.springframework.boot:spring-boot-dependencies`

If the `springBootVersion` property is not found in the project, then the `latest.release` value is used.

## Plugin append dependencies:

|scope|dependencies|
|:---:|---|
|compileOnly|org.springframework.boot:spring-boot-configuration-processor|
|testCompile|org.springframework.boot:spring-boot-starter-test|

The plugin explicitly indicates the dependency of the `compileJava` task on the `processResources`

## Using extends plugins
[itbasis:java-module-gradle-plugin](https://github.com/itbasis/java-module-gradle-plugin)

[Spring Boot Gradle plugin](https://plugins.gradle.org/plugin/org.springframework.boot)

## Examples

* Custom Spring Boot version using extra-property: 1.5.2.RELEASE ([build.gradle](src/test/resources/boot-version-ext-1.5.2.gradle)), 1.5.3.BUILD-SNAPSHOT ([build.gradle](src/test/resources/boot-version-ext-1.5.3.snapshot.gradle))
* Custom Spring Boot version using properties file: 1.5.2.RELEASE ([build.gradle](src/test/resources/default.gradle) + [gradle.properties](src/test/resources/gradle-new-1.5.2.properties)), 1.5.3.BUILD-SNAPSHOT ([build.gradle](src/test/resources/default.snapshot.gradle) + [gradle.properties](src/test/resources/gradle-new-1.5.3.snapshot.properties))