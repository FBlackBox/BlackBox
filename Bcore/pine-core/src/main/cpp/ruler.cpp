//
// Created by canyie on 2020/3/18.
//

#include "jni_bridge.h"
#include "utils/macros.h"
#include "utils/log.h"

void Ruler_m1(JNIEnv*, jclass) {
    LOGI("Don't call me...");
}

static const JNINativeMethod gMethods[] = {
        {"m1", "()V", reinterpret_cast<void*>(Ruler_m1)}
};

bool register_Ruler(JNIEnv* env, jclass Ruler) {
    return LIKELY(env->RegisterNatives(Ruler, gMethods, NELEM(gMethods)) == JNI_OK);
}