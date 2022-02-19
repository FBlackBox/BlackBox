//
// Created by canyie on 2020/5/26.
//
#include "scoped_memory_access_protection.h"

using namespace pine;

#if defined(__aarch64__) || defined(__arm__)

thread_local ScopedMemoryAccessProtection* ScopedMemoryAccessProtection::current = nullptr;

void ScopedMemoryAccessProtection::HandleSignal(int signal, siginfo_t* info, void* reserved) {
    assert(signal == SIGSEGV);
    ucontext_t* context = static_cast<ucontext_t*>(reserved);
    uintptr_t fault_addr = context->uc_mcontext.fault_address;

    if (LIKELY(info->si_code == SEGV_ACCERR)) {
        if (LIKELY(fault_addr >= current->addr && fault_addr <= (current->addr + current->size))) {
            if (LIKELY(current->max_retries-- > 0)) {
                LOGW("Segmentation fault when trying access %p, unprotect it and try again", (void*) fault_addr);
                if (LIKELY(Memory::Unprotect(reinterpret_cast<void*>(fault_addr))))
                    return;
                LOGE("Failed to unprotect fault address…");
            } else {
                LOGE("Retried too many times to access %p", (void*) fault_addr);
            }
        }
    }

    if (current->def.sa_sigaction == nullptr) {
        FATAL("No default signal handler to dispatch SIGSEGV (fault addr %p)", (void*) fault_addr);
    } else {
        current->def.sa_sigaction(signal, info, reserved);
    }
}
#endif
