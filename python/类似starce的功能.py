#python strace_open_attach.py <pid>

import sys
import os
import traceback
from ptrace import PtraceError
from ptrace.debugger import PtraceDebugger
from ptrace.syscall import SYSCALL_NAMES, SYSCALL_ARG_TYPES


def write_to_file(args, call_stack):
    with open('output.txt', 'a') as f:
        f.write(f"open({args})\n")
        for frame in call_stack:
            f.write(f"{frame}\n")
        f.write("\n")


def main(argv):
    if len(argv) < 2:
        print("Usage: python strace_attach.py <pid>")
        return

    # 创建调试器
    debugger = PtraceDebugger()

    # 附加到目标进程
    try:
        process = debugger.addProcess(int(argv[1]))
    except PtraceError as e:
        print("Error: ", e)
        return

    # 等待程序启动
    process.syscall()

    # 跟踪系统调用
    while True:
        try:
            # 等待系统调用
            process.syscall()

            # 获取系统调用号
            syscall_number = process.getSyscallNumber()

            # 检查系统调用是否为exit
            if syscall_number == 60:
                break

            # 获取系统调用名称
            syscall_name = SYSCALL_NAMES.get(syscall_number, "unknown")

            # 获取系统调用参数类型
            arg_types = SYSCALL_ARG_TYPES.get(syscall_number, [])

            # 获取系统调用参数值
            args = []
            for i in range(len(arg_types)):
                if arg_types[i] == 'pointer':
                    args.append(process.readPointer(process.getSyscallArgument(i)))
                else:
                    args.append(process.getSyscallArgument(i))

            # 如果是open系统调用，则记录参数和调用栈
            if syscall_name == 'open':
                call_stack = traceback.format_stack()[:-1]
                write_to_file(args, call_stack)

            # 继续执行
            process.syscall()
        except PtraceError as e:
            print("Error: ", e)
            break

    # 结束调试器
    debugger.quit()


if __name__ == '__main__':
    if os.path.exists('output.txt'):
        os.remove('output.txt')
    main(sys.argv)
