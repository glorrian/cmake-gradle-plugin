package ru.infochem.cmakegradleplugin;


import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Gradle task for CMake configure stage
 *
 * <ul>
 *     <li>Gradle group: CMake</li>
 *     <li>Gradle description: Build CMake project</li>
 * </ul>
 *
 * @version 1.0
 */
public class CMakeConfigurationTask extends DefaultTask {
    private final Logger logger = getLogger();

    private final ObjectFactory objectFactory = getProject().getObjects();
    private final DirectoryProperty buildDirectory = objectFactory.directoryProperty();
    private final Property<String> cmakeExecutable = objectFactory.property(String.class);
    private final Property<String> buildType = objectFactory.property(String.class);
    private final DirectoryProperty sourceDirectory = objectFactory.directoryProperty();
    private final Property<String> generator = objectFactory.property(String.class);
    private final ListProperty<String> arguments = objectFactory.listProperty(String.class);
    private final Property<String> toolchain = objectFactory.property(String.class);

    public CMakeConfigurationTask() {
        setGroup("cmake");
        setDescription("Configure a Build with CMake");
    }

    private List<String> buildCommandLine() {
        List<String> cmdLine = new ArrayList<>();
        cmdLine.add(new File(cmakeExecutable.get()).getAbsolutePath());

        cmdLine.add("-DCMAKE_BUILD_TYPE=" + getBuildType().get());

        if (generator.isPresent()){
            cmdLine.add("-G");
            cmdLine.add(generator.get());
        }
        if (arguments.isPresent())
            cmdLine.addAll(arguments.get());
        if (toolchain.isPresent())
            cmdLine.add("-DCMAKE_TOOLCHAIN_FILE=" + toolchain.get());

        cmdLine.add(getSourceDirectory().get().getAsFile().getAbsolutePath());
        System.out.println(cmdLine);
        return cmdLine;
    }

    @TaskAction
    public void configure(){
        logger.info(CMakePlugin.CONFIGURE_CMAKE_TASK_NAME + "tasks is starting execution");
        if (logger.isDebugEnabled()) {
            logger.debug("The value of the buildDirectory property: " + buildDirectory.get());
            logger.debug("The value of the cmakeExecutable property: " + cmakeExecutable.get());
            logger.debug("The value of the buildType property: " + buildType.get());
            logger.debug("The value of the sourceDirectory property: " + sourceDirectory.get());
            logger.debug("The value of the generator property: " + generator.getOrElse("Not present"));
        }

        CMakeExecutor cMakeExecutor = new CMakeExecutor(logger);
        cMakeExecutor.exec(buildCommandLine(), buildDirectory.get().getAsFile());
//        ExecResult result = getProject().exec(execSpec -> {
//            execSpec.setWorkingDir(buildDirectory.get().getAsFile());
//            execSpec.commandLine(buildCommandLine());
//        });
//        result.assertNormalExitValue();
    }

    @OutputDirectory
    public DirectoryProperty getBuildDirectory() {
        return buildDirectory;
    }

    @InputDirectory
    public DirectoryProperty getSourceDirectory() {
        return sourceDirectory;
    }

    @InputFile
    public Property<String> getCmakeExecutable() {
        return cmakeExecutable;
    }

    @Input
    public Property<String> getBuildType() {
        return buildType;
    }

    @Optional
    @Input
    public Property<String> getGenerator() {
        return generator;
    }

    @Optional
    @InputFile
    public Property<String> getToolchain() {
        return toolchain;
    }

    @Optional
    @Input
    public ListProperty<String> getArguments() {
        return arguments;
    }
}
