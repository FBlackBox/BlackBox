//
// Created by canyie on 2020/4/7.
//

#ifndef PINE_ARM64_H
#define PINE_ARM64_H

#include <cstdint>
#include "../trampoline_installer.h"

namespace pine {
    class Arm64TrampolineInstaller final : public TrampolineInstaller {
    public:
        Arm64TrampolineInstaller() : TrampolineInstaller(8) {
        }
    protected:
        virtual void InitTrampolines() override ;
        virtual bool CannotBackup(art::ArtMethod* target, size_t size) override ;
        virtual void FillWithNopImpl(void* target, size_t size) override ;
    private:
        static bool IsPCRelatedInst(uint32_t inst);
    };
}

#endif //PINE_ARM64_H
