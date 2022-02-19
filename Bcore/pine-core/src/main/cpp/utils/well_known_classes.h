//
// Created by canyie on 2020/4/1.
//

#ifndef PINE_WELL_KNOWN_CLASSES_H
#define PINE_WELL_KNOWN_CLASSES_H

#include <cstdlib>
#include <jni.h>
#include "macros.h"
#include "log.h"

namespace pine {
    class WellKnownClasses final {
    public:
        static void Init(JNIEnv* env);
        static jclass java_lang_reflect_ArtMethod;
        static jfieldID java_lang_reflect_Executable_artMethod;
    private:
        static jclass RequireClass(JNIEnv* env, const char* name) {
            jclass local_ref = env->FindClass(name);
            CHECK(local_ref != nullptr, "Required class %s not found", name);
            jclass global_ref = static_cast<jclass>(env->NewGlobalRef(local_ref));
            env->DeleteLocalRef(local_ref);
            return global_ref;
        }

        static jclass FindClass(JNIEnv* env, const char* name) {
            jclass local_ref = env->FindClass(name);
            if (local_ref != nullptr) {
                jclass global_ref = static_cast<jclass>(env->NewGlobalRef(local_ref));
                env->DeleteLocalRef(local_ref);
                return global_ref;
            } else {
                env->ExceptionClear();
                return nullptr;
            }
        }

        static jfieldID RequireNonStaticFieldID(JNIEnv* env, const char* class_name, const char* name,
                                                const char* signature) {
            jclass c = env->FindClass(class_name);
            CHECK(c != nullptr, "Required class %s not found", class_name);
            jfieldID field = env->GetFieldID(c, name, signature);
            CHECK(field != nullptr, "Required field %s with signature %s in class %s is not found",
                    name, signature, class_name);
            env->DeleteLocalRef(c);
            return field;
        }

        DISALLOW_IMPLICIT_CONSTRUCTORS(WellKnownClasses);
    };

}

#endif //PINE_WELL_KNOWN_CLASSES_H
