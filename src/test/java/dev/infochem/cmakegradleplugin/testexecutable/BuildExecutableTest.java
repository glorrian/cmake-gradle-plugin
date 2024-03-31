package dev.infochem.cmakegradleplugin.testexecutable;

import dev.infochem.cmakegradleplugin.AbstractFunctionalTest;
import dev.infochem.cmakegradleplugin.CMakeExecutor;
import dev.infochem.cmakegradleplugin.util.NativePlatform;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class BuildExecutableTest extends AbstractFunctionalTest {
    @Test
    void testExecutable() throws IOException {
        File cMakeProjectDir = new File(getClass().getResource("test_project").getFile());

        String settingsBuildContent = "rootProject.name = \"hello-world'\"\n" +
                "includeBuild(\"" +  rootDir.getAbsolutePath() + "\")";
        writeBuildFile(settingsFile, settingsBuildContent);
        String buildContent = "plugins {\n" +
                "id(\"dev.infochem.cmake-gradle-plugin\")\n" +
                "}\n" +
                "cmake {\n" +
                "val srcDir = project.objects.directoryProperty()\n" +
                "srcDir.set(File(\"" + cMakeProjectDir.getAbsolutePath() + "\"))\n" +
                "sourceDirectory = srcDir\n" +
                "val buildDir = project.objects.directoryProperty()\n" +
                "buildDir.set(File(\"" + testProjectDir.getAbsolutePath() + "\"))\n" +
                "buildDirectory = buildDir\n" +
                "}";
        writeBuildFile(buildFile, buildContent);

        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments("build")
                .withPluginClasspath()
                .build();
        assertCMakeResult(result);

        String executableBinName = NativePlatform.IS_WINDOWS ? "test_executable.exe" : "test_executable";
        String executableBinPath = "bin/" + executableBinName;

        CMakeExecutor executor = new CMakeExecutor(BuildExecutableTest.class);
        executor.execute(List.of(executableBinPath), testProjectDir, this::assertOutput, this::assertError);
    }

    void assertOutput(String output) {
        assertEquals("TEST EXECUTABLE", output);
    }
}
