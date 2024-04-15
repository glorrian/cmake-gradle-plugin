# CMake Gradle Plugin
Gradle plugin for comfortable CMake build system using inside your gradle builds.

This plugin is convenient to use in cases when you need to connect a CMake project to your Gradle project. For example, to build a dynamic library to use JNI.

To use it, you need to apply the plugin in your build script:

Groovy:
```groovy
apply plugin: 'io.github.glorrian.cmake-gradle-plugin'
```
Kotlin:
```kotlin
apply(plugin = "io.github.glorrian.cmake-gradle-plugin")
```

Then write the configuration of your Cmake project in the gradle build file:

Groovy:
```groovy
cmake {
    // default is "projectRoot/build/cmake"
    buildDirectory = buildDirectory.dir("path/to/your/build/dir/for/cmake/project")
    //default is "projectRoot/src/main/cpp"
    sourceDirectory = layout.projectDirectory.dir("path/to/your/cmake/project")
    // if not specified, it will be searched automatically
    cMakeExecutable = "path/to/cmake/binary"
    //default is Debug
    buildType = "CMake build type"
    toolchain = "toolchain/file"
    generator = "CMake generator"
    arguments = ["ANY", "ARGUMENTS"]
}
```
Kotlin:
```kotlin
cmake {
    // default is "projectRoot/build/cmake"
    buildDirectory = buildDirectory.dir("path/to/your/build/dir/for/cmake/project")
    //default is "projectRoot/src/main/cpp"
    sourceDirectory = layout.projectDirectory.dir("path/to/your/cmake/project")
    // if not specified, it will be searched automatically
    cMakeExecutable = "path/to/cmake/binary"
    //default is Debug
    buildType = "CMake build type"
    toolchain = "toolchain/file"
    generator = "CMake generator"
    arguments = listOf("ANY", "ARGUMENTS")
}
```
Let's look at all the properties:
- [DirectoryProperty](https://docs.gradle.org/current/javadoc/org/gradle/api/file/DirectoryProperty.html) buildDirectory - indicates the path where the CMake project will be assembled. By default, the cmake folder will be set inside the standard gradle build folder
- [DirectoryProperty](https://docs.gradle.org/current/javadoc/org/gradle/api/file/DirectoryProperty.html) sourceDirectory - indicates the folder where the CMake project is located, the CMakeLists.txt file must be in the folder to build the project. By default, the src/main/cxx folder will be selected if it exists.
- [Property](https://docs.gradle.org/current/javadoc/org/gradle/api/provider/Property.html)<[String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)> сMakeExecutable - the path that indicates to the cmake binary executable file, if this path is not specified, the plugin will try to find it in the PATH environment of your system.
- [Property](https://docs.gradle.org/current/javadoc/org/gradle/api/provider/Property.html)<[String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)> buildType - the value of the [CMAKE_BUILD_TYPE](https://cmake.org/cmake/help/latest/variable/CMAKE_BUILD_TYPE.html) variable in your cmake project
- [Property](https://docs.gradle.org/current/javadoc/org/gradle/api/provider/Property.html)<[String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)> toolchain - the value of the [CMAKE_TOOLCHAIN_FILE](https://cmake.org/cmake/help/latest/variable/CMAKE_TOOLCHAIN_FILE.html) variable in your cmake project
- [Property](https://docs.gradle.org/current/javadoc/org/gradle/api/provider/Property.html)<[String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)> generator - the name of the generator that will be used to build your CMake project (the generator that you are calling must be installed on your device)
- [ListProperty](https://docs.gradle.org/current/javadoc/org/gradle/api/provider/ListProperty.html)<[String](https://docs.oracle.com/javase/8/docs/api/java/lang/String.html)>arguments - a list of arguments that will be added when building your CMake project

Unit and functional tests are written for the plugin. The build on Windows cannot be completed because a bug occurs in which the system cannot delete the temporary junit tests folder, since it contains a library loaded and used by java. The solution to this bug has not yet been found.

If you have any suggestions, criticism, or you have found some bugs in the plugin, you can create a new theme in the Issues tab of this GitHub repository, I will try not to miss anything.

If you liked the project, you can give it a star on the GitHub repository, thereby you will help in the development and promotion of the project.
