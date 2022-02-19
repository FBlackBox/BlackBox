//
// Created by canyie on 2020/5/26.
//

#ifndef PINE_SCOPED_MEMORY_ACCESS_PROTECTION_H
#define PINE_SCOPED_MEMORY_ACCESS_PROTECTION_H

#include <cstdint>
#include <cassert>
#include <signal.h>
#include "macros.h"
#include "log.h"
#include "memory.h"

namespace pine {
    class ScopedMemoryAccessProtection {
    public:
#if defined(__aarch64__) || defined(__arm__)
        ScopedMemoryAccessProtection(void* addr, size_t size, uint32_t max_retries = 2) :
                addr(reinterpret_cast<uintptr_t>(addr)), size(size), max_retries(max_retries) {
            assert(current == nullptr);
            current = this;
            struct sigaction my;
            my.sa_sigaction = HandleSignal;
            my.sa_flags = SA_SIGINFO;
            sigaction(SIGSEGV, &my, &def);
        }

        ~ScopedMemoryAccessProtection() {
            sigaction(SIGSEGV, &def, nullptr);
            current = nullptr;
        }
#else
        ScopedMemoryAccessProtection(void* addr, size_t size, uint32_t max_retries = 2) {
        }

        ~ScopedMemoryAccessProtection() {
        }
#endif
    private:
#if defined(__aarch64__) || defined(__arm__)
        static void HandleSignal(int signal, siginfo_t* info, void* reserved);

        static thread_local ScopedMemoryAccessProtection* current;

        uintptr_t addr;
        size_t size;
        uint32_t max_retries;
        struct sigaction def;
#endif
        DISALLOW_COPY_AND_ASSIGN(ScopedMemoryAccessProtection);
    };

}

#endif //PINE_SCOPED_MEMORY_ACCESS_PROTECTION_H
