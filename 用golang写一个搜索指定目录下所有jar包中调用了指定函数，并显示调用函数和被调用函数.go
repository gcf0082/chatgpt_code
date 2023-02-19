package main

import (
    "archive/zip"
    "bufio"
    "fmt"
    "os"
    "path/filepath"
    "regexp"
    "strings"

    "github.com/lesovsky/pgcenter/tools/deps/asm"
    "github.com/lesovsky/pgcenter/tools/deps/asm-util"
)

func main() {
    // 要搜索的目录和函数名称
    searchDir := "path/to/dir"
    functionName := "com.example.MyClass.myMethod"

    // 匹配类名和函数名的正则表达式
    classRegexp := regexp.MustCompile(`(.+)\.class$`)
    methodRegexp := regexp.MustCompile(`([^(]+)\((.*)\)(.*)`)

    // 遍历目录中的所有 jar 文件
    err := filepath.Walk(searchDir, func(path string, info os.FileInfo, err error) error {
        if err != nil {
            return err
        }

        if info.IsDir() {
            return nil
        }

        if filepath.Ext(path) != ".jar" {
            return nil
        }

        // 打开 jar 文件并遍历其中的所有类文件
        r, err := zip.OpenReader(path)
        if err != nil {
            return err
        }
        defer r.Close()

        for _, f := range r.File {
            if !classRegexp.MatchString(f.Name) {
                continue
            }

            // 读取类文件内容
            rc, err := f.Open()
            if err != nil {
                return err
            }
            defer rc.Close()

            scanner := bufio.NewScanner(rc)
            for scanner.Scan() {
                line := scanner.Text()

                // 使用 asm 解析类文件
                classReader := &util.ClassReader{Data: []byte(line)}
                classFile, err := classReader.Parse()
                if err != nil {
                    continue
                }

                // 检查是否调用了指定的函数
                for _, method := range classFile.Methods {
                    if !strings.HasPrefix(method.Name, functionName) {
                        continue
                    }

                    // 输出调用函数和被调用函数的信息
                    fmt.Printf("Call: %s\n", method.Name)
                    for _, insn := range method.Instructions {
                        if insn.Opcode == asm.OpInvokevirtual || insn.Opcode == asm.OpInvokespecial || insn.Opcode == asm.OpInvokeinterface || insn.Opcode == asm.OpInvokestatic {
                            match := methodRegexp.FindStringSubmatch(insn.Operand)
                            if match != nil {
                                fmt.Printf("    -> Called method: %s%s\n", match[1], match[2])
                            }
                        }
                    }
                }
            }

            if err := scanner.Err(); err != nil {
                return err
            }
        }

        return nil
    })

    if err != nil {
        fmt.Println(err)
        os.Exit(1)
    }
}
