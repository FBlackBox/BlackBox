//
// Created by canyie on 2020/3/19.
//

#ifndef PINE_TRAMPOLINE_INSTALLER_H
#define PINE_TRAMPOLINE_INSTALLER_H

#include "../utils/macros.h"
#include "../art/art_method.h"
#include "arch/trampolines.h"

#define INST_CASE(mask, op) \
if (UNLIKELY(((inst) & (mask)) == op)) return true

#define AS_SIZE_T(value) (reinterpret_cast<size_t>(value))
#define AS_VOID_PTR(value) (reinterpret_cast<void*>(value))
#define AS_PTR_NUM(value) (reinterpret_cast<uintptr_t>(value))
#define PTR_SIZE (sizeof(void*))

namespace pine {
    class TrampolineInstaller {
    public:
        static TrampolineInstaller* GetOrInitDefault();

        static TrampolineInstaller* GetDefault() {
            return default_;
        }

        TrampolineInstaller(size_t skip_bytes) : kSkipBytes(skip_bytes), kReplacementModeOnly(false) {
        };

        TrampolineInstaller(size_t skip_bytes, bool replacement_only) : kSkipBytes(skip_bytes), kReplacementModeOnly(replacement_only) {
        }

        void Init() {
            InitTrampolines();
            kBridgeJumpTrampolineSize = SubAsSize(kCallOriginTrampoline, kBridgeJumpTrampoline);
            kCallOriginTrampolineSize = SubAsSize(kBackupTrampoline, kCallOriginTrampoline);
            kBackupTrampolineSize = SubAsSize(kTrampolinesEnd, kBackupTrampoline);
        }

        bool IsReplacementOnly() {
            return kReplacementModeOnly;
        }

        bool CannotSafeInlineHook(size_t size) {
            return size < kDirectJumpTrampolineSize;
        }

        bool CannotSafeInlineHook(art::ArtMethod* target) {
            size_t target_code_size = target->GetCompiledCodeSize();
            if (UNLIKELY(CannotSafeInlineHook(target_code_size))) {
                LOGW("Cannot safe inline hook method: code size of target method too small (size %u)!",
                     target_code_size);
                return true;
            }
            if (UNLIKELY(CannotBackup(target, kDirectJumpTrampolineSize))) {
                LOGW("Cannot safe inline hook method: code of target method has pc register related instruction!");
                return true;
            }
            return false;
        }

        bool CanSkipFirstFewBytes(art::ArtMethod* target) {
            size_t target_code_size = target->GetCompiledCodeSize();
            size_t size = kDirectJumpTrampolineSize + kSkipBytes;
            if (UNLIKELY(target_code_size < size)) {
                LOGW("Cannot safe inline hook method and skip first few bytes: "
                     "code size of target method too small (size %u)!",
                     target_code_size);
                return false;
            }
            if (UNLIKELY(CannotBackup(target, size))) {
                LOGW("Cannot safe inline hook method and skip first few bytes: "
                     "code of target method has pc register related instruction!");
                return false;
            }
            return true;
        }

        void* InstallReplacementTrampoline(art::ArtMethod* target, art::ArtMethod* bridge);

        void* InstallInlineTrampoline(art::ArtMethod* target, art::ArtMethod* bridge, bool skip_first_few_bytes);

        virtual bool NativeHookNoBackup(void* target, void* to);

        bool FillWithNop(void* target, size_t size);

    protected:
        static inline size_t SubAsSize(void const* a, void const* b) {
            return AS_SIZE_T(reinterpret_cast<uintptr_t>(a) - reinterpret_cast<uintptr_t>(b));
        }

        inline size_t DirectJumpTrampolineOffset(void* ptr) {
            return SubAsSize(ptr, kDirectJumpTrampoline);
        }

        inline size_t BridgeJumpTrampolineOffset(void* ptr) {
            return SubAsSize(ptr, kBridgeJumpTrampoline);
        }

        inline size_t CallOriginTrampolineOffset(void* ptr) {
            return SubAsSize(ptr, kCallOriginTrampoline);
        }

        inline size_t BackupTrampolineOffset(void* ptr) {
            return SubAsSize(ptr, kBackupTrampoline);
        }

        virtual void InitTrampolines() = 0;

        virtual void* CreateDirectJumpTrampoline(void* to);

        void WriteDirectJumpTrampolineTo(void* mem, void* jump_to);

        virtual void* CreateBridgeJumpTrampoline(art::ArtMethod* target, art::ArtMethod* bridge,
                                                 void* origin_code_entry);

        virtual void* CreateCallOriginTrampoline(art::ArtMethod* origin, void* original_code_entry);

        virtual bool CannotBackup(art::ArtMethod* target, size_t size) = 0;

        virtual void* Backup(art::ArtMethod* target, size_t size);

        virtual void FillWithNopImpl(void* target, size_t size) = 0;

        static TrampolineInstaller* default_;

        const bool kReplacementModeOnly;

        void const* kDirectJumpTrampoline;
        size_t kDirectJumpTrampolineEntryOffset;
        size_t kDirectJumpTrampolineSize;

        void const* kBridgeJumpTrampoline;
        size_t kBridgeJumpTrampolineTargetMethodOffset;
        size_t kBridgeJumpTrampolineExtrasOffset;
        size_t kBridgeJumpTrampolineBridgeMethodOffset;
        size_t kBridgeJumpTrampolineBridgeEntryOffset;
        size_t kBridgeJumpTrampolineOriginCodeEntryOffset;
        size_t kBridgeJumpTrampolineSize;

        void const* kCallOriginTrampoline;
        size_t kCallOriginTrampolineOriginMethodOffset;
        size_t kCallOriginTrampolineOriginalEntryOffset;
        size_t kCallOriginTrampolineSize;

        void const* kBackupTrampoline;
        size_t kBackupTrampolineOverrideSpaceOffset;
        size_t kBackupTrampolineOriginMethodOffset;
        size_t kBackupTrampolineRemainingCodeEntryOffset;
        size_t kBackupTrampolineSize;

        void const* kTrampolinesEnd;

        size_t kSkipBytes;
    private:
        DISALLOW_COPY_AND_ASSIGN(TrampolineInstaller);
    };
}


#endif //PINE_TRAMPOLINE_INSTALLER_H
