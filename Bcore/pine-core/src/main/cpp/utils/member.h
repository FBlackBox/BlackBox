//
// Created by canyie on 2020/2/9.
//

#ifndef PINE_MEMBER_H
#define PINE_MEMBER_H

#include <stdint.h>
#include <string.h>

template <typename IType, typename MType>
class Member {
public:
    Member() : offset(-1) {
    }

    Member(int32_t offset) : offset(offset) {
    }

    template <typename ToType>
    ToType GetAs(IType *instance) {
        if (UNLIKELY(!IsValid())) {
            return 0;
        }
        return *reinterpret_cast<ToType*>((uintptr_t) instance + offset);
    }

    MType Get(IType *instance) {
        return GetAs<MType>(instance);
    }

    template <typename ToType>
    void SetAs(IType* instance, ToType value) {
        if (UNLIKELY(!IsValid())) {
            return;
        }
        memcpy(reinterpret_cast<void *> ((uintptr_t) instance + offset), &value, sizeof(ToType));
    }

    void Set(IType* instance, MType value) {
        SetAs<MType>(instance, value);
    }

    void SetOffset(int32_t offset) {
        this->offset = offset;
    }

    int32_t GetOffset() {
        return offset;
    }

    bool IsValid() {
        return offset >= 0;
    }

private:
    int32_t offset;
};

#endif //PINE_MEMBER_H
