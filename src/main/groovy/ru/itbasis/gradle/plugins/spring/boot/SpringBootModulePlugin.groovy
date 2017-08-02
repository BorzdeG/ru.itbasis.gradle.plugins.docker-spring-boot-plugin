package ru.itbasis.gradle.plugins.spring.boot

import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.Plugin
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.internal.project.ProjectInternal
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import ru.itbasis.gradle.plugins.java.JavaModulePlugin

class SpringBootModulePlugin implements Plugin<ProjectInternal> {
	public static final String PROPERTY_SPRING_BOOT_VERSION = 'springBootVersion'
	public static final String VERSION_LATEST_RELEASE = 'latest.release'

	@Override
	void apply(ProjectInternal project) {
		project.configure(project) {
			project.plugins.apply(JavaModulePlugin.class)

			project.plugins.apply(SpringBootPlugin.class)
			applyMavenBom(project)
			applyBaseDependencies(project)
			applyConfigurationProcessor(project)
		}
	}

	private static applyBaseDependencies(ProjectInternal project) {
		project.dependencies {
			testCompile('org.springframework.boot:spring-boot-starter-test')
		}
	}

	private static applyConfigurationProcessor(ProjectInternal project) {
		project.dependencies {
			compileOnly('org.springframework.boot:spring-boot-configuration-processor')
		}

//		project.afterEvaluate({
//			project.tasks
//			       .getByName('compileJava')
//			       .dependsOn(project.tasks.getByName('processResources'))
//		})
	}

	private static applyMavenBom(ProjectInternal project) {
		final springBootVersion = project.hasProperty(PROPERTY_SPRING_BOOT_VERSION) ?
			project.property(PROPERTY_SPRING_BOOT_VERSION) : (
			project.hasProperty('spring-boot.version') ? project.property('spring-boot.version') : VERSION_LATEST_RELEASE
		)

		final dependencyManagementExtension = project.extensions.getByType(DependencyManagementExtension.class)
		dependencyManagementExtension.imports({
			mavenBom "org.springframework.boot:spring-boot-dependencies:${springBootVersion}"
		})
	}

}
