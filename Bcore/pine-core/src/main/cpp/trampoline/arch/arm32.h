//
// Created by canyie on 2020/3/19.
//

#ifndef PINE_ARM32_H
#define PINE_ARM32_H

#include <cstdint>
#include "../trampoline_installer.h"
#include "../../utils/macros.h"

namespace pine {
    class Arm32TrampolineInstaller final : public TrampolineInstaller {
    public:
        Arm32TrampolineInstaller() : TrampolineInstaller(4) {
        }
    protected:
        virtual void InitTrampolines() override ;
        virtual bool CannotBackup(art::ArtMethod* target, size_t size) override ;
        virtual void FillWithNopImpl(void* target, size_t size) override ;
    private:
        static bool IsPCRelatedInst(uint32_t inst);
    };
}


#endif //PINE_ARM32_H
