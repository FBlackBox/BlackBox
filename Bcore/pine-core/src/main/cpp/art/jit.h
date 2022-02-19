//
// Created by canyie on 2020/3/15.
//

#ifndef PINE_JIT_H
#define PINE_JIT_H

#include <memory>
#include "../utils/macros.h"
#include "thread.h"
#include "../utils/log.h"
#include "../utils/member.h"
#include "../utils/elf_img.h"

namespace pine::art {
    class JitCompiler final {
    public:
        std::unique_ptr<void> compiler_options_;
    private:
        DISALLOW_IMPLICIT_CONSTRUCTORS(JitCompiler);
    };

    class Jit final {
    public:
        static void Init(const ElfImg* art_lib_handle, const ElfImg* jit_lib_handle);

        static JitCompiler* GetCompiler() {
            return LIKELY(self_compiler) ? self_compiler : GetGlobalCompiler();
        }

        static JitCompiler* GetGlobalCompiler() {
            return LIKELY(global_compiler_ptr) ? *global_compiler_ptr : nullptr;
        }

        static bool CompileMethod(Thread* thread, void* method);

        static bool DisableInline();

    private:
        static JitCompiler* self_compiler;

        static bool (*jit_compile_method)(void*, void*, void*, bool);

        static bool (*jit_compile_method_q)(void*, void*, void*, bool, bool);

        static JitCompiler** global_compiler_ptr;

        static void** jit_update_options_ptr;

        static Member<void, size_t>* CompilerOptions_inline_max_code_units;

        DISALLOW_IMPLICIT_CONSTRUCTORS(Jit);
    };

}

#endif //PINE_JIT_H
