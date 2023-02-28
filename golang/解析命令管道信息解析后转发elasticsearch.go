package main

import (
	"bufio"
	"context"
	"encoding/json"
	"fmt"
	"log"
	"os"
	"strings"

	"github.com/elastic/go-elasticsearch/v7"
	"github.com/elastic/go-elasticsearch/v7/esapi"
)

func main() {
	// 创建 Elasticsearch 客户端
	es, err := elasticsearch.NewDefaultClient()
	if err != nil {
		log.Fatalf("无法创建 Elasticsearch 客户端：%s", err)
	}

	// 从标准输入读取并传递到管道
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		line := scanner.Text()
		// 解析命令行输出为 JSON 格式
		fields := strings.Fields(line)
		if len(fields) < 9 {
			continue
		}
		data := map[string]interface{}{
			"mode":     fields[0],
			"nlink":    fields[1],
			"owner":    fields[2],
			"group":    fields[3],
			"size":     fields[4],
			"modified": fmt.Sprintf("%s %s %s", fields[5], fields[6], fields[7]),
			"name":     fields[8],
		}
		jsonData, err := json.Marshal(data)
		if err != nil {
			log.Printf("无法解析命令行输出：%s", err)
			continue
		}

		// 发送到 Elasticsearch
		req := esapi.IndexRequest{
			Index: "my-index",
			Body:  strings.NewReader(string(jsonData)),
		}
		res, err := req.Do(context.Background(), es)
		if err != nil {
			log.Printf("无法发送数据到 Elasticsearch：%s", err)
			continue
		}
		defer res.Body.Close()
		if res.IsError() {
			log.Printf("Elasticsearch 错误：%s", res.String())
			continue
		}
		log.Println("数据已成功发送到 Elasticsearch")
	}

	if err := scanner.Err(); err != nil {
		log.Fatalf("标准输入扫描错误：%s", err)
	}
}
