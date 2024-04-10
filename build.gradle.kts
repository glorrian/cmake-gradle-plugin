
plugins {
    id("java")
    id("com.gradle.plugin-publish") version "1.2.1"
    `java-gradle-plugin`
}

group = "dev.glorrian"
version = "1.0"

repositories {
    mavenCentral()
}

gradlePlugin {
    vcsUrl = "https://github.com/GloRRian55/cmake-gradle-plugin"
    plugins {
        create("CMakeGradlePlugin") {
            id = "dev.glorrian.cmake-gradle-plugin"
            implementationClass = "dev.glorrian.cmakegradleplugin.CMakePlugin"
            displayName = "CMake Gradle Plugin"
            description = "Gradle plugin for comfortable CMake build system using inside gradle builds"
            tags.set(listOf("cmake", "jni", "c++", "c", "native", "plugin"))
        }
    }
}

tasks.withType<Javadoc> {
    (options as StandardJavadocDocletOptions).tags("implNote")
}

dependencies {
    runtimeOnly(gradleApi())
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
