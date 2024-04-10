cmake_minimum_required(VERSION 3.5)

project(test_executable LANGUAGES CXX)

add_executable(test_executable hello.cxx)

set_target_properties(test_executable PROPERTIES RUNTIME_OUTPUT_DIRECTORY "bin")
