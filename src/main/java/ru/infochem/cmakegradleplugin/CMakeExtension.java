package ru.infochem.cmakegradleplugin;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import ru.infochem.cmakegradleplugin.utlis.BuildType;

import javax.inject.Inject;

/**
 * Extension model for Gradle plugin to build CMake into Gradle
 *
 * @version 1.0
 */
public class CMakeExtension {
//    private final CMakeExecutor cMakeExecutor;

    private final DirectoryProperty buildDirectory;
    private final DirectoryProperty sourceDirectory;
    private final RegularFileProperty pathToExecutableCmake;
    private final Property<String> generator;
    private final Property<String> buildType;
    private final ListProperty<String> arguments;
    private final RegularFileProperty toolchain;

    @Inject
    public CMakeExtension(ObjectFactory objectFactory, ProjectLayout projectLayout){
        buildDirectory = objectFactory.directoryProperty();
        sourceDirectory = objectFactory.directoryProperty();
        pathToExecutableCmake = objectFactory.fileProperty();
        generator = objectFactory.property(String.class);
        buildType = objectFactory.property(String.class);
        arguments = objectFactory.listProperty(String.class);
        toolchain = objectFactory.fileProperty();

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

    public RegularFileProperty getToolchain() {
        return toolchain;
    }

    public RegularFileProperty getPathToExecutableCmake() {
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
