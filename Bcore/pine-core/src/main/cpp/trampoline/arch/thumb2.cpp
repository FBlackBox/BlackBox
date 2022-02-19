//
// Created by canyie on 2020/3/23.
//

#include "thumb2.h"
#include "../extras.h"
#include "../../utils/memory.h"

using namespace pine;

void Thumb2TrampolineInstaller::InitTrampolines() {
    kDirectJumpTrampoline = ToAddress(AS_VOID_PTR(pine_thumb_direct_jump_trampoline));
    kDirectJumpTrampolineEntryOffset = DirectJumpTrampolineOffset(
            ToAddress(AS_VOID_PTR(pine_thumb_direct_jump_trampoline_jump_entry)));

    kBridgeJumpTrampoline = ToAddress(AS_VOID_PTR(pine_thumb_bridge_jump_trampoline));
    kBridgeJumpTrampolineTargetMethodOffset = BridgeJumpTrampolineOffset(
            ToAddress(AS_VOID_PTR(pine_thumb_bridge_jump_trampoline_target_method)));
    kBridgeJumpTrampolineExtrasOffset = BridgeJumpTrampolineOffset(
            ToAddress(AS_VOID_PTR(pine_thumb_bridge_jump_trampoline_extras)));
    kBridgeJumpTrampolineBridgeMethodOffset = BridgeJumpTrampolineOffset(
            ToAddress(AS_VOID_PTR(pine_thumb_bridge_jump_trampoline_bridge_method)));
    kBridgeJumpTrampolineBridgeEntryOffset = BridgeJumpTrampolineOffset(
            ToAddress(AS_VOID_PTR(pine_thumb_bridge_jump_trampoline_bridge_entry)));
    kBridgeJumpTrampolineOriginCodeEntryOffset = BridgeJumpTrampolineOffset(
            ToAddress(AS_VOID_PTR(pine_thumb_bridge_jump_trampoline_call_origin_entry)));

    kCallOriginTrampoline = ToAddress(AS_VOID_PTR(pine_thumb_call_origin_trampoline));
    kCallOriginTrampolineOriginMethodOffset = CallOriginTrampolineOffset(
            ToAddress(AS_VOID_PTR(pine_thumb_call_origin_trampoline_origin_method)));
    kCallOriginTrampolineOriginalEntryOffset = CallOriginTrampolineOffset(
            ToAddress(AS_VOID_PTR(pine_thumb_call_origin_trampoline_origin_code_entry)));

    kBackupTrampoline = ToAddress(AS_VOID_PTR(pine_thumb_backup_trampoline));
    kBackupTrampolineOriginMethodOffset = BackupTrampolineOffset(
            AS_VOID_PTR(pine_thumb_backup_trampoline_origin_method));
    kBackupTrampolineOverrideSpaceOffset = BackupTrampolineOffset(
            AS_VOID_PTR(pine_thumb_backup_trampoline_override_space));
    kBackupTrampolineRemainingCodeEntryOffset = BackupTrampolineOffset(
            AS_VOID_PTR(pine_thumb_backup_trampoline_remaining_code_entry));

    kTrampolinesEnd = ToAddress(AS_VOID_PTR(pine_thumb_trampolines_end));

    kDirectJumpTrampolineSize = 8; // Hardcoded size of pine_thumb_direct_jump_trampoline now :)
}

bool Thumb2TrampolineInstaller::IsThumb32PCRelatedInst(uint32_t inst) {
    INST_CASE(0xF800D000, 0xF800D000); // blx <label>
    INST_CASE(0xF800D000, 0xF000D000); // bl <label>
    INST_CASE(0xF800D000, 0xF0008000); // b.w <label>
    INST_CASE(0xF800D000, 0xF0009000); // b.w <label>
    INST_CASE(0xFBFF8000, 0xF2AF0000); // adr.w Rd, <label>
    INST_CASE(0xFBFF8000, 0xF20F0000); // adr.w Rd, <label>
    INST_CASE(0xFF7F0000, 0xF85F0000); // ldr.w Rt, <label>
    INST_CASE(0xFFFF00F0, 0xE8DF0000); // tbb [pc, Rm]
    INST_CASE(0xFFFF00F0, 0xE8DF0010); // tbh [pc, Rm, LSL #1]
    return false;
}

bool Thumb2TrampolineInstaller::IsThumb16PCRelatedInst(uint16_t inst) {
    INST_CASE(0xF000, 0xD000); // b <label>
    INST_CASE(0xF800, 0xE000); // b <label>
    INST_CASE(0xFFF8, 0x4778); // bx Rn
    INST_CASE(0xFF78, 0x4478); // add <Rdn>, pc (Rd != pc, Rn != pc)
    INST_CASE(0xFF78, 0x4678); // mov Rd, pc
    INST_CASE(0xF800, 0xA000); // adr Rd, <label>
    INST_CASE(0xF800, 0x4800); // ldr Rt, <label>
    INST_CASE(0xF500, 0xB100); // cb{n}z <Rn>, <label>
    return false;
}

bool Thumb2TrampolineInstaller::CannotBackup(art::ArtMethod* target, size_t size) {
    uintptr_t entry = reinterpret_cast<uintptr_t>(target->GetCompiledCodeAddr());
    uint32_t index = 0;
    while (index < size) {
        uint16_t* ptr16 = reinterpret_cast<uint16_t*>(entry + index);
        uint32_t* ptr32 = reinterpret_cast<uint32_t*>(entry + index);
        if (LIKELY(IsThumb32(*ptr16))) {
            if (UNLIKELY(IsThumb32PCRelatedInst(*ptr32))) {
                return true;
            }
            index += 4;
        } else {
            if (UNLIKELY(IsThumb16PCRelatedInst(*ptr16))) {
                return true;
            }
            index += 2;
        }
    }
    return false;
}

size_t Thumb2TrampolineInstaller::GetBackupCodeSize(art::ArtMethod* target, size_t min_size) {
    uintptr_t entry = reinterpret_cast<uintptr_t>(target->GetCompiledCodeAddr());
    size_t size = 0;
    while (size < min_size) {
        if (LIKELY(IsThumb32(*reinterpret_cast<uint16_t*>(entry + size)))) {
            size += 4;
        } else {
            size += 2;
        }
    }
    return size;
}

void* Thumb2TrampolineInstaller::Backup(art::ArtMethod* target, size_t size) {
    void* mem = Memory::AllocUnprotected(kBackupTrampolineSize);
    if (UNLIKELY(!mem)) {
        LOGE("Failed to allocate executable memory for backup!");
        return nullptr;
    }
    size_t backup_size = GetBackupCodeSize(target, size);
    memcpy(mem, kBackupTrampoline, kBackupTrampolineSize);

    uintptr_t addr = reinterpret_cast<uintptr_t>(mem);

    auto origin_out = reinterpret_cast<art::ArtMethod**>(addr + kBackupTrampolineOriginMethodOffset);
    *origin_out = target;

    void* target_addr = target->GetCompiledCodeAddr();
    memcpy(AS_VOID_PTR(addr + kBackupTrampolineOverrideSpaceOffset), target_addr, backup_size);

    if (LIKELY(target->GetCompiledCodeSize() != backup_size)) {
        // has remaining code
        void** remaining_out = reinterpret_cast<void**>(addr + kBackupTrampolineRemainingCodeEntryOffset);
        *remaining_out = ToPC(AS_VOID_PTR(reinterpret_cast<uintptr_t>(target_addr) + backup_size));
    }

    Memory::FlushCache(mem, kBackupTrampolineSize);
    return ToPC(mem);
}

void Thumb2TrampolineInstaller::FillWithNopImpl(void* target, size_t size) {
    uintptr_t entry = reinterpret_cast<uintptr_t>(target);
    for (uint index = 0;index < size;index += sizeof(uint16_t)) {
        uint16_t* p = reinterpret_cast<uint16_t*>(entry + index);
        *p = 0xbf00; // nop, android only use little-endian
    }
}
