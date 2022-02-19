//
// Created by canyie on 2020/3/25.
//

#ifndef PINE_JNI_HELPER_H
#define PINE_JNI_HELPER_H

#include <jni.h>
#include "macros.h"

namespace pine {
    class JNIHelper {
    public:
        static bool SetStaticBooleanField(JNIEnv* env, jclass c, const char* name, bool value) {
            jfieldID field = env->GetStaticFieldID(c, name, "Z");
            if (LIKELY(field)) {
                env->SetStaticBooleanField(c, field, static_cast<jboolean>(value));
                return true;
            } else {
                return false;
            }
        }

        static void Throw(JNIEnv* env, const char* class_name, const char* msg) {
            jclass c = env->FindClass(class_name);
            if (UNLIKELY(!c)) {
                return;
            }
            env->ThrowNew(c, msg);
            env->DeleteLocalRef(c);
        }

    private:
        DISALLOW_IMPLICIT_CONSTRUCTORS(JNIHelper);
    };
}

#endif //PINE_JNI_HELPER_H
