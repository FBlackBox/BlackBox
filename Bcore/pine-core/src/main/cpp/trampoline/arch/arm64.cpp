//
// Created by canyie on 2020/4/7.
//

#include "arm64.h"

using namespace pine;

void Arm64TrampolineInstaller::InitTrampolines() {
    kDirectJumpTrampoline = AS_VOID_PTR(pine_direct_jump_trampoline);
    kDirectJumpTrampolineEntryOffset = DirectJumpTrampolineOffset(
            AS_VOID_PTR(pine_direct_jump_trampoline_jump_entry));

    kBridgeJumpTrampoline = AS_VOID_PTR(pine_bridge_jump_trampoline);
    kBridgeJumpTrampolineTargetMethodOffset = BridgeJumpTrampolineOffset(
            AS_VOID_PTR(pine_bridge_jump_trampoline_target_method));
    kBridgeJumpTrampolineExtrasOffset = BridgeJumpTrampolineOffset(
            AS_VOID_PTR(pine_bridge_jump_trampoline_extras));
    kBridgeJumpTrampolineBridgeMethodOffset = BridgeJumpTrampolineOffset(
            AS_VOID_PTR(pine_bridge_jump_trampoline_bridge_method));
    kBridgeJumpTrampolineBridgeEntryOffset = BridgeJumpTrampolineOffset(
            AS_VOID_PTR(pine_bridge_jump_trampoline_bridge_entry));
    kBridgeJumpTrampolineOriginCodeEntryOffset = BridgeJumpTrampolineOffset(
            AS_VOID_PTR(pine_bridge_jump_trampoline_call_origin_entry));

    kCallOriginTrampoline = AS_VOID_PTR(pine_call_origin_trampoline);
    kCallOriginTrampolineOriginMethodOffset = CallOriginTrampolineOffset(
            AS_VOID_PTR(pine_call_origin_trampoline_origin_method));
    kCallOriginTrampolineOriginalEntryOffset = CallOriginTrampolineOffset(
            AS_VOID_PTR(pine_call_origin_trampoline_origin_code_entry));

    kBackupTrampoline = AS_VOID_PTR(pine_backup_trampoline);
    kBackupTrampolineOriginMethodOffset = BackupTrampolineOffset(
            AS_VOID_PTR(pine_backup_trampoline_origin_method));
    kBackupTrampolineOverrideSpaceOffset = BackupTrampolineOffset(
            AS_VOID_PTR(pine_backup_trampoline_override_space));
    kBackupTrampolineRemainingCodeEntryOffset = BackupTrampolineOffset(
            AS_VOID_PTR(pine_backup_trampoline_remaining_code_entry));

    kTrampolinesEnd = AS_VOID_PTR(pine_trampolines_end);

    kDirectJumpTrampolineSize = 16;
}

bool Arm64TrampolineInstaller::IsPCRelatedInst(uint32_t inst) {
    INST_CASE(0xff000010, 0x54000000); // b <label>
    INST_CASE(0x7c000000, 0x14000000); // bl <label>
    INST_CASE(0x7e000000, 0x34000000); // cb{n}z Rn, <label>
    INST_CASE(0x7e000000, 0x36000000); // tb{n}z Rt, #<imm>, <label>
    INST_CASE(0x3b000000, 0x18000000); // ldr
    INST_CASE(0x1f000000, 0x10000000); // adr/adrp Rd, <label>
    return false;
}

bool Arm64TrampolineInstaller::CannotBackup(art::ArtMethod* target, size_t size) {
    uintptr_t entry = reinterpret_cast<uintptr_t>(target->GetEntryPointFromCompiledCode());
    for (uint32_t index = 0;index < size;index += 4) {
        uint32_t* p = reinterpret_cast<uint32_t*>(entry + index);
        if (UNLIKELY(IsPCRelatedInst(*p))) {
            return true;
        }
    }
    return false;
}

void Arm64TrampolineInstaller::FillWithNopImpl(void* target, size_t size) {
    uintptr_t entry = reinterpret_cast<uintptr_t>(target);
    for (uint32_t index = 0;index < size;index += sizeof(uint32_t)) {
        uint32_t* p = reinterpret_cast<uint32_t*>(entry + index);
        *p = 0xd503201f; // nop, android only use little-endian
    }
}
