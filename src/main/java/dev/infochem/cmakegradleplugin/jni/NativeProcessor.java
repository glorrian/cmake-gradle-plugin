package dev.infochem.cmakegradleplugin.jni;

import dev.infochem.cmakegradleplugin.util.NativePlatform;

import java.net.URL;

public class NativeProcessor {
    public static String processLibraryName(String libraryName) {
        if (NativePlatform.IS_WINDOWS) {
            return libraryName + ".dll";
        } else if (NativePlatform.IS_LINUX) {
            return "lib" + libraryName + ".so";
        } else if (NativePlatform.IS_MACOS) {
            return "lib" + libraryName + ".dylib";
        }
        return libraryName;
    }

    public static void loadLibraryFromResources(Class<?> libraryClass, String libraryName) {
        URL libraryURL = libraryClass.getResource(processLibraryName(libraryName));
        System.load(libraryURL.getPath());
    }
}
