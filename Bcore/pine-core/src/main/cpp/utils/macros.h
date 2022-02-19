//
// Created by canyie on 2020/2/9.
//

#ifndef PINE_MACROS_H
#define PINE_MACROS_H

#define LIKELY(x) __builtin_expect(!!(x), 1)
#define UNLIKELY(x) __builtin_expect(!!(x), 0)
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#define ALIGNED(x) __attribute__ ((__aligned__(x)))
#define PACKED(x) __attribute__ ((__aligned__(x), __packed__))
#define EXPORT_C extern "C" __attribute__ ((visibility ("default")))
#define ALWAYS_INLINE __attribute__((always_inline))
#define JBOOL_TRUE(x) (x != JNI_FALSE)

#define DISALLOW_COPY_AND_ASSIGN(TypeName) \
TypeName(const TypeName&) = delete;      \
void operator=(const TypeName&) = delete

#define DISALLOW_IMPLICIT_CONSTRUCTORS(TypeName) \
TypeName() = delete;                           \
DISALLOW_COPY_AND_ASSIGN(TypeName)


#endif //PINE_MACROS_H
