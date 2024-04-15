package dev.glorrian.cmakegradleplugin.testexecutable;

import dev.glorrian.cmakegradleplugin.AbstractFunctionalTest;
import dev.glorrian.cmakegradleplugin.CMakeExecutor;
import dev.glorrian.cmakegradleplugin.util.BuildType;
import dev.glorrian.cmakegradleplugin.util.NativePlatform;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class BuildExecutableTest extends AbstractFunctionalTest {
    @Test
    void testExecutable() throws IOException {
        File cMakeProjectDir = new File(getClass().getResource("test_project").getFile());

        String settingsBuildContent = "rootProject.name = \"hello-world'\"\n" +
                "includeBuild(\"" +  rootDir.getAbsolutePath() + "\")";
        writeBuildFile(settingsFile, escapeSlashes(settingsBuildContent));
        String buildContent = "plugins {\n" +
                "id(\"io.github.glorrian.cmake-gradle-plugin\")\n" +
                "}\n" +
                "cmake {\n" +
                "val srcDir = project.objects.directoryProperty()\n" +
                "srcDir.set(File(\"" + cMakeProjectDir.getAbsolutePath() + "\"))\n" +
                "sourceDirectory = srcDir\n" +
                "val buildDir = project.objects.directoryProperty()\n" +
                "buildDir.set(File(\"" + buildDir.getAbsolutePath() + "\"))\n" +
                "buildDirectory = buildDir\n" +
                "}";
        writeBuildFile(buildFile, escapeSlashes(buildContent));

        BuildResult result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withArguments("build")
                .withPluginClasspath()
                .build();
        assertCMakeResult(result);

        String executableBinName = NativePlatform.IS_WINDOWS ? "test_executable.exe" : "test_executable";
        String executableBinPath = "bin/" + executableBinName;
        String winExecutableBinPath = "bin/" + BuildType.DEBUG + "/" + executableBinName;

        CMakeExecutor executor = new CMakeExecutor(BuildExecutableTest.class);
        if (new File(buildDir, executableBinPath).exists()) {
            executor.execute(List.of(executableBinPath), buildDir, this::assertOutput, this::assertError);
        } else if (new File(buildDir, winExecutableBinPath).exists()) {
            executor.execute(List.of("powershell.exe", winExecutableBinPath), buildDir, this::assertOutput, this::assertError);
        } else {
            throw new FileNotFoundException("The executable file was not found after the build.");
        }
    }

    void assertOutput(String output) {
        System.out.println(output);
        assertEquals("TEST EXECUTABLE", output);
    }
}
