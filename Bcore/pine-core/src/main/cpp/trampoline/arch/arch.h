//
// Created by canyie on 2020/4/2.
//

#ifndef PINE_ARCH_H
#define PINE_ARCH_H

#if defined(__aarch64__) || defined(__arm__)
inline void sev() {
    __asm__ __volatile__ ("sev" :::"memory");
}

inline void dmb() {
    __asm__ __volatile__ ("dmb sy" :::"memory");
}

inline void dsb() {
    __asm__ __volatile__ ("dsb sy" :::"memory");
}
#endif

#endif //PINE_ARCH_H
