package ru.infochem.cmakegradleplugin.utlis;

/**
 * Build type of the CMake project build
 *
 * @version 1.0
 */
public interface BuildType {
    String DEBUG = "Debug";
    String RELEASE = "Release";
    String REL_WITH_DEB_INFO = "RelWithDebInfo";
    String MIN_SIZE_REL = "MinSizeRel";
}
