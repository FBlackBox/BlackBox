//
// Created by canyie on 2020/3/18.
//

#ifndef PINE_PINE_H
#define PINE_PINE_H

#include <jni.h>

extern "C" {
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved);
bool register_Pine(JNIEnv* env, jclass Pine);
bool register_Ruler(JNIEnv* env, jclass Ruler);

void Ruler_m1(JNIEnv* env, jclass); // used for search ArtMethod members
}

#endif //PINE_PINE_H
