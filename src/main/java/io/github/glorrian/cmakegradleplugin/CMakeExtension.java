package io.github.glorrian.cmakegradleplugin;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

/**
 * <p>Extension model for Gradle plugin to build CMake into Gradle</p>
 * <p>An abstract class cannot be used outside of Gradle. Gradle creates fields and getter implementations automatically</p>
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
