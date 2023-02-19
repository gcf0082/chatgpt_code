package main

import (
	"bufio"
	"fmt"
	"os"
	"os/exec"
	"strings"
)

func main() {
	args := os.Args[1:]
	if len(args) == 0 {
		fmt.Println("Usage: rlwrap command [arguments]")
		os.Exit(1)
	}

	cmd := exec.Command(args[0], args[1:]...)
	cmd.Stdin = os.Stdin
	cmd.Stdout = os.Stdout

	rl := bufio.NewReader(os.Stdin)
	for {
		fmt.Print("> ")
		line, err := rl.ReadString('\n')
		if err != nil {
			break
		}

		line = strings.TrimSpace(line)
		if line == "" {
			continue
		}

		if _, err = cmd.Stdin.Write([]byte(line + "\n")); err != nil {
			break
		}

		if err = cmd.Run(); err != nil {
			fmt.Println("Error:", err)
			break
		}
	}
}
