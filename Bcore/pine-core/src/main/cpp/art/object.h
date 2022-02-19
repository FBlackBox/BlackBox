//
// Created by canyie on 2020/8/3.
//

#ifndef PINE_OBJECT_H
#define PINE_OBJECT_H

#include <cstdint>
#include "../utils/macros.h"

class PACKED(4) Object final {
public:
    Object* GetClass() {
        return reinterpret_cast<Object*>(class_);
    }

    uint32_t GetMonitor() {
        return monitor_;
    }

    bool IsForwardingAddress() {
        return (monitor_ & kStateMask) == kForwardingAddress;
    }

    Object* GetForwardingAddress() {
        return reinterpret_cast<Object*>(monitor_ << kStateUsed);
    }

private:
    uint32_t class_; // HeapReference<Class> { uint32_t reference_; };
    uint32_t monitor_;

    static constexpr uint32_t kStateUsed = 3u; // art objects are aligned with 8 bytes.
    static constexpr uint32_t kStateMask = 3u << 30; // Top 2 bits.
    static constexpr uint32_t kForwardingAddress = 3u << 30; // Top 2 bits are value 3 (kStateForwardingAddress).

    DISALLOW_IMPLICIT_CONSTRUCTORS(Object);
    ~Object() = delete; // Disallow direct to delete a art object
};

#endif //PINE_OBJECT_H
