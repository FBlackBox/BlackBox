//
// Created by canyie on 2020/3/19.
//

#include "trampoline_installer.h"
#include "extras.h"
#include "../pine_config.h"
#include "../utils/memory.h"
#include "../utils/scoped_memory_access_protection.h"

#ifdef __aarch64__
#include "arch/arm64.h"
#elif defined(__arm__)
#include "arch/thumb2.h"
#elif defined(__i386__)
#include "arch/x86.h"
#else
#error unsupported architecture
#endif

using namespace pine;

TrampolineInstaller* TrampolineInstaller::default_ = nullptr;

TrampolineInstaller* TrampolineInstaller::GetOrInitDefault() {
    if (default_ == nullptr) {
#ifdef __aarch64__
        default_ = new Arm64TrampolineInstaller;
#elif defined(__arm__)
        default_ = new Thumb2TrampolineInstaller;
#elif defined(__i386__)
        default_ = new X86TrampolineInstaller;
#endif
        default_->Init();
    }
    return default_;
}

void* TrampolineInstaller::CreateDirectJumpTrampoline(void* to) {
    void* mem = Memory::AllocUnprotected(kDirectJumpTrampolineSize);
    if (UNLIKELY(!mem)) {
        LOGE("Failed to allocate direct jump trampoline!");
        return nullptr;
    }
    WriteDirectJumpTrampolineTo(mem, to);
    return mem;
}

void TrampolineInstaller::WriteDirectJumpTrampolineTo(void* mem, void* jump_to) {
    memcpy(mem, kDirectJumpTrampoline, kDirectJumpTrampolineSize);
    void* to_out = AS_VOID_PTR(reinterpret_cast<uintptr_t>(mem) + kDirectJumpTrampolineEntryOffset);
    memcpy(to_out, &jump_to, PTR_SIZE);
    Memory::FlushCache(mem, kDirectJumpTrampolineSize);
}

void*
TrampolineInstaller::CreateBridgeJumpTrampoline(art::ArtMethod* target, art::ArtMethod* bridge,
                                                void* origin_code_entry) {
    void* mem = Memory::AllocUnprotected(kBridgeJumpTrampolineSize);
    if (UNLIKELY(!mem)) {
        LOGE("Failed to allocate bridge jump trampoline!");
        return nullptr;
    }
    memcpy(mem, kBridgeJumpTrampoline, kBridgeJumpTrampolineSize);
    uintptr_t addr = reinterpret_cast<uintptr_t>(mem);

    auto target_out = reinterpret_cast<art::ArtMethod**>(addr +
                                                         kBridgeJumpTrampolineTargetMethodOffset);
    *target_out = target;

    auto extras_out = reinterpret_cast<Extras**>(addr + kBridgeJumpTrampolineExtrasOffset);
    *extras_out = new Extras;

    auto bridge_out = reinterpret_cast<art::ArtMethod**>(addr +
                                                         kBridgeJumpTrampolineBridgeMethodOffset);
    *bridge_out = bridge;

    auto bridge_entry_out = reinterpret_cast<void**>(addr + kBridgeJumpTrampolineBridgeEntryOffset);
    *bridge_entry_out = bridge->GetEntryPointFromCompiledCode();

    auto origin_entry_out = reinterpret_cast<void**>(addr +
                                                     kBridgeJumpTrampolineOriginCodeEntryOffset);
    *origin_entry_out = origin_code_entry;

    Memory::FlushCache(mem, kBridgeJumpTrampolineSize);

    return mem;
}

void*
TrampolineInstaller::CreateCallOriginTrampoline(art::ArtMethod* origin, void* original_code_entry) {
    void* mem = Memory::AllocUnprotected(kCallOriginTrampolineSize);
    if (UNLIKELY(!mem)) {
        LOGE("Failed to allocate call origin trampoline!");
        return nullptr;
    }
    memcpy(mem, kCallOriginTrampoline, kCallOriginTrampolineSize);
    uintptr_t addr = reinterpret_cast<uintptr_t>(mem);

    auto origin_method_out = reinterpret_cast<art::ArtMethod**>(addr +
                                                                kCallOriginTrampolineOriginMethodOffset);
    *origin_method_out = origin;

    void** original_code_entry_out = reinterpret_cast<void**>(addr +
                                                              kCallOriginTrampolineOriginalEntryOffset);
    *original_code_entry_out = original_code_entry;

    Memory::FlushCache(mem, kCallOriginTrampolineSize);
    return mem;
}

void* TrampolineInstaller::Backup(art::ArtMethod* target, size_t size) {
    void* mem = Memory::AllocUnprotected(kBackupTrampolineSize);
    if (UNLIKELY(!mem)) {
        LOGE("Failed to allocate executable memory for backup!");
        return nullptr;
    }
    memcpy(mem, kBackupTrampoline, kBackupTrampolineSize);
    uintptr_t addr = reinterpret_cast<uintptr_t>(mem);

    auto origin_out = reinterpret_cast<art::ArtMethod**>(addr +
                                                         kBackupTrampolineOriginMethodOffset);
    *origin_out = target;

    void* target_addr = target->GetEntryPointFromCompiledCode();
    memcpy(AS_VOID_PTR(addr + kBackupTrampolineOverrideSpaceOffset), target_addr, size);

    if (LIKELY(target->GetCompiledCodeSize() != size)) {
        // has remaining code
        void** remaining_out = reinterpret_cast<void**>(addr +
                                                        kBackupTrampolineRemainingCodeEntryOffset);
        *remaining_out = AS_VOID_PTR(reinterpret_cast<uintptr_t>(target_addr) + size);
    }

    Memory::FlushCache(mem, kBackupTrampolineSize);
    return mem;
}

void*
TrampolineInstaller::InstallReplacementTrampoline(art::ArtMethod* target, art::ArtMethod* bridge) {
    void* origin_code_entry = target->GetEntryPointFromCompiledCode();
    void* bridge_jump_trampoline = CreateBridgeJumpTrampoline(target, bridge, origin_code_entry);
    if (UNLIKELY(!bridge_jump_trampoline)) return nullptr;

    // Unknown bug:
    // After setting the r0 register to the original method, if the original method needs to be
    // traced back to the call stack (such as an exception), the thread will become a zombie thread
    // and there will be no response. Just set origin code entry and don't create call_origin_trampoline
    // to set r0 register to avoid it.

    // void *call_origin_trampoline = CreateCallOriginTrampoline(target, origin_code_entry);
    // if (UNLIKELY(!call_origin_trampoline)) return nullptr;

    target->SetEntryPointFromCompiledCode(bridge_jump_trampoline);
    // return call_origin_trampoline;

    if (PineConfig::debug)
        LOGD("InstallReplacementTrampoline: origin %p origin_entry %p bridge_jump %p",
                target, origin_code_entry, bridge_jump_trampoline);

    return origin_code_entry;
}

void* TrampolineInstaller::InstallInlineTrampoline(art::ArtMethod* target, art::ArtMethod* bridge,
                                                   bool skip_first_few_bytes) {
    void* target_code_addr = target->GetCompiledCodeAddr();
    bool target_code_writable = Memory::Unprotect(target_code_addr);
    if (UNLIKELY(!target_code_writable)) {
        LOGE("Failed to make target code writable!");
        return nullptr;
    }

    size_t backup_size = kDirectJumpTrampolineSize;
    if (skip_first_few_bytes) backup_size += kSkipBytes;

    void* backup = Backup(target, backup_size);
    if (UNLIKELY(!backup)) return nullptr;

    void* bridge_jump_trampoline = CreateBridgeJumpTrampoline(target, bridge, backup);
    if (UNLIKELY(!bridge_jump_trampoline)) return nullptr;

    {
        ScopedMemoryAccessProtection protection(target_code_addr, kDirectJumpTrampolineSize);
        if (skip_first_few_bytes) {
            FillWithNopImpl(target_code_addr, kSkipBytes);
            WriteDirectJumpTrampolineTo(AS_VOID_PTR(AS_PTR_NUM(target_code_addr) + kSkipBytes),
                    bridge_jump_trampoline);
        } else {
            WriteDirectJumpTrampolineTo(target_code_addr, bridge_jump_trampoline);
        }
    }

    if (PineConfig::debug)
        LOGD("InstallInlineTrampoline: target_code_addr %p backup %p bridge_jump %p",
                target_code_addr, backup, bridge_jump_trampoline);

    return backup;
}

bool TrampolineInstaller::NativeHookNoBackup(void* target, void* to) {
    bool target_code_writable = Memory::Unprotect(target);
    if (UNLIKELY(!target_code_writable)) {
        LOGE("Failed to make target code %p writable!", target);
        return false;
    }

    {
        ScopedMemoryAccessProtection protection(target, kDirectJumpTrampolineSize);
        WriteDirectJumpTrampolineTo(target, to);
    }
    return true;
}

bool TrampolineInstaller::FillWithNop(void* target, size_t size) {
    bool target_code_writable = Memory::Unprotect(target);
    if (UNLIKELY(!target_code_writable)) {
        LOGE("Failed to make target code %p writable!", target);
        return false;
    }

    {
        ScopedMemoryAccessProtection protection(target, size);
        FillWithNopImpl(target, size);
    }
    return true;
}
