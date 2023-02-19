use std::ffi::CString;
use std::fs::OpenOptions;
use std::io::{Error, Write};
use std::os::raw::{c_long, c_void};
use std::os::unix::io::IntoRawFd;
use std::ptr;
use std::slice;

use nix::sys::ptrace;
use nix::sys::wait::waitpid;
use nix::unistd::{fork, ForkResult};

const ORIG_RAX: usize = 15;
const PT_TRACE_ME: i32 = 0;
const PT_ATTACH: i32 = 16;
const PT_SYSCALL: i32 = 24;
const PT_DETACH: i32 = 17;

fn get_syscalls() -> Vec<&'static str> {
    // 获取系统调用号与名称的映射
    let file = include_str!("/usr/include/x86_64-linux-gnu/asm/unistd_64.h");
    let mut syscalls = Vec::new();
    for line in file.lines() {
        if let Some(captures) = regex::Regex::new(r"^#define __NR_(\w+)\s+(\d+)").unwrap().captures(line) {
            let syscall = captures.get(1).unwrap().as_str();
            let num = captures.get(2).unwrap().as_str().parse::<usize>().unwrap();
            if num > syscalls.len() {
                syscalls.resize(num + 1, "");
            }
            syscalls[num] = syscall;
        }
    }
    syscalls
}

fn trace_syscall(pid: libc::pid_t) -> Result<(), Error> {
    // 获取系统调用号与名称的映射
    let syscalls = get_syscalls();

    // 将子进程变成跟踪状态
    ptrace::attach(pid)?;
    waitpid(pid, None)?;

    // 进入系统调用跟踪模式
    ptrace::syscall(pid, None)?;

    loop {
        // 等待子进程执行系统调用
        let mut status = 0;
        waitpid(pid, Some(&mut status), None)?;
        if nix::sys::signal::Signal::from_c_int(status as i32).map_or(false, |s| s == nix::sys::signal::Signal::SIGTRAP | 0x80) {
            // 获取系统调用号和参数
            let orig_rax = ptrace::getregs(pid)?.orig_rax as usize;
            let mut args: [usize; 6] = [0; 6];
            for i in 1..=6 {
                args[i - 1] = ptrace::getregs(pid)?.regs[i] as usize;
            }
            // 如果是open()相关的系统调用，则记录参数
            if ["open", "openat", "creat"].contains(&syscalls[orig_rax]) {
                let mut path = [0u8; 256];
                let path_ptr = args[0] as *mut c_void;
                let mut i = 0;
                loop {
                    let c = ptrace::read(pid, path_ptr.offset(i), None)?.get(0).unwrap().to_owned();
                    path[i] = c;
                    i += 1;
                    if c == 0 || i >= path.len() {
                        break;
                    }
                }
                let path = CString::new(&path[..            i]).unwrap().to_str().unwrap();
            let flags = args[1] as c_long;
            let mode = args[2] as c_long;
            // 将参数记录到文件中
            let mut file = OpenOptions::new().create(true).append(true).open("syscalls.log")?;
            writeln!(
                &mut file,
                "{}(\"{}\", {}, {:#o}) = {}",
                syscalls[orig_rax],
                path,
                flags,
                mode,
                ptrace::getregs(pid)?.rax,
            )?;
        }
        // 让子进程执行系统调用并等待下一次跟踪信号
        ptrace::syscall(pid, None)?;
    } else {
        // 处理其他信号（通常是子进程退出）
        break;
    }
}

// 让子进程脱离跟踪状态
ptrace::detach(pid, None)?;
Ok(())
}

fn main() {
let pid = fork();
match pid {
Ok(ForkResult::Child) => {
// 子进程执行一个系统调用
let _ = libc::getpid();
}
Ok(ForkResult::Parent { child }) => {
// 父进程跟踪子进程的系统调用
let _ = ptrace::setoptions(child, ptrace::Options::PTRACE_O_TRACESYSGOOD);
let _ = ptrace::syscall(child, None);
let _ = trace_syscall(child);
}
Err(_) => {
eprintln!("Failed to fork");
}
}
}
