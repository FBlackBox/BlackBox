//
// Created by canyie on 2020/3/11.
//

#ifndef PINE_LOCK_H
#define PINE_LOCK_H

#include "macros.h"

namespace pine {
    class ScopedLock {
    public:
        inline ScopedLock(std::mutex& mutex) : mLock(mutex)  { mLock.lock(); }
        inline ScopedLock(std::mutex* mutex) : mLock(*mutex) { mLock.lock(); }
        inline ~ScopedLock() { mLock.unlock(); }
    private:
        std::mutex& mLock;

        DISALLOW_COPY_AND_ASSIGN(ScopedLock);
    };
}
#endif //PINE_LOCK_H
