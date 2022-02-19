//
// Created by canyie on 2020/3/23.
//

#ifndef PINE_THUMB2_H
#define PINE_THUMB2_H

#include "trampolines.h"
#include "../trampoline_installer.h"

namespace pine {
    class Thumb2TrampolineInstaller final : public TrampolineInstaller {
    public:
        Thumb2TrampolineInstaller() : TrampolineInstaller(4) {
        }
    protected:
        virtual void InitTrampolines() override;

        virtual bool CannotBackup(art::ArtMethod* target, size_t size) override;

        virtual void* CreateDirectJumpTrampoline(void* to) override {
            return ToPC(TrampolineInstaller::CreateDirectJumpTrampoline(to));
        }

        virtual void* CreateBridgeJumpTrampoline(art::ArtMethod* target, art::ArtMethod* bridge, void* origin_code_entry) override {
            return ToPC(TrampolineInstaller::CreateBridgeJumpTrampoline(target, bridge, origin_code_entry));
        }

        virtual void* CreateCallOriginTrampoline(art::ArtMethod* origin, void* original_code_entry) override {
            return ToPC(TrampolineInstaller::CreateCallOriginTrampoline(origin, original_code_entry));
        }

        virtual void* Backup(art::ArtMethod* target, size_t size) override;

        virtual bool NativeHookNoBackup(void* target, void* to) override {
            return TrampolineInstaller::NativeHookNoBackup(ToAddress(target), to);
        }

    private:
        static inline bool IsThumb32(uint16_t inst) {
            return ((inst & 0xF000) == 0xF000) || ((inst & 0xF800) == 0xE800);
        }

        static inline void* ToPC(void* addr) {
            return AS_VOID_PTR(reinterpret_cast<uintptr_t>(addr) | 1);
        }

        static inline void* ToAddress(void* pc) {
            return AS_VOID_PTR(reinterpret_cast<uintptr_t>(pc) & ~1);
        }

        static bool IsThumb32PCRelatedInst(uint32_t inst);

        static bool IsThumb16PCRelatedInst(uint16_t inst);

        size_t GetBackupCodeSize(art::ArtMethod* target, size_t min_size);

        virtual void FillWithNopImpl(void* target, size_t size) override;
    };
}

#endif //PINE_THUMB2_H
