package dev.infochem.cmakegradleplugin;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

/**
 * Extension model for Gradle plugin to build CMake into Gradle
 *
 * @version 1.0
 */
@SuppressWarnings("unused")
public abstract class CMakeExtension {
    abstract public DirectoryProperty getBuildDirectory();

    abstract public DirectoryProperty getSourceDirectory();

    abstract public Property<String> getToolchain();

    abstract public Property<String> getCMakeExecutable();

    abstract public Property<String> getGenerator();

    abstract public Property<String> getBuildType();

    abstract public ListProperty<String> getArguments();

}
