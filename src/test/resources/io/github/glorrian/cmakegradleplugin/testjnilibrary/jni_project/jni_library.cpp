#include <jni.h>
#include "io_github_glorrian_cmakegradleplugin_testjnilibrary_JNILinker.h"

JNIEXPORT jint JNICALL Java_io_github_glorrian_cmakegradleplugin_testjnilibrary_JNILinker_doubleInt
  (JNIEnv* env, jclass clazz, jint number) {
  return number * 2;
}