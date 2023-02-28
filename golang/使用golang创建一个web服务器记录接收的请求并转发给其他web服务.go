package main

import (
	"bytes"
	"fmt"
	"io/ioutil"
	"log"
	"net/http"
	"time"
)

func main() {
	http.HandleFunc("/", handler)
	log.Fatal(http.ListenAndServe(":8080", nil))
}

func handler(w http.ResponseWriter, r *http.Request) {
	// 记录接收请求的时间戳
	startTime := time.Now()

	// 读取请求的 body
	body, err := ioutil.ReadAll(r.Body)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadRequest)
		return
	}
	defer r.Body.Close()

	// 异步发送 HTTP 请求
	go sendRequestAsync(body)

	// 发送响应
	fmt.Fprintln(w, "Hello, World!")

	// 计算处理请求的时间
	duration := time.Since(startTime)
	log.Printf("request processed in %v", duration)
}

func sendRequestAsync(body []byte) {
	// 创建 HTTP 客户端
	client := &http.Client{Timeout: 10 * time.Second}

	// 创建 HTTP 请求
	req, err := http.NewRequest("POST", "http://example.com", bytes.NewBuffer(body))
	if err != nil {
		log.Println(err)
		return
	}

	// 发送 HTTP 请求
	resp, err := client.Do(req)
	if err != nil {
		log.Println(err)
		return
	}
	defer resp.Body.Close()

	// 读取 HTTP 响应
	// ...
}
