package main

import (
	"fmt"
	"net/http"
	"time"

	"github.com/google/martian"
	"github.com/google/martian/log"
	"github.com/google/martian/proxy"
	"github.com/google/martian/verify"
)

func main() {
	// 创建martian调用栈
	stack, _ := martian.NewStack("martian-example", martian.NewLogger(log.Standard()))

	// 使用 martian.RequestModifier 实现前置处理，记录开始接收请求时间
	stack.AddRequestModifier(martian.RequestModifierFunc(func(req *http.Request) error {
		req.Header.Set("X-Request-Start-Time", time.Now().Format(time.RFC3339))
		return nil
	}))

	// 使用 martian.ResponseModifier 实现后置处理，记录响应时间
	stack.AddResponseModifier(martian.ResponseModifierFunc(func(res *http.Response) error {
		res.Header.Set("X-Response-Time", time.Now().Format(time.RFC3339))
		return nil
	}))

	// 创建反向代理
	reverseProxy := proxy.NewHTTPProxy()

	// 使用 VerifyRequests Middleware，确保所有请求都被正确处理
	stack.AddRequestModifier(verify.NewRequestsMiddleware(reverseProxy))

	// 添加反向代理到调用栈
	stack.SetRoundTripper(reverseProxy)

	// 创建HTTP服务器并使用martian作为handler
	server := &http.Server{
		Addr:    ":8080",
		Handler: stack,
	}

	// 启动服务器
	fmt.Println("Starting server on :8080")
	if err := server.ListenAndServe(); err != nil {
		fmt.Printf("Server error: %v", err)
	}
}
