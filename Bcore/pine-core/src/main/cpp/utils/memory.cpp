//
// Created by canyie on 2020/3/11.
//

#include <sys/user.h>
#include <sys/mman.h>
#include <sys/prctl.h>
#include <bits/sysconf.h>
#include "memory.h"
#include "lock.h"
#include "../pine_config.h"

using namespace pine;

const size_t Memory::page_size = static_cast<const size_t>(sysconf(_SC_PAGESIZE));

uintptr_t Memory::address = 0;
size_t Memory::offset = 0;
std::mutex Memory::mutex;

void* Memory::AllocUnprotected(size_t size) {
    if (UNLIKELY(size > page_size)) {
        LOGE("Attempting to allocate too much memory space (%zx bytes)", size);
        errno = ENOMEM;
        return nullptr;
    }

    ScopedLock lock(mutex);

    if (LIKELY(address)) {
        size_t next_offset = offset + size;
        if (LIKELY(next_offset <= page_size)) {
            void* ptr = reinterpret_cast<void*>(address + offset);
            offset = next_offset;
            return ptr;
        }
    }

    void* mapped = mmap(nullptr, page_size, PROT_READ | PROT_WRITE | PROT_EXEC, MAP_ANONYMOUS | MAP_PRIVATE, -1, 0);

    if (UNLIKELY(mapped == MAP_FAILED)) {
        LOGE("Unable to allocate executable memory: %s (%d)", strerror(errno), errno);
        return nullptr;
    }
    if (PineConfig::debug)
        LOGD("Mapped new memory %p (size %u)", mapped, page_size);

    if (!PineConfig::anti_checks)
        prctl(PR_SET_VMA, PR_SET_VMA_ANON_NAME, mapped, size, "pine codes");

    memset(mapped, 0, page_size);
    address = reinterpret_cast<uintptr_t>(mapped);
    offset = size;
    return mapped;
}
