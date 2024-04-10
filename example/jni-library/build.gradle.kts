plugins{
    application
    id("dev.glorrian.cmake-gradle-plugin")
}

cmake {
    arguments = listOf("-DLIBRARY_PATH=../../src/main/resources")
}

application {
    mainClass = "JNILibrary"
}