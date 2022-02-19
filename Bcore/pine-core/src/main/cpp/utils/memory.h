//
// Created by canyie on 2020/3/11.
//

#ifndef PINE_MEMORY_H
#define PINE_MEMORY_H

#include <cerrno>
#include <mutex>
#include <sys/mman.h>
#include "macros.h"
#include "log.h"

namespace pine {
    class Memory {
    public:
        static void* AllocUnprotected(size_t size);

        static inline bool Unprotect(void* ptr) {
            size_t alignment = (uintptr_t) ptr % page_size;
            void* aligned_ptr = (void*) ((uintptr_t) ptr - alignment);
            int result = mprotect(aligned_ptr, page_size, PROT_READ | PROT_WRITE | PROT_EXEC);
            if (UNLIKELY(result == -1)) {
                LOGE("mprotect failed for %p: %s (%d)", ptr, strerror(errno), errno);
                return false;
            }
            return true;
        }

        template <typename T>
        static int FindOffset(void* start, T value, size_t size, uint step) {
            for (uint32_t offset = 0;offset < size;offset += step) {
                T current = *reinterpret_cast<T*>(reinterpret_cast<uintptr_t>(start) + offset);
                if (current == value) return static_cast<int>(offset);
            }
            return -1;
        }

        static inline void FlushCache(void* addr, size_t size) {
            __builtin___clear_cache((char*) addr, (char*) ((uintptr_t) addr + size));
        }

        template <typename T>
        static inline T AlignUp(T value, T align_with) {
            T alignment = value % align_with;
            if (alignment) {
                value += (align_with - alignment);
            }
            return value;
        }
    private:
        static const size_t page_size;
        static uintptr_t address;
        static size_t offset;
        static std::mutex mutex;
        DISALLOW_IMPLICIT_CONSTRUCTORS(Memory);
    };
}

#endif //PINE_MEMORY_H
