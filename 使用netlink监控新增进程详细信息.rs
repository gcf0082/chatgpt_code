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

//neli = "0.9.3"
//string_template = "0.6.0"
