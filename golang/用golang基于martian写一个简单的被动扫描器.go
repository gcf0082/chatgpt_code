package main

import (
	"bytes"
	"context"
	"io/ioutil"
	"log"
	"net/http"
	"time"

	"github.com/google/martian"
	"github.com/google/martian/log"
	"github.com/google/martian/martianhttp"
)

func main() {
	// 创建martian代理
	m := martian.NewRegistry()

	// 创建martian日志记录器
	l := martian.NewLogger(log.New())

	// 创建一个HTTP请求拦截器
	mux := martian.NewServeMux()
	mux.HandleFunc("/", func(res http.ResponseWriter, req *http.Request) {
		log.Infof("Received request: %v", req)

		// 修改请求
		if req.Method == http.MethodPost {
			body, err := ioutil.ReadAll(req.Body)
			if err != nil {
				log.Errorf("Failed to read request body: %v", err)
				http.Error(res, "Internal server error", http.StatusInternalServerError)
				return
			}

			// 修改请求体
			body = bytes.Replace(body, []byte("foo"), []byte("bar"), -1)

			req.Body = ioutil.NopCloser(bytes.NewReader(body))
		}

		// 传递请求到下一个过滤器或发送到目标服务器
		mux.ServeHTTP(res, req)
	})

	// 将HTTP请求拦截器添加到martian代理中
	mustAddRequestModifier := martianhttp.NewModifier("modify requests", mux)
	m.MustAddRequestModifier(mustAddRequestModifier)

	// 创建HTTP代理处理程序
	proxy := martianhttp.NewProxy()

	// 将martian日志记录器添加到HTTP代理处理程序中
	proxy.SetLogger(l)

	// 创建HTTP服务器
	server := &http.Server{
		Addr:    ":8080",
		Handler: proxy,
	}

	// 启动HTTP服务器
	go func() {
		if err := server.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			log.Fatalf("HTTP server ListenAndServe: %v", err)
		}
	}()

	// 等待1秒钟
	time.Sleep(1 * time.Second)

	// 创建一个HTTP客户端
	client := &http.Client{}

	// 发送一个HTTP GET请求
	req, err := http.NewRequest("GET", "http://example.com", nil)
	if err != nil {
		log.Fatalf("Failed to create HTTP request: %v", err)
	}

	res, err := client.Do(req)
	if err != nil {
		log.Fatalf("Failed to send HTTP request: %v", err)
	}

	// 打印响应体
	log.Infof("Response: %v", res)

	// 发送一个HTTP POST请求
	req, err = http.NewRequest("POST", "http://example.com", bytes.NewBufferString("foo"))
	if err != nil {
		log.Fatalf("Failed to create HTTP request: %v", err)
	}

res, err = client.Do(req)
if err != nil {
	log.Fatalf("Failed to send HTTP request: %v", err)
}

// 打印响应体
log.Infof("Response: %v", res)

// 修改请求并重新发送
if res.StatusCode == http.StatusOK {
	body, err := ioutil.ReadAll(res.Body)
	if err != nil {
		log.Errorf("Failed to read response body: %v", err)
		return
	}

	// 修改响应体
	body = bytes.Replace(body, []byte("bar"), []byte("foo"), -1)

	// 创建新的HTTP POST请求
	req, err = http.NewRequest("POST", "http://example.com", bytes.NewReader(body))
	if err != nil {
		log.Fatalf("Failed to create HTTP request: %v", err)
	}

	// 重新发送HTTP POST请求
	res, err = client.Do(req)
	if err != nil {
		log.Fatalf("Failed to send HTTP request: %v", err)
	}

	// 打印修改后的响应体
	log.Infof("Modified response: %v", res)
}

// 关闭HTTP响应体
res.Body.Close()

// 关闭HTTP服务器
ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
defer cancel()

if err := server.Shutdown(ctx); err != nil {
	log.Fatalf("HTTP server Shutdown: %v", err)
}
