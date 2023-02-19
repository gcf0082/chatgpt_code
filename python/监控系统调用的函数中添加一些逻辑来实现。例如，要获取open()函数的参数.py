import ctypes
import os
import signal

libc = ctypes.CDLL("libc.so.6", use_errno=True)

ORIG_RAX = 15
PT_TRACE_ME = 0
PT_ATTACH = 16
PT_SYSCALL = 24
PT_DETACH = 17

def get_syscalls():
    # 获取系统调用号与名称的映射
    f = open("/usr/include/asm/unistd_64.h")
    syscalls = {}
    for line in f:
        if line.startswith("#define __NR_"):
            syscall, num = line.split()
            num = int(num)
            syscall = syscall[8:].lower()
            syscalls[num] = syscall
    return syscalls

def trace_syscall(pid):
    # 获取系统调用号与名称的映射
    syscalls = get_syscalls()

    # 将子进程变成跟踪状态
    libc.ptrace(PT_ATTACH, pid, 0, 0)
    os.waitpid(pid, 0)
    libc.ptrace(PT_SYSCALL, pid, 0, 0)

    while True:
        # 等待子进程执行系统调用
        status = ctypes.c_int()
        libc.waitpid(pid, ctypes.byref(status), 0)
        if libc.WIFSTOPPED(status) and libc.WSTOPSIG(status) & 0x80:
            # 获取系统调用号和参数
            orig_rax = libc.ptrace(0, pid, 8 * ctypes.c_long(ORIG_RAX), 0)
            args = [libc.ptrace(0, pid, 8 * ctypes.c_long(i), 0) for i in range(1, 7)]
            # 如果是open()相关的系统调用，则记录参数
            if syscalls[orig_rax] in {"open", "openat", "creat"}:
                path = ctypes.create_string_buffer(256)
                libc.memcpy(path, args[0], 256)
                with open("syscall.log", "a") as f:
                    f.write(f"{syscalls[orig_rax]}('{path.value.decode('utf-8')}', {args[1]:#x})\n")
            # 恢复子进程运行
            libc.ptrace(PT_SYSCALL, pid, 0, 0)

def main():
    # 启动一个子进程
    pid = os.fork()
    if pid == 0:
        # 在子进程中运行一个简单的命令
        os.execlp("ls", "ls", "-l")
    else:
        # 监控子进程的系统调用
        trace_syscall(pid)
        os.kill(pid, signal.SIGTERM)

if __name__ == "__main__":
    main()
