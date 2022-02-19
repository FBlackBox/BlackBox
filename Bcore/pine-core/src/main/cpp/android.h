//
// Created by canyie on 2020/3/15.
//

#ifndef PINE_ANDROID_H
#define PINE_ANDROID_H

#include <jni.h>
#include "art/gc_defs.h"
#include "utils/log.h"
#include "utils/macros.h"
#include "utils/elf_img.h"

namespace pine {
    class ScopedGCCriticalSection {
    public:
        ALWAYS_INLINE ScopedGCCriticalSection(void* self, art::GcCause cause, art::CollectorType collector);
        ALWAYS_INLINE ~ScopedGCCriticalSection();
    private:
        art::GCCriticalSection critical_section_;
        const char* old_no_suspend_reason_;
    };

    class Android final {
    public:
        static inline constexpr bool Is64Bit() {
            return sizeof(void*) == 8;
        }

        static void Init(JNIEnv* env, int sdk_version, bool disable_hiddenapi_policy, bool disable_hiddenapi_policy_for_platform);
        static void DisableHiddenApiPolicy(bool application, bool platform) {
            ElfImg handle("libart.so");
            DisableHiddenApiPolicy(&handle, application, platform);
        }
        static bool DisableProfileSaver();
        static void SetClassLinker(void* class_linker) {
            LOGI("Got class linker %p", class_linker);
            class_linker_ = class_linker;
        }
        static void* GetClassLinker() {
            return class_linker_;
        }

        static void MakeInitializedClassesVisiblyInitialized(void* thread, bool wait) {
            // If symbol MakeInitializedClassesVisiblyInitialized not found,
            // class_linker_ won't be initialized.
            if (!class_linker_) {
                return;
            }
            make_visibly_initialized_(class_linker_, thread, wait);
        }

        static bool MoveJitInfo(void* from, void* to) {
            if (LIKELY(jit_code_cache_ && move_obsolete_method_)) {
                move_obsolete_method_(jit_code_cache_, from, to);
                return true;
            }
            return false;
        }

        static int version;
        static JavaVM* jvm;

        static void StartGCCriticalSection(void* cookie, void* self, art::GcCause cause, art::CollectorType collector) {
            if (start_gc_critical_section) {
                start_gc_critical_section(cookie, self, cause, collector);
            }
        }

        static void EndGCCriticalSection(void* cookie) {
            if (end_gc_critical_section) {
                end_gc_critical_section(cookie);
            }
        }

        static void SuspendVM(void* cookie, void* self, const char* cause) {
            if (suspend_vm) {
                suspend_vm();
            } else if (suspend_all) {
                // Avoid a deadlock between GC and debugger where GC gets suspended during GC. b/25800335.
                ScopedGCCriticalSection gcs(self, art::GcCause::kGcCauseDebugger, art::CollectorType::kCollectorTypeDebugger);
                suspend_all(cookie, cause, false);
            }
        }

        static void ResumeVM(void* cookie) {
            if (resume_vm) {
                resume_vm();
            } else if (resume_all) {
                resume_all(cookie);
            }
        }

        static constexpr int kK = 19;
        static constexpr int kL = 21;
        static constexpr int kLMr1 = 22;
        static constexpr int kM = 23;
        static constexpr int kN = 24;
        static constexpr int kNMr1 = 25;
        static constexpr int kO = 26;
        static constexpr int kOMr1 = 27;
        static constexpr int kP = 28;
        static constexpr int kQ = 29;
        static constexpr int kR = 30;
        static constexpr int kS = 31;
        static constexpr int kSL = 32;
        static constexpr int kT = 33;
    private:
        static void DisableHiddenApiPolicy(const ElfImg* handle, bool application, bool platform);
        static void InitMembersFromRuntime(JavaVM* jvm, const ElfImg* handle);
        static void InitClassLinker(void* runtime, size_t java_vm_offset, const ElfImg* handle, bool has_small_irt);
        static void InitJitCodeCache(void* runtime, size_t java_vm_offset, const ElfImg* handle);

        static size_t OffsetOfJavaVm(bool has_small_irt) {
            if (has_small_irt) {
                return Is64Bit() ? 528 : 0 /* TODO: Calculate offset on 32-bit. Currently force fallback to search memory. */;
            }
            switch (version) {
                case kT:
                case kSL:
                case kS:
                case kR:
                case kQ:
                    return Is64Bit() ? 496 : 288;
                default:
                    FATAL("Unexpected android version %d", version);
            }
        }

        static void (*suspend_vm)();
        static void (*resume_vm)();
        static void (*suspend_all)(void*, const char*, bool);
        static void (*resume_all)(void*);
        static void (*start_gc_critical_section)(void*, void*, art::GcCause, art::CollectorType);
        static void (*end_gc_critical_section)(void*);

        static void* class_linker_;
        static void (*make_visibly_initialized_)(void*, void*, bool);

        static void* jit_code_cache_;
        static void (*move_obsolete_method_)(void*, void*, void*);
        DISALLOW_IMPLICIT_CONSTRUCTORS(Android);
    };

    class ScopedSuspendVM {
    public:
        ScopedSuspendVM(void* self) {
            Android::SuspendVM(this, self, "pine hook method");
        }

        ~ScopedSuspendVM() {
            Android::ResumeVM(this);
        }

    private:
        DISALLOW_COPY_AND_ASSIGN(ScopedSuspendVM);
    };
}

#endif //PINE_ANDROID_H
