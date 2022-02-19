//
// Created by canyie on 2020/3/31.
//

#include "thread.h"

using namespace pine::art;

Thread* (*Thread::current)() = nullptr;

jclass Thread::Thread_ = nullptr;
jfieldID Thread::nativePeer = nullptr;
jmethodID Thread::currentThread = nullptr;

pthread_key_t* Thread::key_self = nullptr;

jobject (*Thread::new_local_ref)(JNIEnv*, void*) = nullptr;

jweak (*Thread::add_weak_global_ref)(JavaVM*, Thread*, void*) = nullptr;

void* (*Thread::decode_jobject)(Thread*, jobject) = nullptr;

void* (*Thread::alloc_non_movable)(void*, Thread*) = nullptr;

void Thread::Init(const ElfImg* handle) {
    if (Android::version == Android::kL || Android::version == Android::kLMr1) {
        // This function is needs to create the backup method on Lollipop.
        // Below M, ArtMethod actually is a instance of java.lang.reflect.ArtMethod, can't use malloc()
        // It should be immovable. On Kitkat, moving gc is unimplemented in art, so it can't be moved
        // but on Lollipop, this object may be moved by gc, so we need to ensure it is non-movable.
        alloc_non_movable = reinterpret_cast<void* (*)(void*, Thread*)>(handle->GetSymbolAddress(
                // art::mirror::Class::AllocNonMovableObject(art::Thread*)
                "_ZN3art6mirror5Class21AllocNonMovableObjectEPNS_6ThreadE"));
    }

    current = reinterpret_cast<Thread* (*)()>(handle->GetSymbolAddress(
            "_ZN3art6Thread14CurrentFromGdbEv")); // art::Thread::CurrentFromGdb()

    if (UNLIKELY(!current && Android::version < Android::kN)) {
        current = reinterpret_cast<Thread* (*)()>(handle->GetSymbolAddress(
                "_ZN3art6Thread7CurrentEv")); // art::Thread::Current()
        if (UNLIKELY(!current)) {
            key_self = static_cast<pthread_key_t*>(handle->GetSymbolAddress(
                    "_ZN3art6Thread17pthread_key_self_E")); // art::Thread::pthread_key_self_
        }
    }

    new_local_ref = reinterpret_cast<jobject (*)(JNIEnv*, void*)>(handle->GetSymbolAddress(
            "_ZN3art9JNIEnvExt11NewLocalRefEPNS_6mirror6ObjectE")); // art::JNIEnvExt::NewLocalRef(art::mirror::Object *)

    if (UNLIKELY(!new_local_ref)) {
        LOGW("JNIEnvExt::NewLocalRef is unavailable, try JavaVMExt::AddWeakGlobalReference");
        const char* add_global_weak_ref_symbol;
        if (Android::version < Android::kM) {
            // art::JavaVMExt::AddWeakGlobalReference(art::Thread *, art::mirror::Object *)
            add_global_weak_ref_symbol = "_ZN3art9JavaVMExt22AddWeakGlobalReferenceEPNS_6ThreadEPNS_6mirror6ObjectE";
        } else if (Android::version < Android::kO) {
            // art::JavaVMExt::AddWeakGlobalRef(art::Thread *, art::mirror::Object *)
            add_global_weak_ref_symbol = "_ZN3art9JavaVMExt16AddWeakGlobalRefEPNS_6ThreadEPNS_6mirror6ObjectE";
        } else {
            // art::JavaVMExt::AddWeakGlobalRef(art::Thread *, art::ObjPtr<art::mirror::Object>)
            add_global_weak_ref_symbol = "_ZN3art9JavaVMExt16AddWeakGlobalRefEPNS_6ThreadENS_6ObjPtrINS_6mirror6ObjectEEE";
        }
        add_weak_global_ref = reinterpret_cast<jweak (*)(JavaVM*, Thread*, void*)>(
                handle->GetSymbolAddress(add_global_weak_ref_symbol));
    }

    decode_jobject = reinterpret_cast<void* (*)(Thread*, jobject)>(handle->GetSymbolAddress(
            "_ZNK3art6Thread13DecodeJObjectEP8_jobject")); // art::Thread::DecodeJObject(_jobject *)
}
