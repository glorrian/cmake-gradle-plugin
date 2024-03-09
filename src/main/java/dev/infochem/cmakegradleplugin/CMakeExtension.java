package dev.infochem.cmakegradleplugin;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import dev.infochem.cmakegradleplugin.util.BuildType;

import javax.inject.Inject;

/**
 * Extension model for Gradle plugin to build CMake into Gradle
 *
 * @version 1.0
 */
public class CMakeExtension {
    private final DirectoryProperty buildDirectory;
    private final DirectoryProperty sourceDirectory;
    private final Property<String> pathToExecutableCmake;
    private final Property<String> generator;
    private final Property<String> buildType;
    private final ListProperty<String> arguments;
    private final Property<String> toolchain;

    @Inject
    public CMakeExtension(ObjectFactory objectFactory, ProjectLayout projectLayout){
        buildDirectory = objectFactory.directoryProperty();
        sourceDirectory = objectFactory.directoryProperty();
        pathToExecutableCmake = objectFactory.property(String.class);
        generator = objectFactory.property(String.class);
        buildType = objectFactory.property(String.class);
        arguments = objectFactory.listProperty(String.class);
        toolchain = objectFactory.property(String.class);

        buildDirectory.set(projectLayout.getBuildDirectory().dir("cmake"));
        sourceDirectory.set(projectLayout.getProjectDirectory().dir("src/main/cpp"));
        buildType.set(BuildType.DEBUG);
    }

    public DirectoryProperty getBuildDirectory() {
        return buildDirectory;
    }

    public DirectoryProperty getSourceDirectory() {
        return sourceDirectory;
    }

    public Property<String> getToolchain() {
        return toolchain;
    }

    public Property<String> getPathToExecutableCmake() {
        return pathToExecutableCmake;
    }

    public Property<String> getGenerator() {
        return generator;
    }

    public Property<String> getBuildType() {
        return buildType;
    }

    public ListProperty<String> getArguments() {
        return arguments;
    }
}
