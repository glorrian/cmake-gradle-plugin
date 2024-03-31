#include <jni.h>
#include "dev_infochem_cmakegradleplugin_testjnilibrary_JNILinker.h"

JNIEXPORT jint JNICALL Java_dev_infochem_cmakegradleplugin_testjnilibrary_JNILinker_doubleInt
  (JNIEnv* env, jclass clazz, jint number) {
  return number * 2;
}