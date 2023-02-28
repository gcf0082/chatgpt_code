package main

import (
	"fmt"
	"net/http"
	"os"
)

func main() {
	socketFile := "/tmp/myapp.sock"

	// 检查 socket 文件是否存在，如果存在则删除
	if _, err := os.Stat(socketFile); err == nil {
		if err := os.Remove(socketFile); err != nil {
			fmt.Printf("Failed to remove socket file: %v\n", err)
			return
		}
	}

	// 创建监听 socket
	listener, err := net.Listen("unix", socketFile)
	if err != nil {
		fmt.Printf("Failed to create socket: %v\n", err)
		return
	}

	// 注册 HTTP 处理函数
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "Hello, World!")
	})

	// 启动 HTTP 服务器并监听连接
	err = http.Serve(listener, nil)
	if err != nil {
		fmt.Printf("Failed to start server: %v\n", err)
		return
	}
}
