package io.github.glorrian.cmakegradleplugin;

import io.github.glorrian.cmakegradleplugin.util.NativePlatform;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CMakeExecutorTest {
    private final CMakeExecutor cMakeExecutor = new CMakeExecutor(CMakeExecutorTest.class);
    private final String ECHO_STRING = "CMakeExecutorTestTest";
    private final String CURRENT_PATH = System.getProperty("user.dir");
    private final File CURRENT_DIR = new File(CURRENT_PATH);
    private void assertEcho(String executeResult) {
        assertEquals(ECHO_STRING, executeResult);
    }
    private void assertError(String errorResult) {
        assert false;
    }

    @Test
    void testExecuting() {
        List<String> cmdLine = NativePlatform.IS_WINDOWS ? List.of("powershell.exe", "echo",  ECHO_STRING)
                : List.of("echo",  ECHO_STRING);
        cMakeExecutor.execute(cmdLine, CURRENT_DIR, this::assertEcho, this::assertError);
    }

    private void assertDirectory(String executeResult) {
        assertEquals(CURRENT_PATH, executeResult);
    }
    @Test
    void directoryTest() {
        List<String> cmdLine = NativePlatform.IS_WINDOWS ? List.of("powershell.exe", "pwd")
                : List.of("pwd");
        cMakeExecutor.execute(cmdLine, CURRENT_DIR, this::assertDirectory, this::assertError);
    }

    private void voidAssert(String executeResult) {
    }
    @Test
    void cmakeTest() {
        List<String> cmdLine = List.of(NativePlatform.getCMakeExecutable().getAbsolutePath(), "--help");
        cMakeExecutor.execute(cmdLine, CURRENT_DIR, this::voidAssert, this::assertError);
    }
}
