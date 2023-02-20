//$ gcc -shared -fPIC fopen_interceptor.c -o fopen_interceptor.so -ldl
//$ export LD_PRELOAD=./fopen_interceptor.so

#define _GNU_SOURCE
#include <stdio.h>
#include <dlfcn.h>
#include <execinfo.h>

// 定义原始的fopen函数
typedef FILE *(*orig_fopen_func_type)(const char *path, const char *mode);
static orig_fopen_func_type orig_fopen;

// 定义我们自己的fopen函数
FILE *fopen(const char *path, const char *mode) {
    // 打印调用栈
    void *stack_trace[10];
    int size = backtrace(stack_trace, 10);
    backtrace_symbols_fd(stack_trace, size, STDERR_FILENO);

    // 打印调用参数
    printf("fopen called with args: %s, %s\n", path, mode);

    // 调用原始的fopen函数
    orig_fopen = (orig_fopen_func_type)dlsym(RTLD_NEXT, "fopen");
    FILE *file = orig_fopen(path, mode);

    return file;
}
