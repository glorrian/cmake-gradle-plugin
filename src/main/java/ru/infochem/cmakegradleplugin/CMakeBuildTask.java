package ru.infochem.cmakegradleplugin;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecResult;
import ru.infochem.cmakegradleplugin.utlis.NativePlatform;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Task for the cmake project build stage from the cache
 *
 * <ul>
 *     <li>Gradle group: CMake</li>
 *     <li>Gradle description: Build CMake project</li>
 * </ul>
 *
 * @version 1.0
 */
public class CMakeBuildTask extends DefaultTask {
    private final Logger logger = getLogger();

    private final ObjectFactory objectFactory = getProject().getObjects();
    private final DirectoryProperty buildDirectory = objectFactory.directoryProperty();
    private final Property<String> cmakeExecutable = objectFactory.property(String.class);
    private final Property<String> buildType = objectFactory.property(String.class);

    public CMakeBuildTask() {
        setGroup(CMakePlugin.gradleTasksGroup);
        setDescription("Build CMake project");
    }

    private Object[] buildCommandLine() {
        List<String> cmdLine = new ArrayList<>();
        cmdLine.add(new File(cmakeExecutable.get()).getAbsolutePath());

        if (NativePlatform.IS_WINDOWS)
            cmdLine.add("--config " + buildType);

        Collections.addAll(cmdLine, "--build", ".", "--clean-first");

        return cmdLine.toArray(new Object[0]);
    }

    @TaskAction
    void build() {
        logger.info(CMakePlugin.BUILD_CMAKE_TASK_NAME + "tasks is starting execution");
        if (logger.isDebugEnabled()) {
            logger.debug("The value of the buildDirectory property: " + buildDirectory.get());
            logger.debug("The value of the cmakeExecutable property: " + cmakeExecutable.get());
            logger.debug("The value of the buildType property: " + buildType.get());
        }

        ExecResult result = getProject().exec((task) -> {
            task.setWorkingDir(buildDirectory.get().getAsFile());
            task.commandLine(buildCommandLine());
        });
        result.assertNormalExitValue();
    }

    @OutputDirectory
    public DirectoryProperty getBuildDirectory() {
        return buildDirectory;
    }

    @InputFile
    public Property<String> getCmakeExecutable() {
        return cmakeExecutable;
    }

    @Input
    public Property<String> getBuildType() {
        return buildType;
    }

}
