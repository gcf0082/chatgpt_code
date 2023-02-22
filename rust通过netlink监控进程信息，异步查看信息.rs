use neli::{
    consts::SocketFlags,
    rtnl::{Taskstats, TaskstatsMessage},
    socket::NlSocketHandle,
};
use std::{
    ffi::CString,
    io::Read,
    os::unix::{
        io::{AsRawFd, RawFd},
        net::UnixStream,
    },
    thread,
};
use string_template::Template;

// 定义一个结构体来保存进程信息
struct ProcessInfo {
    pid: i32,
    cmdline: String,
}

fn main() {
    // 建立Netlink socket
    let socket = NlSocketHandle::connect(NeliProtocol::Route, None, None, SocketFlags::empty())
        .expect("Failed to connect to Netlink socket");

    // 向内核注册TASKSTATS类别的Netlink事件
    let mut message = TaskstatsMessage::default();
    message.header.nlmsg_type = TASKSTATS_TYPE_PID;
    message.header.nlmsg_flags |= NLM_F_REQUEST | NLM_F_DUMP;
    message.header.nlmsg_len = message.header.nlmsg_len();
    message.tkl_pid = 0;
    message.tkl_stats = 0xffffffff;

    socket
        .send(&message, None)
        .expect("Failed to send Netlink message");

    // 建立与父进程通信的Unix域套接字
    let mut stream = UnixStream::connect("/tmp/proc_info.sock").unwrap();
    let fd = stream.as_raw_fd();

    // 建立进程信息获取线程
    thread::spawn(move || {
        let mut buf = [0; 4096];
        loop {
            let mut taskstats = Taskstats::default();

            // 从Unix域套接字读取进程ID
            let mut pid = 0;
            unsafe {
                let len = libc::read(
                    fd,
                    &mut pid as *mut i32 as *mut libc::c_void,
                    std::mem::size_of::<i32>(),
                );
                if len != std::mem::size_of::<i32>() as isize {
                    panic!("Error reading from pipe");
                }
            }

            // 通过/proc文件系统获取进程完整路径和参数信息
            let mut cmdline = String::new();
            let mut file = std::fs::File::open(format!("/proc/{}/cmdline", pid)).unwrap();
            file.read_to_string(&mut cmdline).unwrap();
            cmdline = cmdline.replace("\0", " ").trim().to_string();

            // 将进程信息写入Unix域套接字
            let info = ProcessInfo { pid, cmdline };
            let template = Template::new("{pid} {cmdline}");
            let message = template.render(&info).unwrap();
            stream.write_all(message.as_bytes()).unwrap();
        }
    });

    // 处理Netlink消息并输出进程信息
    let mut buf = [0; 4096];
    loop {
        let len = socket.recv(&mut buf, None).unwrap();

        let mut nlmsghdr = neli::consts::nl::Nlmsghdr::new();
        let mut nlmsg_len = len;

        使用Golang监控进程


madrehongs@hotmail.com
使用golang基于netlink监控新增的进程

要使用Golang基于netlink监控新增的进程，您可以使用github.com/vishvananda/netlink包。以下是一个简单的示例程序，它将创建一个netlink socket以接收进程通知，然后打印新进程的PID和名称。

go
Copy code
package main

import (
    "github.com/vishvananda/netlink"
    "log"
)

func main() {
    netlinkSocket, err := netlink.Subscribe(netlink.NETLINK_NETFILTER, netlink.SOCK_DGRAM, netlink.PROCESS_EVENTS)
    if err != nil {
        log.Fatal("Unable to subscribe to netlink socket:", err)
    }

    for {
        messages, err := netlinkSocket.Receive()
        if err != nil {
            log.Fatal("Error receiving message from netlink socket:", err)
        }

        for _, message := range messages {
            switch message.Header.Type {
            case netlink.PROCESS_NEW:
                log.Printf("New process with PID %d and name %s\n", message.Data.Pid, message.Data.Comm)
            }
        }
    }
}
此程序使用netlink.Subscribe函数创建一个NETLINK_NETFILTER类型的netlink socket，以接收进程通知。在无限循环中，它调用netlinkSocket.Receive()以接收所有消息，然后遍历每个消息以查找类型为PROCESS_NEW的消息。当找到一个新进程时，它打印进程的PID和名称。

注意，为了运行此程序，您需要以root权限运行它，因为只有root用户才能访问netlink socket。




madrehongs@hotmail.com
同时显示进程的完整路径和参数

要显示进程的完整路径和参数，您可以使用github.com/mitchellh/go-ps包。以下是一个修改后的示例程序，它将监视新进程并打印它们的PID、名称、完整路径和参数：

go
Copy code
package main

import (
    "fmt"
    "github.com/mitchellh/go-ps"
    "github.com/vishvananda/netlink"
    "log"
)

func main() {
    netlinkSocket, err := netlink.Subscribe(netlink.NETLINK_NETFILTER, netlink.SOCK_DGRAM, netlink.PROCESS_EVENTS)
    if err != nil {
        log.Fatal("Unable to subscribe to netlink socket:", err)
    }

    for {
        messages, err := netlinkSocket.Receive()
        if err != nil {
            log.Fatal("Error receiving message from netlink socket:", err)
        }

        for _, message := range messages {
            switch message.Header.Type {
            case netlink.PROCESS_NEW:
                process, err := ps.FindProcess(message.Data.Pid)
                if err != nil {
                    log.Printf("Unable to find process with PID %d: %v\n", message.Data.Pid, err)
                    continue
                }
                log.Printf("New process with PID %d, name %s, path %s, and args %s\n", process.Pid(), process.Executable(), process.Executable(), process.Cmdline())
            }
        }
    }
}
此程序与之前的程序类似，但是它使用go-ps包的FindProcess函数来查找新进程的完整路径和参数。在找到进程后，它将打印进程的PID、名称、完整路径和参数。

请注意，go-ps包仅适用于Linux和Windows系统。如果您的程序需要在其他操作系统上运行，请查看github.com/shirou/gopsutil包，它提供了跨平台的进程管理功能。




madrehongs@hotmail.com
以上程序可以在接收到新增进程事件后，通过异步的方式获取进程路径参数信息吗

是的，可以在接收到新增进程事件后，通过异步的方式获取进程路径和参数信息。这可以通过使用Go语言中的goroutine实现异步操作。下面是一个示例程序，它创建了一个goroutine来异步获取进程路径和参数信息：

go
Copy code
package main

import (
    "fmt"
    "github.com/mitchellh/go-ps"
    "github.com/vishvananda/netlink"
    "log"
)

func main() {
    netlinkSocket, err := netlink.Subscribe(netlink.NETLINK_NETFILTER, netlink.SOCK_DGRAM, netlink.PROCESS_EVENTS)
    if err != nil {
        log.Fatal("Unable to subscribe to netlink socket:", err)
    }

    processChan := make(chan *ps.Process, 10)
    go processPathAndArgs(processChan)

    for {
        messages, err := netlinkSocket.Receive()
        if err != nil {
            log.Fatal("Error receiving message from netlink socket:", err)
        }

        for _, message := range messages {
            switch message.Header.Type {
            case netlink.PROCESS_NEW:
                go func(pid uint32) {
                    process, err := ps.FindProcess(int(pid))
                    if err != nil {
                        log.Printf("Unable to find process with PID %d: %v\n", pid, err)
                        return
                    }
                    processChan <- process
                }(message.Data.Pid)
            }
        }
    }
}

func processPathAndArgs(processChan chan *ps.Process) {
    for process := range processChan {
        log.Printf("New process with PID %d, name %s, path %s, and args %s\n", process.Pid(), process.Executable(), process.Executable(), process.Cmdline())
    }
}
此程序与之前的程序类似，但是它创建了一个名为processPathAndArgs的goroutine来异步获取进程路径和参数信息。当一个新进程被检测到时，它将启动另一个goroutine来异步获取进程信息，将进程信息发送到通道中，并让processPathAndArgs函数处理信息。processPathAndArgs函数不断从通道中读取信息，并打印进程的PID、名称、路径和参数。

请注意，processPathAndArgs函数需要使用range语句从通道中读取信息，因此该函数应该在主函数之前定义，以确保通道在程序运行期间始终可用。




madrehongs@hotmail.com
以上程序可以改成c语言版的吗

是的，可以使用C语言编写类似的程序来监视新增进程并异步获取进程路径和参数信息。在Linux系统中，可以使用Netlink API来监视进程事件，并使用procfs文件系统来获取进程信息。以下是一个示例程序，它使用Netlink API和procfs文件系统来监视进程事件并异步获取进程路径和参数信息：

c
Copy code
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/socket.h>
#include <linux/netlink.h>
#include <linux/taskstats.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <dirent.h>

#define MAX_PAYLOAD 2048
#define TASKSTATS_TYPE 20
#define TASKSTATS_CMD_ATTR_PID 1

struct taskstats_data {
    __u64 cpu_time; /* CPU time (in nanoseconds) */
    __u64 blkio_delay; /* Block I/O delay (in nanoseconds) */
    __u64 swapin_delay; /* Swapin delay (in nanoseconds) */
    __u64 blkio_delay_total; /* Total block I/O delay (in nanoseconds) */
    __u64 swapin_delay_total; /* Total swapin delay (in nanoseconds) */
};

int main() {
    int nlSocket;
    struct sockaddr_nl srcAddr, destAddr;
    struct nlmsghdr *nlMsgHdr;
    struct iovec iov;
    struct msghdr msg;
    char buf[MAX_PAYLOAD];
    ssize_t len;

    nlSocket = socket(AF_NETLINK, SOCK_RAW, TASKSTATS_TYPE);
    if (nlSocket < 0) {
        perror("Error creating Netlink socket");
        return -1;
    }

    memset(&srcAddr, 0, sizeof(srcAddr));
    srcAddr.nl_family = AF_NETLINK;
    srcAddr.nl_pid = getpid();

    if (bind(nlSocket, (struct sockaddr *)&srcAddr, sizeof(srcAddr)) < 0) {
        perror("Error binding Netlink socket");
        return -1;
    }

    memset(&destAddr, 0, sizeof(destAddr));
    destAddr.nl_family = AF_NETLINK;
    destAddr.nl_groups = TASKSTATS_CMD_ATTR_PID;

    nlMsgHdr = (struct nlmsghdr *)buf;
    nlMsgHdr->nlmsg_len = NLMSG_LENGTH(sizeof(struct taskstats_data));
    nlMsgHdr->nlmsg_pid = getpid();
    nlMsgHdr->nlmsg_type = TASKSTATS_CMD_ATTR_PID;
    nlMsgHdr->nlmsg_flags = NLM_F_REQUEST | NLM_F_ACK;

    iov.iov_base = (void *)&nlMsgHdr->nlmsg_seq;
    iov.iov_len = sizeof(nlMsgHdr->nlmsg_seq);

    memset(&msg, 0, sizeof(msg));
    msg.msg_name = (void *)&destAddr;
    msg.msg_namelen = sizeof(destAddr);
    msg.msg_iov = &iov;
    msg.msg_iovlen = 1;

    if (sendmsg(nlSocket, &msg, 0) < 0) {
        perror("Error sending Netlink message");
        return -1;
    }

    struct dirent *dent;
    char path[1024];
    char cmdline[1024];
    char *line = NULL;
    size_t len_line = 0;
    ssize_t read_line;
    FILE *file;

    int fd = open("/proc", O_RDONLY | O_DIRECTORY);
    if (fd < 0) {
        perror



madrehongs@hotmail.com
继续

("Error opening /proc directory");
return -1;
}

scss
Copy code
while (1) {
    len = recv(nlSocket, buf, MAX_PAYLOAD, 0);
    if (len < 0) {
        perror("Error receiving Netlink message");
        return -1;
    }

    nlMsgHdr = (struct nlmsghdr *)buf;

    while (NLMSG_OK(nlMsgHdr, len)) {
        if (nlMsgHdr->nlmsg_type == NLMSG_ERROR) {
            struct nlmsgerr *err = (struct nlmsgerr *)NLMSG_DATA(nlMsgHdr);
            if (nlMsgHdr->nlmsg_len < NLMSG_LENGTH(sizeof(struct nlmsgerr))) {
                fprintf(stderr, "Error receiving Netlink message: invalid error message\n");
                return -1;
            }
            errno = -err->error;
            perror("Error receiving Netlink message");
            return -1;
        }

        if (nlMsgHdr->nlmsg_type == TASKSTATS_TYPE) {
            struct taskstats *taskstats = (struct taskstats *)NLMSG_DATA(nlMsgHdr);
            snprintf(path, sizeof(path), "/proc/%d/cmdline", taskstats->ac_pid);
            file = fopen(path, "r");
            if (file) {
                read_line = getline(&line, &len_line, file);
                if (read_line > 0) {
                    printf("New process: %s [%d]\n", line, taskstats->ac_pid);
                }
                fclose(file);
            }
        }

        nlMsgHdr = NLMSG_NEXT(nlMsgHdr, len);
    }
}

close(fd);
close(nlSocket);
return 0;
}

Copy code

注意，此示例程序仅用于演示如何使用Netlink API和procfs文件系统在C语言中实现类似的功能，并且可能需要进行进一步的优化和调试才能用于生产环境。



madrehongs@hotmail.com
以上程序使用的是异步查找进程信息吗

以上程序并没有使用异步的方式查找进程信息，而是同步地在处理Netlink消息时进行的。具体来说，当程序收到一个Netlink消息时，它会解析出其中的进程ID，并使用该进程ID从/proc文件系统中读取进程的完整路径和参数信息。这个过程是同步的，即需要等待fopen()和getline()系统调用返回才能继续处理下一个Netlink消息。

如果要使用异步方式获取进程信息，可以将获取进程信息的逻辑放到另外一个线程中，并通过消息队列或管道等方式与主线程进行通信。具体实现方式可以参考多线程编程和进程间通信的相关知识。




madrehongs@hotmail.com
改成异步的方式吧

好的，以下是一个改成异步方式的示例程序，它通过一个独立的线程来获取进程信息，并通过管道将进程信息发送给主线程进行处理。主线程会创建一个Netlink socket并监听内核发送的进程事件。当收到进程事件时，它会解析出进程ID，并将进程ID发送给进程信息获取线程。进程信息获取线程会从/proc文件系统中读取进程的完整路径和参数信息，并将进程信息通过管道发送给主线程。主线程在管道上监听，一旦有新的进程信息到达，它就会输出该进程的完整路径和参数信息。

c
Copy code
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <unistd.h>
#include <pthread.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <linux/netlink.h>
#include <linux/taskstats.h>

#define MAX_PAYLOAD 1024

struct process_info {
    pid_t pid;
    char cmdline[1024];
};

int nlSocket;
int pipefd[2];

void *get_process_info_thread(void *arg) {
    struct process_info info;
    char path[256];
    FILE *file;
    ssize_t read_line;
    size_t len_line = 0;

    while (1) {
        // 从管道读取进程ID
        if (read(pipefd[0], &info, sizeof(info)) == -1) {
            perror("Error reading from pipe");
            exit(EXIT_FAILURE);
        }

        // 读取进程的完整路径和参数信息
        snprintf(path, sizeof(path), "/proc/%d/cmdline", info.pid);
        file = fopen(path, "r");
        if (file) {
            read_line = getline(&info.cmdline, &len_line, file);
            if (read_line > 0) {
                // 将进程信息写入管道
                if (write(pipefd[1], &info, sizeof(info)) == -1) {
                    perror("Error writing to pipe");
                    exit(EXIT_FAILURE);
                }
            }
            fclose(file);
        }
    }

    return NULL;
}

int main(int argc, char *argv[]) {
    struct sockaddr_nl nlAddr;
    struct nlmsghdr *nlMsgHdr;
    char buf[MAX_PAYLOAD];
    ssize_t len;
    pid_t pid;
    struct process_info info;

    // 创建Netlink socket
    nlSocket = socket(AF_NETLINK, SOCK_RAW, NETLINK_GENERIC);
    if (nlSocket == -1) {
        perror("Error creating Netlink socket");
        exit(EXIT_FAILURE);
    }

    memset(&nlAddr, 0, sizeof(nlAddr));
    nlAddr.nl_family = AF_NETLINK;
    nlAddr.nl_groups = CN_IDX_PROC;
    if (bind(nlSocket, (struct sockaddr *)&nlAddr, sizeof(nlAddr)) == -1) {
        perror("Error binding Netlink socket");
        exit(EXIT_FAILURE);
    }

    // 创建管道
    if (pipe(pipefd) == -1) {
        perror("Error creating pipe");
        exit(EXIT_FAILURE);
    }

    // 创建进程信息获取线程
    pthread_t thread_id;
    if (pthread_create(&thread_id, NULL, get_process_info_thread, NULL) != 0) {
        perror("Error creating thread");
        exit(EXIT_FAILURE);
    }

    while (1) {
        len =



madrehongs@hotmail.com
继续

以下是续上述程序的代码：

c
Copy code
        // 接收Netlink消息
        len = recv(nlSocket, buf, MAX_PAYLOAD, 0);
        if (len == -1) {
            perror("Error receiving Netlink message");
            exit(EXIT_FAILURE);
        }

        nlMsgHdr = (struct nlmsghdr *)buf;

        // 遍历所有Netlink消息
        while (NLMSG_OK(nlMsgHdr, len)) {
            // 检查是否是进程事件
            if (nlMsgHdr->nlmsg_type == TASKSTATS_TYPE_PID) {
                struct taskstats *ts = (struct taskstats *)NLMSG_DATA(nlMsgHdr);
                if (ts->ac_exitcode == 0) {
                    pid = ts->ac_pid;

                    // 将进程ID写入管道
                    info.pid = pid;
                    if (write(pipefd[1], &info, sizeof(info)) == -1) {
                        perror("Error writing to pipe");
                        exit(EXIT_FAILURE);
                    }
                }
            }

            // 移动到下一个Netlink消息
            nlMsgHdr = NLMSG_NEXT(nlMsgHdr, len);
        }

        // 从管道读取进程信息并输出
        while (read(pipefd[0], &info, sizeof(info)) > 0) {
            printf("pid=%d, cmdline=%s\n", info.pid, info.cmdline);
        }
    }

    close(nlSocket);
    close(pipefd[0]);
    close(pipefd[1]);
    return 0;
}
这个程序的主要改动是添加了一个独立的线程来获取进程信息，并通过管道与主线程进行通信。主线程在处理Netlink消息时，会将收到的进程ID写入管道。进程信息获取线程从管道中读取进程ID，然后从/proc文件系统中读取进程的完整路径和参数信息，并将进程信息再次写入管道。主线程在管道上监听，一旦有新的进程信息到达，它就会输出该进程的完整路径和参数信息。

这种异步方式可以避免在处理Netlink消息时阻塞，提高程序的响应速度。同时，它也可以更好地处理进程信息获取的异常情况，比如/proc文件系统中没有找到相应的进程信息等。




madrehongs@hotmail.com
以上程序有rust版吗

以下是一个类似的Rust程序，也是基于Netlink和/proc文件系统来监控新增进程并获取进程的完整路径和参数：

rust
Copy code
use neli::{
    consts::SocketFlags,
    rtnl::{Taskstats, TaskstatsMessage},
    socket::NlSocketHandle,
};
use std::{
    ffi::CString,
    io::Read,
    os::unix::{
        io::{AsRawFd, RawFd},
        net::UnixStream,
    },
    thread,
};
use string_template::Template;

// 定义一个结构体来保存进程信息
struct ProcessInfo {
    pid: i32,
    cmdline: String,
}

fn main() {
    // 建立Netlink socket
    let socket = NlSocketHandle::connect(NeliProtocol::Route, None, None, SocketFlags::empty())
        .expect("Failed to connect to Netlink socket");

    // 向内核注册TASKSTATS类别的Netlink事件
    let mut message = TaskstatsMessage::default();
    message.header.nlmsg_type = TASKSTATS_TYPE_PID;
    message.header.nlmsg_flags |= NLM_F_REQUEST | NLM_F_DUMP;
    message.header.nlmsg_len = message.header.nlmsg_len();
    message.tkl_pid = 0;
    message.tkl_stats = 0xffffffff;

    socket
        .send(&message, None)
        .expect("Failed to send Netlink message");

    // 建立与父进程通信的Unix域套接字
    let mut stream = UnixStream::connect("/tmp/proc_info.sock").unwrap();
    let fd = stream.as_raw_fd();

    // 建立进程信息获取线程
    thread::spawn(move || {
        let mut buf = [0; 4096];
        loop {
            let mut taskstats = Taskstats::default();

            // 从Unix域套接字读取进程ID
            let mut pid = 0;
            unsafe {
                let len = libc::read(
                    fd,
                    &mut pid as *mut i32 as *mut libc::c_void,
                    std::mem::size_of::<i32>(),
                );
                if len != std::mem::size_of::<i32>() as isize {
                    panic!("Error reading from pipe");
                }
            }

            // 通过/proc文件系统获取进程完整路径和参数信息
            let mut cmdline = String::new();
            let mut file = std::fs::File::open(format!("/proc/{}/cmdline", pid)).unwrap();
            file.read_to_string(&mut cmdline).unwrap();
            cmdline = cmdline.replace("\0", " ").trim().to_string();

            // 将进程信息写入Unix域套接字
            let info = ProcessInfo { pid, cmdline };
            let template = Template::new("{pid} {cmdline}");
            let message = template.render(&info).unwrap();
            stream.write_all(message.as_bytes()).unwrap();
        }
    });

    // 处理Netlink消息并输出进程信息
    let mut buf = [0; 4096];
    loop {
        let len = socket.recv(&mut buf, None).unwrap();

        let mut nlmsghdr = neli::consts::nl::Nlmsghdr::new();
        let mut nlmsg_len = len;

        // 遍历所有Netlink消息
        while neli::consts::nl::Nlmsg::ok(&nlmsghdr, nlmsg_len) {
            let message = n



madrehongs@hotmail.com
继续

对不起，我的回答似乎中途被截断了。以下是完整的Rust程序：

rust
Copy code
use neli::{
    consts::SocketFlags,
    rtnl::{Taskstats, TaskstatsMessage},
    socket::NlSocketHandle,
};
use std::{
    ffi::CString,
    io::Read,
    os::unix::{
        io::{AsRawFd, RawFd},
        net::UnixStream,
    },
    thread,
};
use string_template::Template;

// 定义一个结构体来保存进程信息
struct ProcessInfo {
    pid: i32,
    cmdline: String,
}

fn main() {
    // 建立Netlink socket
    let socket = NlSocketHandle::connect(NeliProtocol::Route, None, None, SocketFlags::empty())
        .expect("Failed to connect to Netlink socket");

    // 向内核注册TASKSTATS类别的Netlink事件
    let mut message = TaskstatsMessage::default();
    message.header.nlmsg_type = TASKSTATS_TYPE_PID;
    message.header.nlmsg_flags |= NLM_F_REQUEST | NLM_F_DUMP;
    message.header.nlmsg_len = message.header.nlmsg_len();
    message.tkl_pid = 0;
    message.tkl_stats = 0xffffffff;

    socket
        .send(&message, None)
        .expect("Failed to send Netlink message");

    // 建立与父进程通信的Unix域套接字
    let mut stream = UnixStream::connect("/tmp/proc_info.sock").unwrap();
    let fd = stream.as_raw_fd();

    // 建立进程信息获取线程
    thread::spawn(move || {
        let mut buf = [0; 4096];
        loop {
            let mut taskstats = Taskstats::default();

            // 从Unix域套接字读取进程ID
            let mut pid = 0;
            unsafe {
                let len = libc::read(
                    fd,
                    &mut pid as *mut i32 as *mut libc::c_void,
                    std::mem::size_of::<i32>(),
                );
                if len != std::mem::size_of::<i32>() as isize {
                    panic!("Error reading from pipe");
                }
            }

            // 通过/proc文件系统获取进程完整路径和参数信息
            let mut cmdline = String::new();
            let mut file = std::fs::File::open(format!("/proc/{}/cmdline", pid)).unwrap();
            file.read_to_string(&mut cmdline).unwrap();
            cmdline = cmdline.replace("\0", " ").trim().to_string();

            // 将进程信息写入Unix域套接字
            let info = ProcessInfo { pid, cmdline };
            let template = Template::new("{pid} {cmdline}");
            let message = template.render(&info).unwrap();
            stream.write_all(message.as_bytes()).unwrap();
        }
    });

    // 处理Netlink消息并输出进程信息
    let mut buf = [0; 4096];
    loop {
        let len = socket.recv(&mut buf, None).unwrap();

        let mut nlmsghdr = neli::consts::nl::Nlmsghdr::new();
        let mut nlmsg_len = len;

        // 遍历所有Netlink消息
        while neli::consts::nl::Nlmsg::ok(&nlmsghdr, nlmsg_len) {
            let message = TaskstatsMessage::deserialize(&buf[nlmsghdr



madrehongs@hotmail.com
继续

// 遍历所有Netlink消息
while neli::consts::nl::Nlmsg::ok(&nlmsghdr, nlmsg_len) {
let message = TaskstatsMessage::deserialize(&buf[nlmsghdr.nlmsg_len as usize..]);
if let Ok(message) = message {
if let Some(taskstats) = message.payload.taskstats() {
// 将进程ID写入Unix域套接字
unsafe {
libc::write(
fd,
&taskstats.ac_pid as *const u32 as *const libc::c_void,
std::mem::size_of::<u32>() as usize,
);
}
}
}
            // 指向下一个Netlink消息
            nlmsg_len -= neli::consts::nl::Nlmsg::payload_size(&nlmsghdr) + nlmsghdr.nlmsg_len;
            nlmsghdr = neli::consts::nl::Nlmsghdr::next(&nlmsghdr, nlmsg_len);
        }
    }
}
