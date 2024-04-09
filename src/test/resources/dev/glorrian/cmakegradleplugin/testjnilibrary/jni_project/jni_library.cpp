#include <jni.h>
#include "dev_glorrian_cmakegradleplugin_testjnilibrary_JNILinker.h"

JNIEXPORT jint JNICALL Java_dev_glorrian_cmakegradleplugin_testjnilibrary_JNILinker_doubleInt
  (JNIEnv* env, jclass clazz, jint number) {
  return number * 2;
}