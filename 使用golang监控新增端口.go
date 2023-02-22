package main

import (
    "fmt"
    "github.com/mdlayher/netlink"
    "syscall"
)

const (
    ProtoTCP = 6
    ProtoUDP = 17
)

func main() {
    // 打开 Netlink Socket
    conn, err := netlink.Dial(syscall.NETLINK_INET_DIAG, nil)
    if err != nil {
        fmt.Println("failed to dial netlink socket:", err)
        return
    }
    defer conn.Close()

    // 创建 Netlink 消息
    msg := netlink.Message{
        Header: netlink.Header{
            Type:  syscall.NLMSG_DONE,
            Flags: netlink.Request | netlink.Acknowledge | syscall.NLM_F_DUMP,
        },
        Data: createInetDiagMsg(ProtoTCP, 0),
    }

    // 发送 Netlink 消息
    err = conn.Send(&msg)
    if err != nil {
        fmt.Println("failed to send netlink message:", err)
        return
    }

    // 接收 Netlink 消息
    msgs, err := conn.Receive()
    if err != nil {
        fmt.Println("failed to receive netlink message:", err)
        return
    }

    // 处理 Netlink 消息
    for _, msg := range msgs {
        if msg.Header.Type == syscall.NLMSG_ERROR {
            // 出现错误
            errno := int32(nlPayloadInt(msg.Data[:4]))
            if errno == 0 {
                continue
            }
            err = syscall.Errno(-errno)
            fmt.Println("received netlink error:", err)
            return
        }
        if msg.Header.Type == syscall.NLMSG_DONE {
            // 完成处理
            break
        }

        // 解析消息内容，提取端口号信息
        port := int(nlPayloadInt(msg.Data[32:34]))

        // 处理端口号信息
        fmt.Println("Port:", port)
    }
}

func createInetDiagMsg(proto, state int) []byte {
    // 构造 InetDiagReqV2 结构体
    msg := make([]byte, syscall.SizeofInetDiagReqV2)
    req := (*syscall.InetDiagReqV2)(unsafe.Pointer(&msg[0]))
    req.Family = syscall.AF_INET
    req.Protocol = uint8(proto)
    req.Ext = syscall.INET_DIAG_INFO | syscall.INET_DIAG_VEGASINFO
    req.State = uint8(state)
    req.IDiagExt = 1

    return msg
}

func nlPayloadInt(b []byte) uint32 {
    var i uint32
    for j := len(b) - 1; j >= 0; j-- {
        i <<= 8
        i |= uint32(b[j])
    }
    return i
}
