
plugins {
    id("java")
    id("com.gradle.plugin-publish") version "1.2.1"
    `java-gradle-plugin`
}

group = "dev.infochem"
version = "1.0"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("CMakeGradlePlugin") {
            id = "dev.infochem.cmake-gradle-plugin"
            implementationClass = "dev.infochem.cmakegradleplugin.CMakePlugin"
        }
    }
}


dependencies {
    implementation(gradleApi())
}
