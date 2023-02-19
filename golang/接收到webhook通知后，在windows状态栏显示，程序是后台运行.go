package main

import (
	"encoding/json"
	"log"
	"net/http"

	"github.com/wailsapp/wails"
)

type webhookData struct {
	Title   string `json:"title"`
	Message string `json:"message"`
}

func main() {
	app := wails.CreateApp(&wails.AppConfig{
		Width:     1024,
		Height:    768,
		Title:     "My Wails App",
		JS:        "dist/main.js",
		CSS:       "dist/main.css",
		Colour:    "#131313",
		Resizable: true,
	})

	app.Bind(webhook) // 绑定 webhook 函数

	app.Window.SetVisible(false) // 隐藏窗口
	app.Window.SetStatusBar(&wails.StatusBar{
		Visible: true,
		Menu: []wails.MenuItem{
			wails.MenuItem{
				Text:    "Exit",
				Tooltip: "Exit the application",
				OnClick: func() {
					app.Quit()
				},
			},
		},
		Icon: "assets/icon.png",
	})

	if err := app.Run(); err != nil {
		log.Fatal(err)
	}
}

func webhook(payload string) {
	var data webhookData
	err := json.Unmarshal([]byte(payload), &data)
	if err != nil {
		log.Printf("Error: %v\n", err)
		return
	}

	// 在状态栏显示通知
	notify(data.Title, data.Message)

	// 在控制台输出消息
	log.Println("Webhook received:", data.Title, data.Message)
}

func notify(title string, message string) {
	// 实现通知逻辑，例如使用 github.com/gen2brain/beeep 库实现通知
	beeep.Notify(title, message, "assets/icon.png")
}

func handleWebhook(w http.ResponseWriter, r *http.Request) {
	if r.Method != "POST" {
		http.Error(w, "Invalid request method", http.StatusMethodNotAllowed)
		return
	}

	// 读取请求 body 中的数据
	payload := r.FormValue("payload")

	// 处理 Webhook 消息
	webhook(payload)
}

func startServer() {
	http.HandleFunc("/webhook", handleWebhook)
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatal("Server error: ", err)
	}
}
