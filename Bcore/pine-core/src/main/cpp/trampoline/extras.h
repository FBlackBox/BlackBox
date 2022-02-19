//
// Created by canyie on 2020/3/31.
//

#ifndef PINE_EXTRAS_H
#define PINE_EXTRAS_H

#include <cstdint>
#include "arch/arch.h"
#include "../utils/macros.h"

namespace pine {
    /** Hook extras data. DO NOT modify the member layout of this class because it is hard-coded in trampolines. */
    class PACKED(4) Extras final {
    public:
        Extras() {
        }

        Extras* CloneAndUnlock() {
            Extras* cloned = static_cast<Extras*>(malloc(sizeof(Extras)));
            memcpy(cloned, this, sizeof(Extras));
            ReleaseLock();
            return cloned;
        }

        ~Extras() {
        }

#if defined(__aarch64__) || defined(__arm__) // Not supported spinlock on x86 platform
        void ReleaseLock() {
            CHECK(lock_flag == 0, "Unexpected lock_flag %d", lock_flag);

            dmb(); // Ensure all previous accesses are observed before the lock is released.
            lock_flag = 1;
            dsb(); // Ensure completion of the store that cleared the lock before sending the event.
            sev(); // Wake up the thread that is waiting for the lock.
        }
#endif

        /** Thread lock flag, 1: unlocked, 0: locked. */
        volatile uint32_t lock_flag = 1;

#if defined(__aarch64__) || defined(__arm__)
        /** r1(32 bit) or x1(64 bit) register */
        void* r1 = nullptr;
        /** r2(32 bit) or x2(64 bit) register */
        void* r2 = nullptr;
        /** r3(32 bit) or x3(64 bit) register */
        void* r3 = nullptr;
#ifdef __aarch64__
        /** Floating point registers
         *  Note: In fact, we don’t need to save them,
         *  but an unknown error will occur when receiving directly in the bridge method
         *  See https://github.com/canyie/pine/issues/9
         */
        jdouble fps[8] = { 0, 0, 0, 0, 0, 0, 0, 0 };
#elif defined(__arm__)
        // Floating point registers, s0~s15 = d0~d7
        jfloat fps[16] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
#endif
#elif defined(__i386__)
        void* ecx = nullptr;
        void* edx = nullptr;
        void* esp = nullptr;
#else
#error unsupported architecture
#endif
    private:
        DISALLOW_COPY_AND_ASSIGN(Extras);
    };
}

#endif //PINE_EXTRAS_H
