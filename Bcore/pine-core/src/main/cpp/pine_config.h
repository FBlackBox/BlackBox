//
// Created by canyie on 2020/8/21.
//

#ifndef PINE_PINE_CONFIG_H
#define PINE_PINE_CONFIG_H

#include "utils/macros.h"

namespace pine {
    class PineConfig final {
    public:
        static bool debug;
        static bool debuggable;
        static bool anti_checks;
        static bool jit_compilation_allowed;
    private:
        DISALLOW_IMPLICIT_CONSTRUCTORS(PineConfig);
    };
}

#endif //PINE_PINE_CONFIG_H
