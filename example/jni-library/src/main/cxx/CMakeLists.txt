cmake_minimum_required(VERSION 3.5)

project(jnilibrary LANGUAGES CXX)
find_package(Java 1.8 REQUIRED COMPONENTS Development)

if (CMAKE_HOST_WIN32)
    string(REGEX REPLACE "/bin/java.exe$" "" JAVA_HOME ${Java_JAVA_EXECUTABLE})
    set(JAVA_INCLUDE_PATH2 "${JAVA_HOME}/include/win32")
elseif(CMAKE_HOST_APPLE)
    string(REGEX REPLACE "/bin/java$" "" JAVA_HOME ${Java_JAVA_EXECUTABLE})
    set(JAVA_INCLUDE_PATH2 "${JAVA_HOME}/include/darwin")
elseif(CMAKE_HOST_UNIX)
    string(REGEX REPLACE "/bin/java$" "" JAVA_HOME ${Java_JAVA_EXECUTABLE})
    set(JAVA_INCLUDE_PATH2 "${JAVA_HOME}/include/linux")
endif()
set(JAVA_INCLUDE_PATH "${JAVA_HOME}/include")

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${LIBRARY_PATH})
add_library(jnilibrary SHARED jni_library.cpp)

target_include_directories(jnilibrary PUBLIC ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2})
