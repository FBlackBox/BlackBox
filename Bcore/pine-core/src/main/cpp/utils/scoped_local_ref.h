//
// Created by canyie on 2020/2/9.
//

#ifndef PINE_SCOPED_LOCAL_REF_H
#define PINE_SCOPED_LOCAL_REF_H

#include "macros.h"

template<typename T>
class ScopedLocalRef {
public:
    ScopedLocalRef(JNIEnv* env) : env(env), mLocalRef(nullptr) {
    }

    ScopedLocalRef(JNIEnv* env, T ref) : env(env), mLocalRef(ref) {
    }

    ~ScopedLocalRef() {
        Reset();
    }

    T Get() const {
        return mLocalRef;
    }

    void Reset(T newRef = nullptr) {
        if (mLocalRef != newRef) {
            if (mLocalRef != nullptr) {
                env->DeleteLocalRef(mLocalRef);
            }
            mLocalRef = newRef;
        }
    }

    T Release() __attribute__((warn_unused_result)) {
        T ref = mLocalRef;
        mLocalRef = nullptr;
        return ref;
    }

    bool IsNull() const {
        return mLocalRef == nullptr;
    }

    JNIEnv* Env() {
        return env;
    }

    bool operator==(std::nullptr_t) const {
        return IsNull();
    }

    bool operator!=(std::nullptr_t) const {
        return !IsNull();
    }

    bool operator==(ScopedLocalRef const s) const {
        return env->IsSameObject(mLocalRef, s.mLocalRef);
    }

    bool operator!=(ScopedLocalRef const s) const {
        return !env->IsSameObject(mLocalRef, s.mLocalRef);
    }

    bool operator==(T const other) const {
        return env->IsSameObject(mLocalRef, other);
    }

    bool operator!=(T const other) const {
        return !env->IsSameObject(mLocalRef, other);
    }

private:
    JNIEnv* env;
    T mLocalRef;

    DISALLOW_COPY_AND_ASSIGN(ScopedLocalRef);
};


class ScopedLocalClassRef : public ScopedLocalRef<jclass> {
public:
    ScopedLocalClassRef(JNIEnv* env) : ScopedLocalRef(env) {
    }

    ScopedLocalClassRef(JNIEnv* env, jclass ref) : ScopedLocalRef(env, ref) {
    }

    ScopedLocalClassRef(JNIEnv* env, const char* name) : ScopedLocalRef(env, env->FindClass(name)) {
    }

    jmethodID FindMethodID(const char* name, const char* signature) {
        JNIEnv* env = Env();
        jmethodID method = env->GetMethodID(Get(), name, signature);
        if (LIKELY(method != nullptr)) {
            return method;
        } else {
            env->ExceptionClear();
            return nullptr;
        }
    }
};

class ScopedLocalUtfStringRef : public ScopedLocalRef<jstring> {
public:
    ScopedLocalUtfStringRef(JNIEnv* env) : ScopedLocalRef(env) {
    }

    ScopedLocalUtfStringRef(JNIEnv* env, jstring ref) : ScopedLocalRef(env, ref) {
    }

    ScopedLocalUtfStringRef(JNIEnv* env, const char* content) : ScopedLocalRef(
            env, env->NewStringUTF(content)) {
    }
};

#endif //PINE_SCOPED_LOCAL_REF_H
