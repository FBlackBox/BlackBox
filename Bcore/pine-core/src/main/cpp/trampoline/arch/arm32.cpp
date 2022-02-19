//
// Created by canyie on 2020/3/19.
//

#include "arm32.h"

using namespace pine;

void Arm32TrampolineInstaller::InitTrampolines() {
    TrampolineInstaller::InitTrampolines();
    kDirectJumpTrampolineSize = 8;
}

bool Arm32TrampolineInstaller::IsPCRelatedInst(uint32_t inst) {
    INST_CASE(0xFE000000, 0xFA000000); // blx <label>
    INST_CASE(0x0F000000, 0x0B000000); // bl <label>
    INST_CASE(0xFE000000, 0xEA000000); // b <label>
    INST_CASE(0x0FF000FF, 0x0120001F); // bx Rn
    INST_CASE(0x0FEF0010, 0x008F0000); // add <Rd>, pc, Rm (Rd != pc, Rn != pc)
    INST_CASE(0x0FFF0000, 0x028F0000); // adr Rd, <label>
    INST_CASE(0x0FFF0000, 0x024F0000); // adr Rd, <label>
    INST_CASE(0x0E5F0000, 0x041F0000); // ldr Rt, <label>
    INST_CASE(0x0FE00FFF, 0x01A0000F); // mov Rd, pc
    return false;
}

bool Arm32TrampolineInstaller::CannotBackup(art::ArtMethod* target, size_t size) {
    uintptr_t entry = reinterpret_cast<uintptr_t>(target->GetEntryPointFromCompiledCode());
    for (uint32_t index = 0;index < size;index += 4) {
        uint32_t* p = reinterpret_cast<uint32_t*>(entry + index);
        if (UNLIKELY(IsPCRelatedInst(*p))) {
            return true;
        }
    }
    return false;
}

void Arm32TrampolineInstaller::FillWithNopImpl(void* target, size_t size) {
    uintptr_t entry = reinterpret_cast<uintptr_t>(target);
    for (uint32_t index = 0;index < size;index += sizeof(uint32_t)) {
        uint32_t* p = reinterpret_cast<uint32_t*>(entry + index);
        *p = 0xe320f000; // nop, android only use little-endian
    }
}
