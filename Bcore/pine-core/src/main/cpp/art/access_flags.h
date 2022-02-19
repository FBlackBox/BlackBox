//
// Created by canyie on 2020/3/19.
//

#ifndef PINE_ACCESS_FLAGS_H
#define PINE_ACCESS_FLAGS_H

#include <cstdint>
#include "../utils/macros.h"

namespace pine {
    class AccessFlags final {
    public:
        static constexpr uint32_t kPublic = 0x0001;
        static constexpr uint32_t kPrivate = 0x0002;
        static constexpr uint32_t kProtected = 0x0004;
        static constexpr uint32_t kStatic = 0x0008;
        static constexpr uint32_t kFinal = 0x0010;
        static constexpr uint32_t kSynchronized = 0x0020;
        static constexpr uint32_t kNative = 0x0100;
        static constexpr uint32_t kConstructor = 0x00010000;
        static constexpr uint32_t kDeclaredSynchronized = 0x00020000;
        static constexpr uint32_t kSkipAccessChecks = 0x00080000;
        static constexpr uint32_t kMiranda = 0x00200000;
        static constexpr uint32_t kFastNative = 0x00080000;
        static constexpr uint32_t kCriticalNative = 0x00200000;
        static constexpr uint32_t kDontInline_M = 0x00400000;
        static constexpr uint32_t kCompileDontBother_N = 0x01000000;
        static constexpr uint32_t kCompileDontBother_O_MR1 = 0x02000000;
        static constexpr uint32_t kPreCompiled_R = 0x00200000;

        // This value from commit https://android-review.googlesource.com/c/platform/art/+/1646010
        // We skipped commit https://android-review.googlesource.com/c/platform/art/+/1645449
        static constexpr uint32_t kPreCompiled_S = 0x00800000;
        static constexpr uint32_t kSingleImplementation = 0x08000000;
        static constexpr uint32_t kPublicApi = 0x10000000;
        static constexpr uint32_t kCorePlatformApi = 0x20000000;
        static constexpr uint32_t kFastInterpreterToInterpreterInvoke = 0x40000000;
    private:
        DISALLOW_IMPLICIT_CONSTRUCTORS(AccessFlags);
    };
}

#endif //PINE_ACCESS_FLAGS_H
