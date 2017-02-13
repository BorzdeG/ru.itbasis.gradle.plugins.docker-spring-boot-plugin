package ru.itbasis.gradle.plugins.spring.boot

import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.springframework.boot.gradle.plugin.SpringBootPlugin
import ru.itbasis.gradle.plugins.java.JavaModulePlugin

class DockerSpringBootPlugin implements Plugin<ProjectInternal> {
	public static final String TASK_COPY_APP_FROM_DOCKER = 'copyAppFromDocker'

	public static final String PROPERTY_DOCKER_IMAGE_JAVA = 'docker.image.java'

	@Override
	void apply(ProjectInternal project) {
		project.configure(project) {
			project.plugins.apply(JavaModulePlugin.class)

			project.plugins.apply(SpringBootPlugin.class)
			applyMavenBom(project)
			applyBaseDependencies(project)
			applyConfigurationProcessor(project)

			project.plugins.apply(DockerTaskInjectorPlugin.class)
			applyDocker(project)
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
		project.afterEvaluate({
			project.tasks
			       .getByName('compileJava')
			       .dependsOn(project.tasks
			                         .getByName('processResources'))
		})
	}

	private static applyMavenBom(ProjectInternal project) {
		final springBomDependencies = project.hasProperty('spring-boot.bom-dependencies') ? project.property('spring-boot.bom-dependencies') :
		                              'spring-boot-dependencies'
		final springBootVersion = project.hasProperty('spring-boot.version') ? project.property('spring-boot.version') :
		                          JavaModulePlugin.VERSION_LATEST_RELEASE

		final dependencyManagementExtension = project.extensions.getByType(DependencyManagementExtension.class)
		dependencyManagementExtension.imports({
			mavenBom "org.springframework.boot:${springBomDependencies}:${springBootVersion}"
		})
	}

	private static applyDocker(ProjectInternal project) {
		final copyAppFromDocker = createTaskCopyAppFromDocker(project)

		applyCreateTaskDockerFile(project).dependsOn.add(copyAppFromDocker)
	}

	private static Task createTaskCopyAppFromDocker(ProjectInternal project) {
		final task = project.tasks.maybeCreate(TASK_COPY_APP_FROM_DOCKER)
		task.group = DockerTaskInjectorPlugin.GROUP_DOCKER
		project.tasks.findByName(LifecycleBasePlugin.ASSEMBLE_TASK_NAME).each { Task taskAssemble ->
			task.dependsOn(taskAssemble)
		}
		task.doLast {
			project.copy {
				from new File(project.buildDir, 'libs')
				into new File(project.buildDir, 'docker/apps')
			}
		}
		return task
	}

	private static Task applyCreateTaskDockerFile(ProjectInternal project) {
		final Dockerfile task = project.tasks.getByName(DockerTaskInjectorPlugin.TASK_CREATE_DOCKER_FILE)

		task.from((
			          project.hasProperty(PROPERTY_DOCKER_IMAGE_JAVA)
				          ? project.property(PROPERTY_DOCKER_IMAGE_JAVA)
				          : ('anapsix/alpine-java:' + JavaVersion.toVersion((project.tasks
				                                                                    .getByPath(JavaPlugin.COMPILE_JAVA_TASK_NAME) as JavaCompile)
					                                                            .sourceCompatibility)
				                                                 .majorVersion)
		          ) as String)
		project.logger.info('using docker base image: {}',
		                    (task.instructions.find { e -> e instanceof Dockerfile.FromInstruction } as Dockerfile.FromInstruction).command)

		task.exposePort(8080)
		task.copyFile('./apps', '/opt/apps/')
		task.entryPoint('java'
		                , '-Djava.security.egd=file:/dev/./urandom'
		                , '-jar'
		                , '/opt/apps/' + project.tasks.withType(Jar).archiveName[0])
		return task
	}
}
