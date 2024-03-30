package dev.infochem.cmakegradleplugin;

import dev.infochem.cmakegradleplugin.util.NativePlatform;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.util.List;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class BuildLogicPluginTest {
    private final File rootDir = new File(System.getProperty("user.dir"));

    @TempDir private File testProjectDir;
    private File settingsFile;
    private File buildFile;

    @BeforeEach
    public void setup() throws IOException {
        settingsFile = new File(testProjectDir, "settings.gradle.kts");
        assert settingsFile.createNewFile();
        buildFile = new File(testProjectDir, "build.gradle.kts");
        assert buildFile.createNewFile();
    }

    @Test
    public void testExecutable() throws IOException {
        File cMakeProjectDir = new File(getClass().getResource("testexecutable").getFile());

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

        assertEquals(SUCCESS, result.task(":ConfigureCMake").getOutcome());
        assertEquals(SUCCESS, result.task(":BuildCMake").getOutcome());

        String executableBinName = NativePlatform.IS_WINDOWS ? "test_executable.exe" : "test_executable";
        String executableBinPath = "bin/" + executableBinName;

        CMakeExecutor executor = new CMakeExecutor(BuildLogicPluginTest.class);
        executor.execute(List.of(executableBinPath), testProjectDir, this::assertOutput, this::assertError);
    }

    private void writeBuildFile(File buildFile, String content) throws IOException {
        try (Writer writer = new BufferedWriter(new FileWriter(buildFile))) {
            writer.write(content);
        }
    }

    private void assertOutput(String output) {
        assertEquals("TEST EXECUTABLE", output);
    }

    private void assertError(String error) {
        assert(false);
    }
}
