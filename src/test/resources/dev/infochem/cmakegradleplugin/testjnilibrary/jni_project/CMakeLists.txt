cmake_minimum_required(VERSION 3.5)

project(jnilibrary LANGUAGES CXX)
find_package(Java 1.8 REQUIRED COMPONENTS Development)

string(REGEX REPLACE "/bin/java$" "" JAVA_HOME ${Java_JAVA_EXECUTABLE})

set(JAVA_INCLUDE_PATH "${JAVA_HOME}/include")

if (CMAKE_HOST_WIN32)
    set(JAVA_INCLUDE_PATH2 "${JAVA_INCLUDE_PATH}/win32")
elseif(CMAKE_HOST_APPLE)
    set(JAVA_INCLUDE_PATH2 "${JAVA_INCLUDE_PATH}/darwin")
elseif(CMAKE_HOST_UNIX)
    set(JAVA_INCLUDE_PATH2 "${JAVA_INCLUDE_PATH}/linux")
endif()

add_library(jnilibrary SHARED jni_library.cpp)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "/")
target_include_directories(jnilibrary PUBLIC ${JAVA_INCLUDE_PATH} ${JAVA_INCLUDE_PATH2})
