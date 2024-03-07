
plugins {
    id("java")
    id("com.gradle.plugin-publish") version "1.2.1"
    `java-gradle-plugin`
}

group = "ru.infochem"
version = "1.0"

repositories {
    mavenCentral()
}

gradlePlugin {
    plugins {
        create("CMakeGradlePlugin") {
            id = "ru.infochem.cmake-gradle-plugin"
            implementationClass = "ru.infochem.cmakegradleplugin.CMakePlugin"
        }
    }
}


dependencies {
    implementation(gradleApi())
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
