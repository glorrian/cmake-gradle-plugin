package dev.infochem.cmakegradleplugin;

import org.gradle.api.GradleException;
import org.gradle.api.Task;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;

public interface CMakeTask extends Task {
    @SuppressWarnings("unused")
    void exec();

    @OutputDirectory
    DirectoryProperty getBuildDirectory();

    @InputFile
    Property<String> getCmakeExecutable();

    default CMakeExtension getExtension() {
        CMakeExtension extension = getExtensions().findByType(CMakeExtension.class);
        if (extension == null) {
            throw new GradleException("Cannot find extension from DSL with data");
        }
        return extension;
    }
}
