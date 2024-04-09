package dev.infochem.cmakegradleplugin;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractFunctionalTest {
    protected final File rootDir = new File(System.getProperty("user.dir"));

    @TempDir
    protected static File testProjectDir;
    protected static File settingsFile;
    protected static File buildFile;
    protected static File buildDir;

    @BeforeAll
    public static void setup() throws IOException {
        settingsFile = new File(testProjectDir, "settings.gradle.kts");
        assert settingsFile.createNewFile();
        buildFile = new File(testProjectDir, "build.gradle.kts");
        assert buildFile.createNewFile();
        buildDir = new File(testProjectDir, "buildDir");
        assert buildDir.mkdir();
    }

    protected void writeBuildFile(File buildFile, String content) throws IOException {
        try (Writer writer = new BufferedWriter(new FileWriter(buildFile))) {
            writer.write(content);
        }
    }

    protected void assertError(String error) {
        assert !error.isEmpty();
    }

    protected void assertCMakeResult(BuildResult result) {
        assertEquals(SUCCESS, result.task(":ConfigureCMake").getOutcome());
        assertEquals(SUCCESS, result.task(":BuildCMake").getOutcome());
    }

    protected String escapeSlashes(String path) {
        return path.replace("\\", "\\\\");
    }
}
