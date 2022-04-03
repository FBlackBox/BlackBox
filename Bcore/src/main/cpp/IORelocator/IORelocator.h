#include <unistd.h>
#include <stdlib.h>
#include <string>
#include <map>
#include <list>
#include <jni.h>
#include <dlfcn.h>
#include <stddef.h>
#include <fcntl.h>
#include <dirent.h>
#include <sys/syscall.h>
#include <sys/ptrace.h>
#include <sys/socket.h>
#include <sys/wait.h>
#include <sys/user.h>
#include <limits.h>
#include <Substrate/CydiaSubstrate.h>
#include <Substrate/And64InlineHook.hpp>
#include <Log.h>


#define HOOK_METHOD(handle, method) hook_method(handle, #method, (void*) new_##method, (void**) &orig_##method)

#define HOOK_DEFINITION(ret, method, ...) \
  ret (*orig_##method)(__VA_ARGS__); \
  ret new_##method(__VA_ARGS__)

class IORelocate{
public:
    /**
     * 重定向路径
     * @param orig_path
     * @param new_path
     */
    static void relocatePath(const char *orig_path, const char *new_path);
    /**
     * 开始hook操作
     */
    static void init();
};
