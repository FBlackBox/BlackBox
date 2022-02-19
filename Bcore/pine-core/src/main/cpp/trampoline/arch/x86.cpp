//
// Created by canyie on 2020/8/28.
//

#include "x86.h"
#include "../../utils/memory.h"
#include "../../utils/scoped_memory_access_protection.h"

using namespace pine;

// FIXME: bad arguments & return value and app crash when returning from the hooked method
static const unsigned char bridge_jump_trampoline[] = {
        0x57, // push edi
        0xbf, 0x00, 0x00, 0x00, 0x00, // mov edi, 0x00000000 (address of the hooked method)
        0x39, 0xf8, // cmp eax, edi
        0x75, 0x1e, // jne jump_to_original
        0xbf, 0x00, 0x00, 0x00, 0x00, // mov edi, 0x00000000 (address of the pre-allocated extras)
        0x89, 0x4f, 0x04, // mov DWORD PTR [edi + 4], ecx
        0x89, 0x57, 0x08, // mov DWORD PTR [edi + 8], edx
        0x89, 0x67, 0x0c, // mov DWORD PTR [edi + 12], esp
        0x89, 0xc1, // mov ecx, eax
        0x89, 0xfa, // mov edx, edi
        0x5f, // pop edi
        0xb8, 0x00, 0x00, 0x00, 0x00, // mov eax, 0x00000000 (address of the bridge method)
        0x68, 0x00, 0x00, 0x00, 0x00, // push 0x00000000 (entry of the bridge method)
        0xc3, // ret
        // jump_to_original:
        0x5f, // pop edi
        0x68, 0x00, 0x00, 0x00, 0x00, // push 0x00000000 (original method entry)
        0xc3 // ret
};

void X86TrampolineInstaller::InitTrampolines() {
    kBridgeJumpTrampoline = AS_VOID_PTR(const_cast<unsigned char*>(bridge_jump_trampoline));
    kBridgeJumpTrampolineTargetMethodOffset = 2;
    kBridgeJumpTrampolineExtrasOffset = kBridgeJumpTrampolineTargetMethodOffset + 9;
    kBridgeJumpTrampolineBridgeMethodOffset = kBridgeJumpTrampolineExtrasOffset + 19;
    kBridgeJumpTrampolineBridgeEntryOffset = kBridgeJumpTrampolineBridgeMethodOffset + 5;
    kBridgeJumpTrampolineOriginCodeEntryOffset = kBridgeJumpTrampolineBridgeEntryOffset + 7;

    kTrampolinesEnd = AS_VOID_PTR(AS_PTR_NUM(bridge_jump_trampoline) + Memory::AlignUp<uintptr_t>(sizeof(bridge_jump_trampoline), 4)); // For calculate size only

    kCallOriginTrampoline = kTrampolinesEnd; // For calculate size only

    kDirectJumpTrampoline = kBackupTrampoline; // For calculate size only
    kDirectJumpTrampolineSize = 0;
    kCallOriginTrampolineOriginMethodOffset = 0;
    kCallOriginTrampolineOriginalEntryOffset = 0;

    kBackupTrampoline = kCallOriginTrampoline; // For calculate size only
    kBackupTrampolineOriginMethodOffset = 0;
    kBackupTrampolineOverrideSpaceOffset = 0;
    kBackupTrampolineRemainingCodeEntryOffset = 0 ;
}

bool X86TrampolineInstaller::NativeHookNoBackup(void* target, void* to) {
    bool target_code_writable = Memory::Unprotect(target);
    if (UNLIKELY(!target_code_writable)) {
        LOGE("Failed to make target code %p writable!", target);
        return false;
    }

    {
        //ScopedMemoryAccessProtection protection(target, kDirectJumpTrampolineSize);
        WriteDirectJump(target, to);
    }
    return true;
}

