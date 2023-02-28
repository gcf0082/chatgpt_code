package main

import (
	"fmt"
	"io/ioutil"
	"net"
	"net/http"
	"os"
)

func main() {
	socketFile := "/tmp/myapp.sock"

	// 连接 Unix Domain Socket
	conn, err := net.Dial("unix", socketFile)
	if err != nil {
		fmt.Printf("Failed to connect to socket: %v\n", err)
		return
	}
	defer conn.Close()

	// 构造 HTTP 请求
	req, err := http.NewRequest("GET", "http://localhost/", nil)
	if err != nil {
		fmt.Printf("Failed to create request: %v\n", err)
		return
	}

	// 发送 HTTP 请求
	client := &http.Client{
		Transport: &http.Transport{
			Dial: func(_, _ string) (net.Conn, error) {
				return conn, nil
			},
		},
	}
	resp, err := client.Do(req)
	if err != nil {
		fmt.Printf("Failed to send request: %v\n", err)
		return
	}
	defer resp.Body.Close()

	// 读取响应内容
	body, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		fmt.Printf("Failed to read response: %v\n", err)
		return
	}

	fmt.Printf("Response: %s\n", body)
}
