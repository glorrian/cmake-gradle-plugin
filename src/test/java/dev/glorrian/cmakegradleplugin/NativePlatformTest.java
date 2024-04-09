package dev.glorrian.cmakegradleplugin;

import dev.glorrian.cmakegradleplugin.util.NativePlatform;
import org.junit.jupiter.api.Test;

import java.io.File;

public class NativePlatformTest {
    @Test
    void definePlatformTest() {
        boolean isOnlyPlatform = (NativePlatform.IS_LINUX && !NativePlatform.IS_MACOS && !NativePlatform.IS_WINDOWS)
                || (!NativePlatform.IS_LINUX && NativePlatform.IS_MACOS && !NativePlatform.IS_WINDOWS)
                || (!NativePlatform.IS_LINUX && !NativePlatform.IS_MACOS && NativePlatform.IS_WINDOWS);
        assert isOnlyPlatform;
    }

    @Test
    void findCMakeExecutableTest() {
        File cMakeExecutable = NativePlatform.getCMakeExecutable();
        assert cMakeExecutable.exists();
        assert cMakeExecutable.isFile();
        assert cMakeExecutable.canExecute();
        assert cMakeExecutable.isAbsolute();
    }
}
