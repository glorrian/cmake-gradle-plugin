package dev.infochem.cmakegradleplugin;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CMakeExecutorTest {
    private final CMakeExecutor cMakeExecutor = new CMakeExecutor(CMakeExecutorTest.class);
    private final String ECHO_STRING = "CMakeExecutorTestTest";
    private void assertEcho(String executeResult) {
        assertEquals(ECHO_STRING, executeResult);
    }
    private void assertError(String errorResult) {
        assert false;
    }

    @Test
    void testExecuting() {
        List<String> cmdLine = List.of("echo", ECHO_STRING);
        cMakeExecutor.execute(cmdLine, new File(System.getProperty("user.dir")), this::assertEcho, this::assertError);
    }
}
