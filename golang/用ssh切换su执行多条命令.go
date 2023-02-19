package main

import (
	"bufio"
	"flag"
	"fmt"
	"golang.org/x/crypto/ssh"
	"log"
	"os"
	"strings"
)

func main() {
	// 解析命令行参数
	host := flag.String("host", "example.com", "SSH server host")
	port := flag.Int("port", 22, "SSH server port")
	user := flag.String("user", "", "SSH server username")
	password := flag.String("password", "", "SSH server password")
	newUser := flag.String("newuser", "", "New SSH server username")
	newPassword := flag.String("newpassword", "", "New SSH server password")
	flag.Parse()

	// 检查参数是否为空
	if *user == "" || *password == "" || *newUser == "" || *newPassword == "" {
		log.Fatal("Missing required arguments")
	}

	// SSH 客户端配置
	config := &ssh.ClientConfig{
		User: *user,
		Auth: []ssh.AuthMethod{
			ssh.Password(*password),
		},
		HostKeyCallback: ssh.InsecureIgnoreHostKey(),
	}

	// 建立 SSH 连接
	conn, err := ssh.Dial("tcp", fmt.Sprintf("%s:%d", *host, *port), config)
	if err != nil {
		log.Fatalf("Failed to connect to host %s:%d: %v", *host, *port, err)
	}
	defer conn.Close()

	// 执行 su 切换用户
	session, err := conn.NewSession()
	if err != nil {
		log.Fatalf("Failed to create session: %v", err)
	}
	defer session.Close()
	stdin, err := session.StdinPipe()
	if err != nil {
		log.Fatalf("Failed to create stdin pipe: %v", err)
	}
	stdout, err := session.StdoutPipe()
	if err != nil {
		log.Fatalf("Failed to create stdout pipe: %v", err)
	}
	stderr, err := session.StderrPipe()
	if err != nil {
		log.Fatalf("Failed to create stderr pipe: %v", err)
	}
	err = session.Start("su " + *newUser)
	if err != nil {
		log.Fatalf("Failed to start su session: %v", err)
	}

	// 输入新用户密码
	scanner := bufio.NewScanner(stderr)
	for scanner.Scan() {
		text := scanner.Text()
		if strings.Contains(text, "Password:") {
			fmt.Fprintln(stdin, *newPassword)
			break
		}
	}

	// 执行多个命令
	cmds := []string{
		"whoami",
		"ls /",
		"echo 'Hello, world!'",
	}

	for _, cmd := range cmds {
		// 在 su 会话中执行命令
		in, err := session.StdinPipe()
		if err != nil {
			log.Fatalf("Failed to open stdin pipe: %v", err)
		}
		defer in.Close()
		out, err := session.StdoutPipe()
	
if err != nil {
			log.Fatalf("Failed to open stdout pipe: %v", err)
		}
		defer out.Close()

		err = session.Start(cmd)
		if err != nil {
			log.Fatalf("Failed to start command %q: %v", cmd, err)
		}
		_, err = in.Write([]byte(*newPassword + "\n"))
		if err != nil {
			log.Fatalf("Failed to write password: %v", err)
		}
		err = in.Close()
		if err != nil {
			log.Fatalf("Failed to close stdin: %v", err)
		}
		scanner := bufio.NewScanner(out)
		for scanner.Scan() {
			fmt.Println(scanner.Text())
		}
		err = session.Wait()
		if err != nil {
			log.Fatalf("Failed to wait for command %q: %v", cmd, err)
		}
	}

	// 保持会话不关闭
	select {}
}
