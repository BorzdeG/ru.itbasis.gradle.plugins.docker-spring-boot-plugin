package ru.itbasis.gradle.plugins.spring.boot

import org.gradle.internal.impldep.com.google.common.io.Files
import org.gradle.testkit.runner.GradleRunner
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class DockerSpringBootPluginTest {
	@Rule
	public final TemporaryFolder testProjectDir = new TemporaryFolder()
	private GradleRunner gradleRunner

	@Before
	void setUp() throws Exception {
		gradleRunner = GradleRunner.create()
		                           .withDebug(true)
		                           .withProjectDir(testProjectDir.root)
		                           .withPluginClasspath()

		// main sources
		new File(testProjectDir.root, 'src/main/java').mkdirs()
		new File(testProjectDir.root, 'src/main/resources').mkdirs()
		// test sources
		final dirTestJava = new File(testProjectDir.root, 'src/test/java/test')
		dirTestJava.mkdirs()
		Files.copy(new File(this.class.classLoader.getResource('TestClass.java').toURI()),
		           new File(dirTestJava, 'TestClass.java'))
	}

	private void copySampleFile(String fileNameFrom, String fileNameTo) {
		Files.copy(new File(this.class.classLoader.getResource(fileNameFrom).toURI()),
		           testProjectDir.newFile(fileNameTo))
	}

	@Test
	void testBuild() {
		copySampleFile('default.gradle', 'build.gradle')
		final result = gradleRunner.withArguments('test')
		                           .build()
		Assert.assertFalse(result.output.contains('spring-boot-configuration-processor: -> 1.5.2.RELEASE'))
	}

	@Test
	void testBootVersionUsingExt_1_5_2() {
		copySampleFile('boot-version-ext-1.5.2.gradle', 'build.gradle')
		final result = gradleRunner.withArguments('dependencies')
		                           .build()
		Assert.assertTrue(result.output.contains('spring-boot-configuration-processor: -> 1.5.2.RELEASE'))
	}

	@Test
	void testBootVersionUsingExt_1_5_3_snapshot() {
		copySampleFile('boot-version-ext-1.5.3.snapshot.gradle', 'build.gradle')
		final result = gradleRunner.withArguments('dependencies')
		                           .build()
		Assert.assertTrue(result.output.contains('spring-boot-configuration-processor: -> 1.5.3.BUILD-SNAPSHOT'))
	}

	@Test
	void testBootVersionUsingExt_1_5_4() {
		copySampleFile('boot-version-ext-1.5.4.gradle', 'build.gradle')
		final result = gradleRunner.withArguments('dependencies')
		                           .build()
		Assert.assertTrue(result.output.contains('spring-boot-configuration-processor: -> 1.5.4.RELEASE'))
	}

	@Test
	void testBootVersionUsingOldProperty_1_5_2() {
		copySampleFile('default.gradle', 'build.gradle')
		copySampleFile('gradle-old-1.5.2.properties', 'gradle.properties')

		final result = gradleRunner.withArguments('dependencies')
		                           .build()

		Assert.assertTrue(result.output.contains('spring-boot-configuration-processor: -> 1.5.2.RELEASE'))
	}

	@Test
	void testBootVersionUsingProperty_1_5_2() {
		copySampleFile('default.gradle', 'build.gradle')
		copySampleFile('gradle-new-1.5.2.properties', 'gradle.properties')

		final result = gradleRunner.withArguments('dependencies')
		                           .build()

		Assert.assertTrue(result.output.contains('spring-boot-configuration-processor: -> 1.5.2.RELEASE'))
	}

	@Test
	void testBootVersionUsingProperty_1_5_3_snapshot() {
		copySampleFile('default.snapshot.gradle', 'build.gradle')
		copySampleFile('gradle-new-1.5.3.snapshot.properties', 'gradle.properties')

		final result = gradleRunner.withArguments('dependencies')
		                           .build()

		Assert.assertTrue(result.output.contains('spring-boot-configuration-processor: -> 1.5.3.BUILD-SNAPSHOT'))
	}

	@Test
	void testBootVersionUsingProperty_1_5_4() {
		copySampleFile('default.gradle', 'build.gradle')
		copySampleFile('gradle-new-1.5.4.properties', 'gradle.properties')

		final result = gradleRunner.withArguments('dependencies')
		                           .build()

		Assert.assertTrue(result.output.contains('spring-boot-configuration-processor: -> 1.5.4.RELEASE'))
	}
}