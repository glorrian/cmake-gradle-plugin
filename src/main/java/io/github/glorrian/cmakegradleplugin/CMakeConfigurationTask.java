package io.github.glorrian.cmakegradleplugin;

import org.gradle.api.file.DirectoryProperty;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Gradle task for CMake configure stage.
 * Cannot be used outside Gradle.
 *
 * @version 1.0
 */
public class CMakeConfigurationTask extends CMakeTask {
    private final Logger logger = LoggerFactory.getLogger(CMakeConfigurationTask.class);

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
        setProperties(getExtension());
        setListProperties(getExtension());
    }

    /**
     * Method to set value of arguments. It's needs because ListProperty is not an inheritor of {@link Property} class
     * @param extension DSL extension with data
     */
    private void setListProperties(CMakeExtension extension) {
        try {
            Method setterMethod = ListProperty.class.getMethod("value", Provider.class);
            setTypedFields(extension, ListProperty.class, setterMethod);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<String> buildCommandLine() {
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
        logger.debug("Command to CMakeConfigurationTask is assembled - \"{}\"", String.join(" ", cmdLine));
        return cmdLine;
    }

    @TaskAction
    public void execute(){
        logger.info("{} tasks is starting execution", CMakePlugin.CONFIGURE_CMAKE_TASK_NAME);
        logProviders(logger::debug);
        CMakeExecutor cMakeExecutor = new CMakeExecutor(getClass());
        cMakeExecutor.execute(buildCommandLine(), buildDirectory.get().getAsFile());
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
    public Property<String> getCMakeExecutable() {
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
