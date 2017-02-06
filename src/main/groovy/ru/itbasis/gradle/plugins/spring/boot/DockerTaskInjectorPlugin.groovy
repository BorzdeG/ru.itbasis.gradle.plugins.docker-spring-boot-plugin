package ru.itbasis.gradle.plugins.spring.boot

import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import com.bmuschko.gradle.docker.tasks.image.Dockerfile
import org.gradle.api.Plugin
import org.gradle.api.Task
import org.gradle.api.internal.project.ProjectInternal

class DockerTaskInjectorPlugin implements Plugin<ProjectInternal> {

	public static final String GROUP_DOCKER            = 'docker'
	public static final String TASK_CREATE_DOCKER_FILE = 'createDockerFile'
	public static final String TASK_BUILD_DOCKER_IMAGE = 'buildDockerImage'

	public static final String PROPERTY_DOCKER_IMAGE_GROUP = 'docker.image.group'

	@Override
	void apply(ProjectInternal project) {
		project.plugins.apply('com.bmuschko.docker-remote-api')

		createTaskDockerFile(project)
		createTaskbuildDockerImage(project)

		project.afterEvaluate {
			fixTaskCreateDockerFile(project)
			fixTaskBuildDockerImage(project)
		}
	}

	private static Task createTaskDockerFile(ProjectInternal project) {
		return project.tasks.maybeCreate(TASK_CREATE_DOCKER_FILE, Dockerfile)
	}

	static Task createTaskbuildDockerImage(ProjectInternal project) {
		final task = project.tasks.maybeCreate(TASK_BUILD_DOCKER_IMAGE, DockerBuildImage)
		task.dependsOn.add(TASK_CREATE_DOCKER_FILE)
		task.tag = (project.hasProperty(PROPERTY_DOCKER_IMAGE_GROUP) ? (project.property(PROPERTY_DOCKER_IMAGE_GROUP) + '/') : '') +
		           project.name
		return task
	}

	private static void fixTaskBuildDockerImage(ProjectInternal project) {
		final task = project.tasks.findByName(TASK_BUILD_DOCKER_IMAGE) as DockerBuildImage
		if (!task) {
			return
		}

		task.group = GROUP_DOCKER

		final taskCreateDockerFile = project.tasks.findByName(TASK_CREATE_DOCKER_FILE) as Dockerfile
		if (taskCreateDockerFile) {
			task.dependsOn.add(taskCreateDockerFile)
			task.inputDir = taskCreateDockerFile.destFile.parentFile
		}
	}

	private static void fixTaskCreateDockerFile(ProjectInternal project) {
		final task = project.tasks.findByName(TASK_CREATE_DOCKER_FILE) as Dockerfile
		if (!task) {
			return
		}

		task.group = GROUP_DOCKER
		task.destFile = new File(project.buildDir, 'docker/Dockerfile')
	}
}
