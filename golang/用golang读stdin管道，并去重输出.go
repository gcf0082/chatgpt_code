package main

import (
    "bufio"
    "fmt"
    "os"
)

func main() {
    seen := make(map[string]bool)
    scanner := bufio.NewScanner(os.Stdin)

    for scanner.Scan() {
        line := scanner.Text()
        if !seen[line] {
            seen[line] = true
            fmt.Println(line)
        }
    }

    if err := scanner.Err(); err != nil {
        fmt.Fprintln(os.Stderr, "error reading standard input:", err)
    }
}
