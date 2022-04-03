#include "IORelocator.h"

#if defined(__LP64__)
#define LINKER_PATH "/system/bin/linker64"
#define packageName "top.niunaijun.blackboxa64"
#else
#define LINKER_PATH "/system/bin/linker"
#define packageName "top.niunaijun.blackboxa32"
#endif

bool is_need_loadEnv = true;

static inline void hook_method(void *handle, const char *symbol, void *new_method, void **old_method) {
    void *address = dlsym(handle, symbol);
    if (address == NULL) {
        ALOGE("寻找不到符号信息 : %s", symbol);
        return;
    }
#if defined(__LP64__)
    A64HookFunction(address, new_method, old_method);
#else
    MSHookFunction(address, new_method, old_method);
#endif
}

void IORelocate::relocatePath(const char *orig_path, const char *new_path){
    ALOGE("旧路径%s,新路径%s",orig_path,new_path);
}

//int open(const char * pathname, int flags);
HOOK_DEFINITION(int,open,const char* pathname, int flags){
    ALOGE("open ->%s",pathname);
    return orig_open(pathname,flags);
}

void IORelocate::init(){
    void *handle = dlopen("libc.so", RTLD_NOW);
    if (handle){
        HOOK_METHOD(handle,open);
    }
}

