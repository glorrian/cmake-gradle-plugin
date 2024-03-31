package dev.infochem.cmakegradleplugin.testjnilibrary;

import dev.infochem.cmakegradleplugin.AbstractFunctionalTest;
import dev.infochem.cmakegradleplugin.jni.NativeProcessor;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import static dev.infochem.cmakegradleplugin.jni.NativeProcessor.processLibraryName;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
public class BuildJNILibraryTest extends AbstractFunctionalTest{
    @Test
    @Order(1)
    void jniBuildTest() throws IOException {
        File cMakeProjectDir = new File(getClass().getResource("jni_project").getFile());

        String settingsBuildContent = "rootProject.name = \"jni-project'\"\n" +
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
        loadLibrary();
    }

    @Test
    @Order(2)
    void testLibrary() {
        assertEquals(100*2, JNILinker.doubleInt(100));
    }

    @Test
    @Order(3)
    void testNativeProcessor() throws IOException, URISyntaxException {
        String libName = processLibraryName("jnilibrary");
        File resourcesDir = new File(BuildJNILibraryTest.class.getResource("").toURI());
        File testLibrary = new File(resourcesDir, libName);
        try {
            assert Arrays.asList(testProjectDir.list()).contains(libName);
            File library = new File(testProjectDir, libName);
            Files.copy(library.toPath(), testLibrary.toPath(), StandardCopyOption.REPLACE_EXISTING);
            assert testLibrary.exists();
            NativeProcessor.loadLibraryFromResources(BuildJNILibraryTest.class, "jnilibrary");
        } finally {
            testLibrary.deleteOnExit();
        }
    }

    void loadLibrary() {
        System.load(testProjectDir.getAbsolutePath()+"/"+processLibraryName("jnilibrary"));
    }

}

class JNILinker {
    public static native int doubleInt(int number);
}
