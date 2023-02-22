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
