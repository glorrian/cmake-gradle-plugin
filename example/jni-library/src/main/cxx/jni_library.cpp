#include <jni.h>
#include "JNILibrary.h"

JNIEXPORT jint JNICALL Java_JNILibrary_callLibrary
  (JNIEnv* env, jclass clazz, jint number) {
  return number * 2;
}