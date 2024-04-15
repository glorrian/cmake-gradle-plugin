plugins{
    application
    id("io.github.glorrian.cmake-gradle-plugin")
}

cmake {
    arguments = listOf("-DLIBRARY_PATH=../../src/main/resources")
}

application {
    mainClass = "JNILibrary"
}