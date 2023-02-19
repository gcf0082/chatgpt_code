
//
这个程序通过fork()函数创建了一个子进程，然后使用ptrace()函数跟踪子进程的系统调用。当子进程进行系统调用时，父进程就会收到一个SIGTRAP信号，这时父进程就可以使用ptrace()函数获取子进程的寄存器信息，包括系统调用号和参数等。程序会将这些信息写入一个文件（syscalls.log），记录下系统调用及其参数和返回值。然后父进程继续让子进程运行，直到子进程退出为止。
#define _GNU_SOURCE
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <sys/reg.h>
#include <sys/user.h>
#include <unistd.h>
#include <stdio.h>
#include <stdlib.h>
#include <fcntl.h>
#include <string.h>
#include <errno.h>

const char *syscalls[] = {
    [SYS_read] = "read",
    [SYS_write] = "write",
    [SYS_open] = "open",
    [SYS_close] = "close",
    // TODO: 定义更多的系统调用名称
};

int main(int argc, char *argv[]) {
    if (argc < 2) {
        fprintf(stderr, "Usage: %s <program> [<args>...]\n", argv[0]);
        return 1;
    }

    // fork一个子进程并让它在跟踪模式下执行目标程序
    pid_t pid = fork();
    if (pid == -1) {
        perror("fork");
        return 1;
    } else if (pid == 0) {
        if (ptrace(PTRACE_TRACEME, 0, NULL, NULL) == -1) {
            perror("ptrace(TRACEME)");
            return 1;
        }
        execvp(argv[1], &argv[1]);
        perror("execvp");
        return 1;
    }

    // 等待子进程开始执行
    int status;
    waitpid(pid, &status, 0);

    while (WIFSTOPPED(status)) {
        // 跟踪系统调用
        if (WSTOPSIG(status) & 0x80) {
            struct user_regs_struct regs;
            if (ptrace(PTRACE_GETREGS, pid, NULL, &regs) == -1) {
                perror("ptrace(GETREGS)");
                break;
            }
            long syscall_num = regs.orig_rax;
            if (syscall_num < 0 || syscall_num >= sizeof(syscalls)/sizeof(*syscalls) || syscalls[syscall_num] == NULL) {
                fprintf(stderr, "Unknown syscall %ld\n", syscall_num);
                break;
            }
            const char *syscall_name = syscalls[syscall_num];

            // 获取系统调用参数
            long args[6];
            args[0] = regs.rdi;
            args[1] = regs.rsi;
            args[2] = regs.rdx;
            args[3] = regs.r10;
            args[4] = regs.r8;
            args[5] = regs.r9;

            // 记录系统调用及参数到文件
            char buf[1024];
            int n = snprintf(buf, sizeof(buf), "%s(", syscall_name);
            for (int i = 0; i < 6 && args[i] != 0; i++) {
                if (i > 0) {
                    n += snprintf(buf+n, sizeof(buf)-n, ", ");
                }
                n += snprintf(buf+n, sizeof(buf)-n, "%#lx", args[i]);
            }
            n += snprintf(buf+n, sizeof(buf)-n, ") = %ld\n", regs.rax);
            int fd = open("syscalls.log", O_CREAT|O_WRONLY|O_APPEND, 0644);
            if (fd == -1) {
                perror("open(syscalls.log)");
                break;
        if (write(fd, buf, n) == -1) {
            perror("write(syscalls.log)");
        }
        close(fd);

        // 继续执行子进程
        if (ptrace(PTRACE_SYSCALL, pid, NULL, NULL) == -1) {
            perror("ptrace(SYSCALL)");
            break;
        }
    } else {
        // 子进程接收到其他信号，继续执行
        if (ptrace(PTRACE_CONT, pid, NULL, NULL) == -1) {
            perror("ptrace(CONT)");
            break;
        }
    }

    // 等待子进程下一个停止信号
    waitpid(pid, &status, 0);
}

return 0;
}
