package dev.infochem.cmakegradleplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.tasks.TaskProvider;
import dev.infochem.cmakegradleplugin.util.NativePlatform;


/**
 * 
 */
public class CMakePlugin implements Plugin<Project> {
    public static final String gradleTasksGroup = "CMake";
    public static final String CONFIGURE_CMAKE_TASK_NAME = "ConfigureCMake";
    public static final String BUILD_CMAKE_TASK_NAME = "BuildCMake";

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply("base");
        CMakeExtension cmakeExtension = project.getExtensions().create("cmake", CMakeExtension.class);

        project.afterEvaluate(p -> {
            if (!cmakeExtension.getPathToExecutableCmake().isPresent()) {
            cmakeExtension.getPathToExecutableCmake().set(NativePlatform.getCMakeExecutable().getAbsolutePath());
            project.getLogger().info(
                    String.format("The CMake executable file - \"%s\" found into environment is using", NativePlatform.getCMakeExecutable()));
            }
            TaskContainer tasks = p.getTasks();

            final TaskProvider<CMakeConfigurationTask> configureCMake = tasks.register(CONFIGURE_CMAKE_TASK_NAME, CMakeConfigurationTask.class, task -> {
                task.getBuildType().set(cmakeExtension.getBuildType());
                task.getBuildDirectory().set(cmakeExtension.getBuildDirectory());
                task.getSourceDirectory().set(cmakeExtension.getSourceDirectory());
                task.getCmakeExecutable().set(cmakeExtension.getPathToExecutableCmake());
                task.getGenerator().set(cmakeExtension.getGenerator());
                task.getToolchain().set(cmakeExtension.getToolchain());
                task.getArguments().set(cmakeExtension.getArguments());
            });

            final TaskProvider<CMakeBuildTask> buildCMake = tasks.register(BUILD_CMAKE_TASK_NAME, CMakeBuildTask.class, task -> {
                task.getBuildDirectory().set(cmakeExtension.getBuildDirectory());
                task.getCmakeExecutable().set(cmakeExtension.getPathToExecutableCmake());
                task.getBuildType().set(cmakeExtension.getBuildType());
            });

            tasks.named("assemble").configure(task -> task.dependsOn(buildCMake));
            buildCMake.configure(task -> task.dependsOn(configureCMake));
        });
    }
}