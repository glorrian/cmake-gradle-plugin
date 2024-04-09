package dev.glorrian.cmakegradleplugin.testjnilibrary;

import dev.glorrian.cmakegradleplugin.AbstractFunctionalTest;
import dev.glorrian.cmakegradleplugin.util.BuildType;
import dev.glorrian.cmakegradleplugin.util.NativePlatform;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
public class BuildJNILibraryTest extends AbstractFunctionalTest{
    @Test
    @Order(1)
    void jniBuildTest() throws IOException {
        File cMakeProjectDir = new File(getClass().getResource("jni_project").getFile());

        String settingsBuildContent = "rootProject.name = \"jni-project'\"\n" +
                "includeBuild(\"" +  rootDir.getAbsolutePath() + "\")";

        writeBuildFile(settingsFile, escapeSlashes(settingsBuildContent));
        String buildContent = "plugins {\n" +
                "id(\"dev.glorrian.cmake-gradle-plugin\")\n" +
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
        loadLibrary();
    }

    @Test
    @Order(2)
    void testLibrary() {
        assertEquals(100*2, JNILinker.doubleInt(100));
    }

    void loadLibrary() {
        String libName = System.mapLibraryName("jnilibrary");
        String libPath;
        if (NativePlatform.IS_WINDOWS) {
            libPath = buildDir.getAbsolutePath() + "\\" + BuildType.DEBUG + "\\" + libName;
        } else {
            libPath = buildDir.getAbsolutePath() + "/" + libName;
        }
        System.load(libPath);
    }

}

class JNILinker {
    public static native int doubleInt(int number);
}
