//
// Created by canyie on 2020/8/27.
//

#ifndef PINE_X86_H
#define PINE_X86_H

#include "../trampoline_installer.h"

namespace pine {
    class X86TrampolineInstaller final : public TrampolineInstaller {
    public:
        X86TrampolineInstaller() : TrampolineInstaller(0, true) {
        }

    protected:
        virtual void InitTrampolines() override ;
        virtual bool CannotBackup(art::ArtMethod* target, size_t size) override {
            return true;
        }
        virtual void* CreateDirectJumpTrampoline(void* to) override {
            FATAL("CreateDirectJumpTrampoline unimplemented");
        }
        virtual void* CreateCallOriginTrampoline(art::ArtMethod* origin, void* original_code_entry) override {
            FATAL("CreateCallOriginTrampoline unimplemented");
        }
        virtual void* Backup(art::ArtMethod* target, size_t size) override {
            FATAL("Backup unimplemented");
        }
        virtual void FillWithNopImpl(void* target, size_t size) override {
            FATAL("FillWithNop unimplemented");
        }

        virtual bool NativeHookNoBackup(void* target, void* to) override ;

    private:
        void WriteDirectJump(void* target, void* jump_to) {
            *static_cast<uint8_t*>(target) = 0xE9;
            *reinterpret_cast<int32_t*>(reinterpret_cast<uintptr_t>(target) + 1) = GetJmpOffset(target, jump_to);
        }

        int32_t GetJmpOffset(void* source, void* target) {
            // E9 <offset(4 bytes)> = 5 bytes
            return reinterpret_cast<intptr_t>(target) - (reinterpret_cast<intptr_t>(source) + 5);
        }
    };
}

#endif //PINE_X86_H
