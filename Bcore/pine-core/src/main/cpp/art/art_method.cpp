//
// Created by canyie on 2020/2/9.
//

#include <jni.h>
#include "art_method.h"
#include "../jni_bridge.h"
#include "../utils/elf_img.h"
#include "../utils/well_known_classes.h"
#include "../utils/scoped_local_ref.h"
#include "../utils/memory.h"

using namespace pine::art;

uint32_t ArtMethod::kAccCompileDontBother = 0;
uint32_t ArtMethod::kAccPreCompiled = 0;

size_t ArtMethod::size = 0;
void* ArtMethod::art_quick_to_interpreter_bridge = nullptr;
void* ArtMethod::art_quick_generic_jni_trampoline = nullptr;
void* ArtMethod::art_interpreter_to_compiled_code_bridge = nullptr;
void* ArtMethod::art_interpreter_to_interpreter_bridge = nullptr;

void (*ArtMethod::copy_from)(ArtMethod*, ArtMethod*, size_t) = nullptr;
void (*ArtMethod::throw_invocation_time_error)(ArtMethod*) = nullptr;

Member<ArtMethod, uint32_t> ArtMethod::access_flags_;
Member<ArtMethod, void*> ArtMethod::entry_point_from_jni_;
Member<ArtMethod, void*> ArtMethod::entry_point_from_compiled_code_;
Member<ArtMethod, void*>* ArtMethod::entry_point_from_interpreter_;
Member<ArtMethod, uint32_t>* ArtMethod::declaring_class = nullptr;

void ArtMethod::Init(const ElfImg* handle) {
    art_quick_to_interpreter_bridge = handle->GetSymbolAddress("art_quick_to_interpreter_bridge");
    art_quick_generic_jni_trampoline = handle->GetSymbolAddress("art_quick_generic_jni_trampoline");

    if (Android::version < Android::kN) {
        art_interpreter_to_compiled_code_bridge = handle->GetSymbolAddress(
                "artInterpreterToCompiledCodeBridge");
        art_interpreter_to_interpreter_bridge = handle->GetSymbolAddress(
                "artInterpreterToInterpreterBridge");
    }

    const char* symbol_copy_from = nullptr;
    if (Android::version >= Android::kO) {
        // art::ArtMethod::CopyFrom(art::ArtMethod *, art::PointerSize)
        symbol_copy_from = "_ZN3art9ArtMethod8CopyFromEPS0_NS_11PointerSizeE";
    } else if (Android::version >= Android::kN) {
#ifdef __LP64__
        // art::ArtMethod::CopyFrom(art::ArtMethod *, unsigned long)
        symbol_copy_from = "_ZN3art9ArtMethod8CopyFromEPS0_m";
#else
        // art::ArtMethod::CopyFrom(art::ArtMethod *, unsigned int)
        symbol_copy_from = "_ZN3art9ArtMethod8CopyFromEPS0_j";
#endif
    } else if (Android::version >= Android::kM) {
#ifdef __LP64__
        // art::ArtMethod::CopyFrom(art::ArtMethod const *, unsigned long)
        symbol_copy_from = "_ZN3art9ArtMethod8CopyFromEPKS0_m";
#else
        // art::ArtMethod::CopyFrom(art::ArtMethod const *, unsigned int)
        symbol_copy_from = "_ZN3art9ArtMethod8CopyFromEPKS0_j";
#endif
    }

    if (symbol_copy_from)
        copy_from = reinterpret_cast<void (*)(ArtMethod*, ArtMethod*, size_t)>(
                handle->GetSymbolAddress(symbol_copy_from));

    if (UNLIKELY(Android::version == Android::kO))
        throw_invocation_time_error = reinterpret_cast<void (*)(ArtMethod*)>(handle->GetSymbolAddress(
                "_ZN3art9ArtMethod24ThrowInvocationTimeErrorEv"));
}

ArtMethod* ArtMethod::FromReflectedMethod(JNIEnv* env, jobject javaMethod) {
    if (Android::version >= Android::kR) {
        return GetArtMethodForR(env, javaMethod);
    }
    return reinterpret_cast<ArtMethod*>(env->FromReflectedMethod(javaMethod));
}

ArtMethod*
ArtMethod::Require(JNIEnv* env, jclass c, const char* name, const char* signature, bool is_static) {
    jmethodID m = is_static ? env->GetStaticMethodID(c, name, signature)
                            : env->GetMethodID(c, name, signature);
    if (Android::version >= Android::kR) {
        if (reinterpret_cast<uintptr_t>(m) & 1) {
            ScopedLocalRef javaMethod(env, env->ToReflectedMethod(c, m, static_cast<jboolean>(is_static)));
            return GetArtMethodForR(env, javaMethod.Get());
        }
    }
    return reinterpret_cast<ArtMethod*>(m);
}

static inline size_t Difference(intptr_t a, intptr_t b) {
    intptr_t size = b - a;
    if (size < 0) size = -size;
    return static_cast<size_t>(size);
}

void ArtMethod::InitMembers(JNIEnv* env, ArtMethod* m1, ArtMethod* m2, ArtMethod* m3, uint32_t access_flags) {
    if (Android::version >= Android::kN) {
        kAccCompileDontBother = (Android::version >= Android::kOMr1)
                                ? AccessFlags::kCompileDontBother_O_MR1
                                : AccessFlags::kCompileDontBother_N;
        if (Android::version >= Android::kR)
            kAccPreCompiled = (Android::version == Android::kR)
                    ? AccessFlags::kPreCompiled_R
                    : AccessFlags::kPreCompiled_S;
    }

    size = Difference(reinterpret_cast<intptr_t>(m1), reinterpret_cast<intptr_t>(m2));
    int android_version = Android::version;
    if (LIKELY(android_version >= Android::kL)) {
        for (uint32_t offset = 0; offset < size; offset += 2) {
            void* ptr = reinterpret_cast<void*>(reinterpret_cast<uintptr_t>(m1) + offset);
            if ((*static_cast<uint32_t*>(ptr)) == access_flags) {
                access_flags_.SetOffset(offset);
            } else if (UNLIKELY(android_version == Android::kL)) {
                // On Android 5.0, type of entry_point_from_jni_ is uint64_t
                if ((*static_cast<uint64_t*>(ptr)) == reinterpret_cast<uint64_t>(Ruler_m1))
                    entry_point_from_jni_.SetOffset(offset);
            } else if ((*static_cast<void**>(ptr)) == Ruler_m1) {
                entry_point_from_jni_.SetOffset(offset);
            }

            bool done = access_flags_.IsValid() && entry_point_from_jni_.IsValid();
            if (UNLIKELY(done)) break;
        }

        if (UNLIKELY(!access_flags_.IsValid())) {
            do {
                if (LIKELY(Android::version >= Android::kN)) {
                    // TODO: Is this really possible?
                    LOGW("failed to find access_flags_ with default access flags, try again with kAccCompileDontBother");
                    access_flags |= kAccCompileDontBother;
                    int offset = Memory::FindOffset(m1, access_flags, size, 2);
                    if (LIKELY(offset >= 0)) {
                        LOGW("Found access_flags_ with kAccCompileDontBother, offset %d", offset);
                        access_flags_.SetOffset(offset);
                        break;
                    }

                    if (LIKELY(Android::version >= Android::kR)) {
                        // Android R has a new access flags: kAccPreCompiled
                        // TODO: Is this really possible?
                        LOGW("failed to find access_flags_ with default access flags, try again with kAccPreCompiled");
                        access_flags |= kAccPreCompiled;
                        // Don't clear kAccCompileDontBother.
                        offset = Memory::FindOffset(m1, access_flags, size, 2);
                        if (LIKELY(offset >= 0)) {
                            LOGW("Found access_flags_ with kAccPreCompiled, offset %d", offset);
                            access_flags_.SetOffset(offset);
                            break;
                        }
                    }
                }
                LOGE("Member access_flags_ not found in ArtMethod, use default.");
                access_flags_.SetOffset(GetDefaultAccessFlagsOffset());
            } while (false);
        }

        uint32_t entry_point_member_size = Android::version == Android::kL
                                           ? sizeof(uint64_t) : sizeof(void*);

        if (LIKELY(entry_point_from_jni_.IsValid())) {
            uint32_t compiled_code_entry_offset = entry_point_from_jni_.GetOffset()
                                                  + entry_point_member_size;

            if (Android::version >= Android::kO) {
                // Only align offset on Android O+ (PtrSizedFields is PACKED(4) in Android N or lower.)
                compiled_code_entry_offset = Memory::AlignUp<uint32_t>(compiled_code_entry_offset,
                                                   entry_point_member_size);
            }

            entry_point_from_compiled_code_.SetOffset(compiled_code_entry_offset);

        } else {
            LOGE("Member entry_point_from_jni_ not found in ArtMethod, use default.");
            entry_point_from_jni_.SetOffset(GetDefaultEntryPointFromJniOffset());
            entry_point_from_compiled_code_.SetOffset(
                    GetDefaultEntryPointFromQuickCompiledCodeOffset());
        }

        if (Android::version < Android::kN) {
            // Not align: PtrSizedFields is PACKED(4) in the android version.
            entry_point_from_interpreter_ = new Member<ArtMethod, void*>(
                    entry_point_from_jni_.GetOffset() - entry_point_member_size);
        } else {
            // On Android 7.0+, the declaring_class may be moved by the GC,
            // so we check and update it when invoke backup method.
            declaring_class = new Member<ArtMethod, uint32_t>(0);
        }
    } else {
        // Hardcode members offset for Kitkat :(
        LOGW("Android Kitkat, hardcode offset only...");
        access_flags_.SetOffset(28);
        entry_point_from_compiled_code_.SetOffset(32);

        // FIXME This offset has not been verified, so it may be wrong
        entry_point_from_interpreter_ = new Member<ArtMethod, void*>(36);
    }

    if (UNLIKELY(throw_invocation_time_error)) {
        // See https://github.com/canyie/pine/issues/8
        if (UNLIKELY(m3->TestDontCompile(env))) {
            LOGW("Detected android 8.1 runtime on android 8.0 device");
            LOGW("For more info, see https://github.com/canyie/pine/issues/8");
            kAccCompileDontBother = AccessFlags::kCompileDontBother_O_MR1;
        }
    }
}

void ArtMethod::BackupFrom(ArtMethod* source, void* entry, bool is_inline_hook, bool is_native, bool is_proxy) {
    if (LIKELY(copy_from)) {
        copy_from(this, source, sizeof(void*));
    } else {
        memcpy(this, source, size);
    }

    uint32_t access_flags = source->GetAccessFlags();
    if (Android::version >= Android::kN) {
        if (Android::version >= Android::kR) access_flags &= ~kAccPreCompiled;
        access_flags |= kAccCompileDontBother;
    }
    if ((access_flags & AccessFlags::kStatic) == 0) {
        // Non-static method, set kAccPrivate to ensure it will be invoked like a direct method.
        access_flags &= ~(AccessFlags::kPublic | AccessFlags::kProtected);
        access_flags |= AccessFlags::kPrivate;
    }
    access_flags &= ~AccessFlags::kConstructor;
    SetAccessFlags(access_flags);

    // JIT compilation was added in Android N. When we hook a method, we may change its entry point
    // and garbage collector loses reference to the entry point of compiled code, so jit info
    // about the target method will be recycled -- but our backup method still references these info
    // and causing random crashes. So we need to do something:
    // 1. If possible, update the method references in jit info to backup method, so collector can
    //   know these jit info are still reachable and won't recycle them.
    // 2. If not possible, clear references to these info in the backup method to prevent possible UAF.
    //   possible references: entry_point_from_compiled_code_ (may references jit compiled code),
    //   and data_ (may be a profiling info).

    bool clear_jit_info_ref = Android::version >= Android::kN && !is_proxy;
    if (LIKELY(clear_jit_info_ref)) {
        // First try to move jit info instead.
        clear_jit_info_ref = !Android::MoveJitInfo(source, this);
        if (UNLIKELY(clear_jit_info_ref))
            clear_jit_info_ref = !is_inline_hook && !is_native && art_quick_to_interpreter_bridge;
    }

    if (UNLIKELY(clear_jit_info_ref)) {
        // entry_point_from_compiled_code_ (may references jit compiled code)
        SetEntryPointFromCompiledCode(art_quick_to_interpreter_bridge);

        // For non-native and non-proxy methods, the entry_point_from_jni_ member is used to save
        // ProfilingInfo, and the ProfilingInfo may saved original compiled code entry, the interpreter
        // will jump directly to the saved_code_entry_ for execution. Clear entry_point_from_jni_ to avoid it.
        entry_point_from_jni_.Set(this, nullptr);
    } else {
        SetEntryPointFromCompiledCode(entry);

        // ArtMethod::CopyFrom() will clear the data_ member, the member is used to save
        // the original interface method for proxy method. Restore it to avoid errors.
        if (UNLIKELY((is_native || is_proxy) && Android::version >= Android::kO))
            SetEntryPointFromJni(source->GetEntryPointFromJni());
    }
}

void ArtMethod::AfterHook(bool is_inline_hook, bool is_native_or_proxy) {
    uint32_t access_flags = GetAccessFlags();

    if (Android::version >= Android::kN) {
        if (Android::version >= Android::kR) access_flags &= ~kAccPreCompiled;
        access_flags |= kAccCompileDontBother;
    }

    if (Android::version >= Android::kO && !is_inline_hook) {
        if (UNLIKELY(PineConfig::debuggable && !is_native_or_proxy)) {
            // Android 8.0+ and debug mode, ART may force the use of interpreter mode,
            // and entry_point_from_compiled_code_ will be ignored. Set kAccNative to avoid it.
            // See ClassLinker::ShouldUseInterpreterEntrypoint(ArtMethod*, const void*)
            access_flags |= AccessFlags::kNative;
        }
    }

    if (Android::version >= Android::kQ) {
        // On Android 10+, a method can be execute with fast interpreter is cached in access flags,
        // and we may need to disable fast interpreter for a hooked method.
        // Clear the cached flag(kAccFastInterpreterToInterpreterInvoke) to refresh the state.
        access_flags &= ~AccessFlags::kFastInterpreterToInterpreterInvoke;
    }

    bool is_native = (access_flags & AccessFlags::kNative) != 0;
    if (UNLIKELY(is_native && Android::version >= Android::kL)) {
        // GC is disabled when executing FastNative and CriticalNative methods
        // and may cause deadlocks. This is not applicable for hooked methods.
        access_flags &= ~AccessFlags::kFastNative;
        if (Android::version >= Android::kP) {
            access_flags &= ~AccessFlags::kCriticalNative;
        }
    }

    SetAccessFlags(access_flags);

    if (art_interpreter_to_compiled_code_bridge)
        SetEntryPointFromInterpreter(art_interpreter_to_compiled_code_bridge);
}

bool ArtMethod::TestDontCompile(JNIEnv* env) {
    // ThrowInvocationTimeError() has a DCHECK(IsAbstract()), so we should use abstract method to test it.
    // assert(IsAbstract());

    // AbstractMethodError extends from IncompatibleClassChangeError
    jclass AbstractMethodError = env->FindClass("java/lang/AbstractMethodError");
    uint32_t access_flags = GetAccessFlags();
    SetAccessFlags(access_flags | AccessFlags::kCompileDontBother_N);
    throw_invocation_time_error(this);
    SetAccessFlags(access_flags);
    jthrowable exception = env->ExceptionOccurred();
    env->ExceptionClear();
    bool special = exception != nullptr && !env->IsInstanceOf(exception, AbstractMethodError);
    env->DeleteLocalRef(AbstractMethodError);
    env->DeleteLocalRef(exception);
    return special;
}

