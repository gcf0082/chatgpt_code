import os
import sys
import ctypes
import platform

# 获取系统调用表
if platform.system() == "Linux":
    libc = ctypes.CDLL("libc.so.6")
    syscalls = {}
    for line in open("/usr/include/asm/unistd_64.h"):
        if line.startswith("#define __NR"):
            syscall, number = line.split()
            syscalls[int(number)] = syscall.replace("__NR_", "")
elif platform.system() == "Darwin":
    libc = ctypes.CDLL("libc.dylib")
    syscalls = {}
    for line in open("/usr/include/sys/syscall.h"):
        if line.startswith("#define SYS_"):
            syscall, number = line.split()
            syscalls[int(number)] = syscall.replace("SYS_", "")
else:
    print("Unsupported platform.")
    sys.exit(1)

# 监控系统调用参数
def trace_syscall(pid):
    libc.ptrace.argtypes = [ctypes.c_int, ctypes.c_void_p, ctypes.c_void_p, ctypes.c_void_p]
    libc.ptrace.restype = ctypes.c_long

    # 运行子进程
    libc.ptrace(0, pid, 0, 0)

    while True:
        # 等待子进程执行系统调用
        status = ctypes.c_int()
        libc.waitpid(pid, ctypes.byref(status), 0)
        if libc.WIFSTOPPED(status) and libc.WSTOPSIG(status) & 0x80:
            # 获取系统调用号和参数
            orig_rax = libc.ptrace(0, pid, 8 * ctypes.c_long(ORIG_RAX), 0)
            args = [libc.ptrace(0, pid, 8 * ctypes.c_long(i), 0) for i in range(1, 7)]
            # 记录系统调用号和参数到文件
            with open("syscall.log", "a") as f:
                f.write(f"{syscalls[orig_rax]}({', '.join(str(arg) for arg in args)})\n")
            # 恢复子进程运行
            libc.ptrace(PT_SYSCALL, pid, 0, 0)

if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} <pid>")
        sys.exit(1)
    pid = int(sys.argv[1])
    trace_syscall(pid)
