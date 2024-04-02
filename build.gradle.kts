
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
