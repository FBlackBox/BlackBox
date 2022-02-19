//
// Created by canyie on 2020/5/21.
//

#ifndef PINE_IO_WRAPPER_H
#define PINE_IO_WRAPPER_H

#include <cstdio>
#include <fcntl.h>
#include <cerrno>
#include "macros.h"
#include "log.h"

namespace pine {
    static bool CanRetry(int error) {
        return error == EINTR || error == EIO;
    }

    int WrappedOpen(const char* pathname, int flags, int max_retries = 2) {
        for (;;) {
            int fd = open(pathname, flags);
            if (LIKELY(fd != -1)) {
                return fd;
            }

            if (LIKELY(CanRetry(errno) && max_retries-- > 0)) {
                LOGW("Retrying to open %s with flags %d: errno %d (%s)",
                     pathname, flags, errno, strerror(errno));
            } else {
                LOGE("Failed to open %s with flags %d: errno %d (%s)",
                     pathname, flags, errno, strerror(errno));
                return -1;
            }
        }
    }

    FILE* WrappedFOpen(const char* pathname, const char* mode, int max_retries = 2) {
        for (;;) {
            FILE* file = fopen(pathname, mode);
            if (LIKELY(file)) {
                return file;
            }

            if (LIKELY(CanRetry(errno) && max_retries-- > 0)) {
                LOGW("Retrying to fopen %s with mode %s: errno %d (%s)",
                     pathname, mode, errno, strerror(errno));
            } else {
                LOGE("Failed to fopen %s with mode %s: errno %d (%s)",
                     pathname, mode, errno, strerror(errno));
                return nullptr;
            }
        }
    }
}

#endif //PINE_IO_WRAPPER_H
