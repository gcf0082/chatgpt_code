Go语言中的文件操作常用代码如下：
1. 打开文件
```go
file, err := os.Open("filename.txt")
if err != nil {
    // 处理错误
    return
}
defer file.Close()
```
2. 写入文件
```go
file, err := os.Create("filename.txt")
if err != nil {
    // 处理错误
    return
}
defer file.Close()
_, err = file.WriteString("Hello, world!")
if err != nil {
    // 处理错误
    return
}
```
3. 读取文件
```go
file, err := os.Open("filename.txt")
if err != nil {
    // 处理错误
    return
}
defer file.Close()
data := make([]byte, 1024)
count, err := file.Read(data)
if err != nil {
    // 处理错误
    return
}
fmt.Println(string(data[:count]))
```
4. 删除文件
```go
err := os.Remove("filename.txt")
if err != nil {
    // 处理错误
    return
}
```
5. 判断文件是否存在
```go
_, err := os.Stat("filename.txt")
if err == nil {
    // 文件存在
} else if os.IsNotExist(err) {
    // 文件不存在
} else {
    // 处理错误
    return
}
```
6. 获取文件信息
```go
fileInfo, err := os.Stat("filename.txt")
if err != nil {
    // 处理错误
    return
}
fmt.Println("文件大小：", fileInfo.Size())
fmt.Println("修改时间：", fileInfo.ModTime())
```
7. 重命名文件
```go
err := os.Rename("oldname.txt", "newname.txt")
if err != nil {
    // 处理错误
    return
}
```
8. 创建目录
```go
err := os.Mkdir("dirname", 0755)
if err != nil {
    // 处理错误
    return
}
```
9. 遍历目录
```go
dir, err := os.Open("dirname")
if err != nil {
    // 处理错误
    return
}
defer dir.Close()
fileInfos, err := dir.Readdir(-1)
if err != nil {
    // 处理错误
    return
}
for _, fileInfo := range fileInfos {
    fmt.Println(fileInfo.Name())
}
```
10. 递归遍历目录
```go
func traverseDir(path string) {
    dir, err := os.Open(path)
    if err != nil {
        // 处理错误
        return
    }
    defer dir.Close()
    fileInfos, err := dir.Readdir(-1)
    if err != nil {
        // 处理错误
        return
    }
    for _, fileInfo := range fileInfos {
        if fileInfo.IsDir() {
            traverseDir(path + "/" + fileInfo.Name())
        } else {
            fmt.Println(path + "/" + fileInfo.Name())
        }
    }
}
```
11. 文件拷贝
```go
srcFile, err := os.Open("sourcefile.txt")
if err != nil {
    // 处理错误
    return
}
defer srcFile.Close()
dstFile, err := os.Create("destfile.txt")
if err != nil {
    // 处理错误
    return
}
defer dstFile.Close()
_, err = io.Copy(dstFile, srcFile)
if err != nil {
    // 处理错误
    return
}
```
12. 创建临时文件
```go
tempFile, err := ioutil.TempFile("", "prefix")
if err != nil {
    // 处理错误
    return
}
defer tempFile.Close()
fmt.Println(tempFile.Name())
```
13. 读取文件内容到内存
```go
data, err := ioutil.ReadFile("filename.txt")
if err != nil {
    // 处理错误
    return
}
fmt.Println(string(data))
```
14. 写入内存内容到文件
```go
data := []byte("Hello, world!")
err := ioutil.WriteFile("filename.txt", data, 0644)
if err != nil {
    // 处理错误
    return
}
```
15. 读取命令行参数
```go
args := os.Args
fmt.Println(args)
```
16. 获取文件信息
```go
fileInfo, err := os.Stat("filename.txt")
if err != nil {
    // 处理错误
    return
}
fmt.Println(fileInfo.Name())
fmt.Println(fileInfo.Size())
fmt.Println(fileInfo.Mode())
fmt.Println(fileInfo.ModTime())
fmt.Println(fileInfo.IsDir())
```
17. 判断文件或目录是否存在
```go
if _, err := os.Stat("filename.txt"); err == nil {
    fmt.Println("file exists")
} else {
    fmt.Println("file does not exist")
}
if _, err := os.Stat("dirname"); err == nil {
    fmt.Println("directory exists")
} else {
    fmt.Println("directory does not exist")
}
```
18. 获取当前工作目录
```go
dir, err := os.Getwd()
if err != nil {
    // 处理错误
    return
}
fmt.Println(dir)
```
19. 改变工作目录
```go
err := os.Chdir("newdir")
if err != nil {
    // 处理错误
    return
}
```
20. 获取环境变量
```go
fmt.Println(os.Getenv("PATH"))
```
21. 执行系统命令
```go
cmd := exec.Command("ls", "-l")
output, err := cmd.Output()
if err != nil {
    // 处理错误
    return
}
fmt.Println(string(output))
```
22. 杀死进程
```go
cmd := exec.Command("sleep", "60")
err := cmd.Start()
if err != nil {
    // 处理错误
    return
}
err = cmd.Process.Kill()
if err != nil {
    // 处理错误
    return
}
```
23. 管道传输数据
```go
cmd1 := exec.Command("echo", "hello")
cmd2 := exec.Command("wc", "-c")
pipe, err := cmd1.StdoutPipe()
if err != nil {
    // 处理错误
    return
}
defer pipe.Close()
cmd1.Start()
cmd2.Stdin = pipe
output, err := cmd2.Output()
if err != nil {
    // 处理错误
    return
}
fmt.Println(string(output))
```
24. 获取系统环境信息
```go
fmt.Println(runtime.GOOS)
fmt.Println(runtime.GOARCH)
```
25. 获取系统CPU信息
```go
cpuInfo, err := cpu.Info()
if err != nil {
    // 处理错误
    return
}
for _, info := range cpuInfo {
    fmt.Println(info.ModelName)
    fmt.Println(info.Cores)
}
```
26. 获取系统内存信息
```go
memInfo, err := mem.VirtualMemory()
if err != nil {
    // 处理错误
    return
}
fmt.Println(memInfo.Total)
fmt.Println(memInfo.Used)
fmt.Println(memInfo.Free)
fmt.Println(memInfo.SwapTotal)
fmt.Println(memInfo.SwapUsed)
fmt.Println(memInfo.SwapFree)
```
27. 获取系统磁盘信息
```go
diskInfo, err := disk.Usage("/")
if err != nil {
    // 处理错误
    return
}
fmt.Println(diskInfo.Total)
fmt.Println(diskInfo.Used)
fmt.Println(diskInfo.Free)
fmt.Println(diskInfo.UsedPercent)
```
28. 获取系统网络信息
```go
netInfo, err := net.Interfaces()
if err != nil {
    // 处理错误
    return
}
for _, info := range netInfo {
    fmt.Println(info.Name)
    fmt.Println(info.HardwareAddr)
    addrs, _ := info.Addrs()
    for _, addr := range addrs {
        fmt.Println(addr.String())
    }
}
```
29. 获取系统时间信息
```go
now := time.Now()
fmt.Println(now)
fmt.Println(now.Year())
fmt.Println(now.Month())
fmt.Println(now.Day())
fmt.Println(now.Hour())
fmt.Println(now.Minute())
fmt.Println(now.Second())
fmt.Println(now.Nanosecond())
```
30. 获取系统时区信息
```go
loc, err := time.LoadLocation("America/New_York")
if err != nil {
    // 处理错误
    return
}
fmt.Println(now.In(loc))
```
31. 读取文件内容
```go
data, err := ioutil.ReadFile("file.txt")
if err != nil {
    // 处理错误
    return
}
fmt.Println(string(data))
```
32. 写入文件内容
```go
data := []byte("hello world\n")
err := ioutil.WriteFile("file.txt", data, 0644)
if err != nil {
    // 处理错误
    return
}
```
33. 逐行读取文件内容
```go
file, err := os.Open("file.txt")
if err != nil {
    // 处理错误
    return
}
defer file.Close()
scanner := bufio.NewScanner(file)
for scanner.Scan() {
    fmt.Println(scanner.Text())
}
if err := scanner.Err(); err != nil {
    // 处理错误
    return
}
```
34. 逐行写入文件内容
```go
file, err := os.Create("file.txt")
if err != nil {
    // 处理错误
    return
}
defer file.Close()
writer := bufio.NewWriter(file)
data := []byte("hello world\n")
for i := 0; i < 10; i++ {
    writer.Write(data)
}
writer.Flush()
```
35. 读取JSON文件
```go
type Person struct {
    Name string `json:"name"`
    Age  int    `json:"age"`
}
var person Person
file, err := os.Open("file.json")
if err != nil {
    // 处理错误
    return
}
defer file.Close()
decoder := json.NewDecoder(file)
err = decoder.Decode(&person)
if err != nil {
    // 处理错误
    return
}
fmt.Println(person.Name)
fmt.Println(person.Age)
```
36. 发送HTTP GET请求
```go
resp, err := http.Get("http://example.com/")
if err != nil {
    // 处理错误
    return
}
defer resp.Body.Close()
body, err := ioutil.ReadAll(resp.Body)
if err != nil {
    // 处理错误
    return
}
fmt.Println(string(body))
```
37. 发送HTTP POST请求
```go
data := url.Values{}
data.Set("key", "value")
resp, err := http.PostForm("http://example.com/", data)
if err != nil {
    // 处理错误
    return
}
defer resp.Body.Close()
body, err := ioutil.ReadAll(resp.Body)
if err != nil {
    // 处理错误
    return
}
fmt.Println(string(body))
```
38. 自定义HTTP请求
```go
data := url.Values{}
data.Set("key", "value")
req, err := http.NewRequest("POST", "http://example.com/", strings.NewReader(data.Encode()))
if err != nil {
    // 处理错误
    return
}
req.Header.Set("Content-Type", "application/x-www-form-urlencoded")
client := &http.Client{}
resp, err := client.Do(req)
if err != nil {
    // 处理错误
    return
}
defer resp.Body.Close()
body, err := ioutil.ReadAll(resp.Body)
if err != nil {
    // 处理错误
    return
}
fmt.Println(string(body))
```
39. 使用模板生成HTML
```go
type Person struct {
    Name string
    Age  int
}
templateString := `
<html>
    <head>
        <title>{{.Name}}</title>
    </head>
    <body>
        <h1>{{.Name}}</h1>
        <p>Age: {{.Age}}</p>
    </body>
</html>
`
person := Person{
    Name: "John",
    Age:  30,
}
tmpl, err := template.New("person").Parse(templateString)
if err != nil {
    // 处理错误
    return
}
err = tmpl.Execute(os.Stdout, person)
if err != nil {
    // 处理错误
    return
}
```
40. 使用正则表达式匹配字符串
```go
re := regexp.MustCompile(`\d+`)
str := "123 foo 456"
matches := re.FindAllString(str, -1)
fmt.Println(matches)
```
41. 使用Go协程
```go
func worker(id int, jobs <-chan int, results chan<- int) {
    for j := range jobs {
        fmt.Println("worker", id, "processing job", j)
        time.Sleep(time.Second)
        results <- j * 2
    }
}
func main() {
    jobs := make(chan int, 100)
    results := make(chan int, 100)
    for w := 1; w <= 3; w++ {
        go worker(w, jobs, results)
    }
    for j := 1; j <= 5; j++ {
        jobs <- j
    }
    close(jobs)
    for a := 1; a <= 5; a++ {
        <-results
    }
}
```
42. 使用WaitGroup等待协程完成
```go
func worker(id int, wg *sync.WaitGroup) {
    defer wg.Done()
    fmt.Println("worker", id, "starting")
    time.Sleep(time.Second)
    fmt.Println("worker", id, "done")
}
func main() {
    var wg sync.WaitGroup
    for i := 1; i <= 5; i++ {
        wg.Add(1)
        go worker(i, &wg)
    }
    wg.Wait()
    fmt.Println("all workers done")
}
```
43. 使用Mutex进行互斥访问
```go
type SafeCounter struct {
    v   map[string]int
    mux sync.Mutex
}
func (c *SafeCounter) Inc(key string) {
    c.mux.Lock()
    c.v[key]++
    c.mux.Unlock()
}
func (c *SafeCounter) Value(key string) int {
    c.mux.Lock()
    defer c.mux.Unlock()
    return c.v[key]
}
func main() {
    c := SafeCounter{v: make(map[string]int)}
    for i := 0; i < 1000; i++ {
        go c.Inc("somekey")
    }
    time.Sleep(time.Second)
    fmt.Println(c.Value("somekey"))
}
```
44. 使用Select进行非阻塞通信
```go
func main() {
    c1 := make(chan string)
    c2 := make(chan string)
    go func() {
        time.Sleep(time.Second)
        c1 <- "one"
    }()
    go func() {
        time.Sleep(time.Second * 2)
        c2 <- "two"
    }()
    for i := 0; i < 2; i++ {
        select {
        case msg1 := <-c1:
            fmt.Println("received", msg1)
        case msg2 := <-c2:
            fmt.Println("received", msg2)
        }
    }
}
```
45. 使用Context进行协程控制
```go
func worker(ctx context.Context, id int, wg *sync.WaitGroup) error {
    defer wg.Done()
    for {
        select {
        default:
            fmt.Println("worker", id, "running")
            time.Sleep(time.Second)
        case <-ctx.Done():
            fmt.Println("worker", id, "done")
            return ctx.Err()
        }
    }
}
func main() {
    ctx, cancel := context.WithCancel(context.Background())
    var wg sync.WaitGroup
    for i := 1; i <= 5; i++ {
        wg.Add(1)
        go func(id int) {
            defer wg.Done()
            err := worker(ctx, id, &wg)
            if err != nil {
                fmt.Println("worker", id, "error:", err)
            }
        }(i)
    }
    time.Sleep(time.Second * 3)
    cancel()
    wg.Wait()
}
```
46. 使用Panic和Recover进行错误处理
```go
func main() {
    defer func() {
        if r := recover(); r != nil {
            fmt.Println("recovered from error:", r)
        }
    }()
    fmt.Println("start")
    panic("something went wrong")
    fmt.Println("end")
}
```
47. 使用defer进行资源清理
```go
f, err := os.Open("file.txt")
if err != nil {
    // 处理错误
    return
}
defer f.Close()
// 使用文件f进行操作
```
48. 使用Go测试框架进行单元测试
```go
func Sum(a, b int) int {
    return a + b
}
func TestSum(t *testing.T) {
    got := Sum(1, 2)
    want := 3
    if got != want {
        t.Errorf("Sum(1, 2) = %!d(MISSING); want %!d(MISSING)", got, want)
    }
}
```
49. 使用Go性能分析工具进行性能优化
```go
import "runtime/pprof"
func main() {
    f, err := os.Create("profile.prof")
    if err != nil {
        // 处理错误
        return
    }
    defer f.Close()
    err = pprof.StartCPUProfile(f)
    if err != nil {
        // 处理错误
        return
    }
    defer pprof.StopCPUProfile()
    // 进行需要分析的操作
}
```
50. 使用Go语言内置的命令行参数解析
```go
import "flag"
func main() {
    wordPtr := flag.String("word", "foo", "a string")
    numbPtr := flag.Int("numb", 42, "an int")
    boolPtr := flag.Bool("fork", false, "a bool")
    var svar string
    flag.StringVar(&svar, "svar", "bar", "a string var")
    flag.Parse()
    fmt.Println("word:", *wordPtr)
    fmt.Println("numb:", *numbPtr)
    fmt.Println("fork:", *boolPtr)
    fmt.Println("svar:", svar)
    fmt.Println("tail:", flag.Args())
}
```
51. 使用Go语言内置的HTTP库进行Web开发
```go
func hello(w http.ResponseWriter, req *http.Request) {
    fmt.Fprintf(w, "hello\n")
}
func headers(w http.ResponseWriter, req *http.Request) {
    for name, headers := range req.Header {
        for _, h := range headers {
            fmt.Fprintf(w, "%!v(MISSING): %!v(MISSING)\n", name, h)
        }
    }
}
func main() {
    http.HandleFunc("/hello", hello)
    http.HandleFunc("/headers", headers)
    http.ListenAndServe(":8080", nil)
}
```
52. 使用第三方Web框架进行Web开发
```go
func main() {
    router := gin.Default()
    router.GET("/ping", func(c *gin.Context) {
        c.JSON(200, gin.H{
            "message": "pong",
        })
    })
    router.Run(":8080")
}
```
53. 使用Go语言内置的SQL包进行数据库操作
```go
import (
    "database/sql"
    _ "github.com/go-sql-driver/mysql"
)
func main() {
    db, err := sql.Open("mysql", "user:password@tcp(127.0.0.1:3306)/database")
    if err != nil {
        // 处理错误
        return
    }
    defer db.Close()
    rows, err := db.Query("SELECT * FROM users")
    if err != nil {
        // 处理错误
        return
    }
    defer rows.Close()
    for rows.Next() {
        var id int
        var name string
        var email string
        err = rows.Scan(&id, &name, &email)
        if err != nil {
            // 处理错误
            return
        }
        fmt.Println(id, name, email)
    }
    err = rows.Err()
    if err != nil {
        // 处理错误
        return
    }
}
```
54. 使用ORM进行数据库操作
```go
import (
    "github.com/jinzhu/gorm"
    _ "github.com/jinzhu/gorm/dialects/mysql"
)
type User struct {
    gorm.Model
    Name  string
    Email string
}
func main() {
    db, err := gorm.Open("mysql", "user:password@tcp(127.0.0.1:3306)/database?charset=utf8&parseTime=True&loc=Local")
    if err != nil {
        // 处理错误
        return
    }
    defer db.Close()
    db.AutoMigrate(&User{})
    user := User{Name: "Bob", Email: "bob@example.com"}
    db.Create(&user)
    var users []User
    db.Find(&users)
    for _, u := range users {
        fmt.Println(u.ID, u.Name, u.Email)
    }
}
```
55. 使用Go语言内置的模板库进行模板渲染
```go
import (
    "html/template"
    "os"
)
type Person struct {
    Name string
    Age  int
}
func main() {
    t := template.Must(template.New("person").Parse("{{.Name}} is {{.Age}} years old."))
    p := Person{Name: "Bob", Age: 30}
    err := t.Execute(os.Stdout, p)
    if err != nil {
        // 处理错误
        return
    }
}
```
56. 使用Go语言的反射机制进行运行时类型检查和动态调用
```go
func main() {
    var x float64 = 3.4
    v := reflect.ValueOf(x)
    fmt.Println("type:", v.Type())
    fmt.Println("kind is float64:", v.Kind() == reflect.Float64)
    fmt.Println("value:", v.Float())
    p := reflect.ValueOf(&x)
    v = p.Elem()
    v.SetFloat(7.1)
    fmt.Println("value:", v.Float())
}
```
57. 使用Go语言的并发机制进行并发编程
```go
func main() {
    ch := make(chan string)
    go func() {
        ch <- "hello"
        ch <- "world"
    }()
    fmt.Println(<-ch)
    fmt.Println(<-ch)
}
```
58. 使用Go语言的锁机制进行并发控制
```go
import (
    "sync"
    "time"
)
func main() {
    var wg sync.WaitGroup
    var mu sync.Mutex
    var count int
    for i := 0; i < 10; i++ {
        wg.Add(1)
        go func() {
            defer wg.Done()
            mu.Lock()
            defer mu.Unlock()
            time.Sleep(time.Millisecond)
            count++
        }()
    }
    wg.Wait()
    fmt.Println(count)
}
```
59. 使用Go语言的Context机制进行上下文管理
```go
import (
    "context"
    "fmt"
    "time"
)
func main() {
    ctx, cancel := context.WithCancel(context.Background())
    go func() {
        time.Sleep(10 * time.Second)
        cancel()
    }()
    select {
    case <-time.After(5 * time.Second):
        fmt.Println("done")
    case <-ctx.Done():
        fmt.Println("cancelled")
    }
}
```
60. 使用Go语言的测试框架进行单元测试
```go
import (
    "testing"
)
func TestAdd(t *testing.T) {
    x := 1
    y := 2
    expected := 3
    actual := add(x, y)
    if actual != expected {
        t.Errorf("add(%!d(MISSING), %!d(MISSING)) = %!d(MISSING); expected %!d(MISSING)", x, y, actual, expected)
    }
}
```
61. 使用Go语言的性能调优工具进行性能优化
```go
import (
    "fmt"
    "runtime/pprof"
    "os"
)
func main() {
    f, err := os.Create("profile.pb")
    if err != nil {
        // 处理错误
        return
    }
    defer f.Close()
    err = pprof.StartCPUProfile(f)
    if err != nil {
        // 处理错误
        return
    }
    defer pprof.StopCPUProfile()
    // 运行需要测试的代码
    fmt.Println("hello world")
}
```
62. 使用Go语言的内存管理机制进行内存优化
```go
import (
    "fmt"
    "runtime"
)
func main() {
    var ms runtime.MemStats
    runtime.ReadMemStats(&ms)
    fmt.Printf("HeapAlloc = %!v(MISSING) MiB\n", ms.HeapAlloc/1024/1024)
}
```
63. 使用Go语言的错误处理机制进行错误处理
```go
import (
    "errors"
    "fmt"
)
func f() error {
    return errors.New("something went wrong")
}
func main() {
    if err := f(); err != nil {
        fmt.Println(err)
    }
}
```
64. 使用Go语言的日志库进行日志管理
```go
import (
    "log"
)
func main() {
    log.Println("hello world")
}
```
65. 使用Go语言的加密库进行加密操作
```go
import (
    "crypto/md5"
    "encoding/hex"
    "fmt"
)
func main() {
    data := []byte("hello world")
    hasher := md5.New()
    hasher.Write(data)
    digest := hasher.Sum(nil)
    fmt.Println(hex.EncodeToString(digest))
}
```
66. 使用Go语言的网络库进行网络编程
```go
import (
    "net"
    "fmt"
)
func main() {
    conn, err := net.Dial("tcp", "golang.org:80")
    if err != nil {
        // 处理错误
        return
    }
    defer conn.Close()
    fmt.Fprint(conn, "GET / HTTP/1.0\r\n\r\n")
}
```
67. 使用Go语言的HTTP库进行HTTP编程
```go
import (
    "net/http"
    "io/ioutil"
    "fmt"
)
func main() {
    resp, err := http.Get("http://golang.org/")
    if err != nil {
        // 处理错误
        return
    }
    defer resp.Body.Close()
    body, err := ioutil.ReadAll(resp.Body)
    if err != nil {
        // 处理错误
        return
    }
    fmt.Println(string(body))
}
```
68. 使用Go语言的JSON库进行JSON编解码
```go
import (
    "encoding/json"
    "fmt"
)
type Person struct {
    Name string `json:"name"`
    Age int `json:"age"`
}
func main() {
    data := []byte(`{"name":"Alice","age":20}`)
    var p Person
    err := json.Unmarshal(data, &p)
    if err != nil {
        // 处理错误
        return
    }
    fmt.Printf("Name: %!s(MISSING), Age: %!d(MISSING)\n", p.Name, p.Age)
}
```
69. 使用Go语言的数据库库进行数据库操作
```go
import (
    "database/sql"
    _ "github.com/go-sql-driver/mysql"
    "fmt"
)
func main() {
    db, err := sql.Open("mysql", "user:password@tcp(localhost:3306)/dbname")
    if err != nil {
        // 处理错误
        return
    }
    defer db.Close()
    rows, err := db.Query("SELECT name, age FROM people")
    if err != nil {
        // 处理错误
        return
    }
    defer rows.Close()
    for rows.Next() {
        var name string
        var age int
        err := rows.Scan(&name, &age)
        if err != nil {
            // 处理错误
            return
        }
        fmt.Printf("name: %!s(MISSING), age: %!d(MISSING)\n", name, age)
    }
}
```
70. 使用Go语言的模板库进行模板渲染
```go
import (
    "html/template"
    "os"
)
func main() {
    const tmpl = `<!DOCTYPE html>
    <html>
    <head>
        <title>{{.Title}}</title>
    </head>
    <body>
        <h1>{{.Heading}}</h1>
        <p>{{.Text}}</p>
    </body>
    </html>`
    t, err := template.New("page").Parse(tmpl)
    if err != nil {
        // 处理错误
        return
    }
    data := struct {
        Title string
        Heading string
        Text string
    }{
        "My Page",
        "Welcome!",
        "This is my page.",
    }
    err = t.Execute(os.Stdout, data)
    if err != nil {
        // 处理错误
        return
    }
}
```
71. 使用Go语言的并发机制进行并发编程
```go
import (
    "fmt"
    "sync"
)
func worker(id int, wg *sync.WaitGroup) {
    defer wg.Done()
    fmt.Printf("Worker %!d(MISSING) starting\n", id)
    // 执行工作
    fmt.Printf("Worker %!d(MISSING) done\n", id)
}
func main() {
    var wg sync.WaitGroup
    for i := 1; i <= 5; i++ {
        wg.Add(1)
        go worker(i, &wg)
    }
    wg.Wait()
    fmt.Println("All workers done")
}
```
72. 使用Go语言的通道进行通信
```go
import (
    "fmt"
)
func worker(id int, jobs <-chan int, results chan<- int) {
    for j := range jobs {
        fmt.Printf("Worker %!d(MISSING) processing job %!d(MISSING)\n", id, j)
        // 执行工作
        results <- j * 2
    }
}
func main() {
    jobs := make(chan int, 100)
    results := make(chan int, 100)
    for i := 1; i <= 5; i++ {
        go worker(i, jobs, results)
    }
    for j := 1; j <= 10; j++ {
        jobs <- j
    }
    close(jobs)
    for r := 1; r <= 10; r++ {
        <-results
    }
}
```
73. 使用Go语言的定时器进行定时任务
```go
import (
    "fmt"
    "time"
)
func main() {
    ticker := time.NewTicker(1 * time.Second)
    done := make(chan bool)
    go func() {
        for {
            select {
            case <-done:
                return
            case t := <-ticker.C:
                fmt.Println("Tick at", t)
            }
        }
    }()
    time.Sleep(5 * time.Second)
    ticker.Stop()
    done <- true
    fmt.Println("Ticker stopped")
}
```
74. 使用Go语言的上下文包进行上下文管理
```go
import (
    "context"
    "fmt"
    "time"
)
func worker(ctx context.Context) {
    for {
        select {
        case <-ctx.Done():
            fmt.Println("Worker done")
            return
        default:
            fmt.Println("Working...")
            time.Sleep(time.Second)
        }
    }
}
func main() {
    ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
    defer cancel()
    go worker(ctx)
    time.Sleep(10 * time.Second)
}
```
75. 使用Go语言的测试框架进行单元测试
```go
import (
    "testing"
)
func TestAddition(t *testing.T) {
    result := 1 + 2
    if result != 3 {
        t.Errorf("1 + 2 = %!d(MISSING); want 3", result)
    }
}
```
76. 使用Go语言的反射包进行反射操作
```go
import (
    "fmt"
    "reflect"
)
func main() {
    var x float64 = 3.14
    v := reflect.ValueOf(x)
    fmt.Println("Type:", v.Type())
    fmt.Println("Kind is float64:", v.Kind() == reflect.Float64)
    fmt.Printf("Value: %!v(MISSING)\n", v.Float())
}
```
77. 使用Go语言的json包进行JSON编解码
```go
import (
    "encoding/json"
    "fmt"
)
type Person struct {
    Name string `json:"name"`
    Age  int    `json:"age"`
}
func main() {
    p := Person{Name: "Alice", Age: 30}
    b, err := json.Marshal(p)
    if err != nil {
        panic(err)
    }
    fmt.Println(string(b))
    var p2 Person
    err = json.Unmarshal(b, &p2)
    if err != nil {
        panic(err)
    }
    fmt.Println(p2)
}
```
78. 使用Go语言的flag包进行命令行参数解析
```go
import (
    "flag"
    "fmt"
)
func main() {
    wordPtr := flag.String("word", "foo", "a string")
    numPtr := flag.Int("num", 42, "an int")
    boolPtr := flag.Bool("bool", false, "a bool")
    flag.Parse()
    fmt.Println("word:", *wordPtr)
    fmt.Println("num:", *numPtr)
    fmt.Println("bool:", *boolPtr)
    fmt.Println("tail:", flag.Args())
}
```
79. 使用Go语言的gin框架进行web开发
```go
import (
    "github.com/gin-gonic/gin"
)
func main() {
    r := gin.Default()
    r.GET("/hello/:name", func(c *gin.Context) {
        name := c.Param("name")
        c.JSON(200, gin.H{
            "message": "Hello " + name + "!",
        })
    })
    r.Run(":8080")
}
```
80. 使用Go语言的gorm框架进行ORM操作
```go
import (
    "gorm.io/driver/sqlite"
    "gorm.io/gorm"
)
type User struct {
    gorm.Model
    Name  string
    Email string
}
func main() {
    db, err := gorm.Open(sqlite.Open("test.db"), &gorm.Config{})
    if err != nil {
        panic("failed to connect database")
    }
    db.AutoMigrate(&User{})
    user := User{Name: "Alice", Email: "alice@example.com"}
    db.Create(&user)
    var users []User
    db.Find(&users)
    for _, u := range users {
        fmt.Println(u.Name, u.Email)
    }
}
```
81. 使用Go语言的cobra库进行命令行工具开发
```go
import (
    "fmt"
    "github.com/spf13/cobra"
)
func main() {
    var rootCmd = &cobra.Command{
        Use:   "hello",
        Short: "A brief description of your application",
        Long:  "A longer description of your application",
        Run: func(cmd *cobra.Command, args []string) {
            fmt.Println("Hello, world!")
        },
    }
    if err := rootCmd.Execute(); err != nil {
        fmt.Println(err)
        os.Exit(1)
    }
}
```
82. 使用Go语言的viper库进行配置文件读取
```go
import (
    "fmt"
    "github.com/spf13/viper"
)
func main() {
    viper.SetConfigName("config")
    viper.AddConfigPath(".")
    err := viper.ReadInConfig()
    if err != nil {
        panic(fmt.Errorf("Fatal error config file: %!s(MISSING) \n", err))
    }
    fmt.Println(viper.GetString("database.host"))
}
```
83. 使用Go语言的zap库进行日志记录
```go
import (
    "go.uber.org/zap"
)
func main() {
    logger, _ := zap.NewProduction()
    defer logger.Sync()
    logger.Info("Hello, world!")
}
```
84. 使用Go语言的jwt-go库进行JWT生成和验证
```go
import (
    "github.com/dgrijalva/jwt-go"
    "time"
)
type UserClaims struct {
    User string `json:"user"`
    jwt.StandardClaims
}
func main() {
    claims := UserClaims{
        User: "Alice",
        StandardClaims: jwt.StandardClaims{
            ExpiresAt: time.Now().Add(24 * time.Hour).Unix(),
        },
    }
    token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
    signedToken, _ := token.SignedString([]byte("secret"))
    fmt.Println(signedToken)
    parsedToken, _ := jwt.ParseWithClaims(signedToken, &UserClaims{}, func(token *jwt.Token) (interface{}, error) {
        return []byte("secret"), nil
    })
    if claims, ok := parsedToken.Claims.(*UserClaims); ok && parsedToken.Valid {
        fmt.Println(claims.User)
    }
}
```
85. 使用Go语言的gomock库进行单元测试
```go
import (
    "testing"
    "github.com/golang/mock/gomock"
)
type MyInterface interface {
    DoSomething() int
}
func MyFunction(i MyInterface) int {
    return i.DoSomething()
}
func TestMyFunction(t *testing.T) {
    ctrl := gomock.NewController(t)
    defer ctrl.Finish()
    mockObj := NewMockMyInterface(ctrl)
    mockObj.EXPECT().DoSomething().Return(42)
    result := MyFunction(mockObj)
    if result != 42 {
        t.Errorf("MyFunction() = %!d(MISSING); want 42", result)
    }
}
```
86. 使用Go语言的gin-jwt库进行JWT认证
```go
import (
    "github.com/appleboy/gin-jwt/v2"
    "github.com/gin-gonic/gin"
    "time"
)
func main() {
    authMiddleware, err := jwt.New(&jwt.GinJWTMiddleware{
        Realm:       "test zone",
        Key:         []byte("secret key"),
        Timeout:     time.Hour,
        MaxRefresh:  time.Hour,
        IdentityKey: "id",
        PayloadFunc: func(data interface{}) jwt.MapClaims {
            if v, ok := data.(*User); ok {
                return jwt.MapClaims{
                    "id":    v.ID,
                    "email": v.Email,
                }
            }
            return jwt.MapClaims{}
        },
        IdentityHandler: func(c *gin.Context) interface{} {
            claims := jwt.ExtractClaims(c)
            return &User{
                ID:    claims["id"].(string),
                Email: claims["email"].(string),
            }
        },
        Authenticator: func(c *gin.Context) (interface{}, error) {
            var loginData LoginData
            if err := c.ShouldBind(&loginData); err != nil {
                return "", jwt.ErrMissingLoginValues
            }
            if loginData.Email == "alice@example.com" && loginData.Password == "password" {
                return &User{
                    ID:    "123",
                    Email: "alice@example.com",
                }, nil
            }
            return nil, jwt.ErrFailedAuthentication
        },
    })
    if err != nil {
        panic(err)
    }
    r := gin.Default()
    r.POST("/login", authMiddleware.LoginHandler)
    auth := r.Group("/auth")
    auth.Use(authMiddleware.MiddlewareFunc())
    auth.GET("/", func(c *gin.Context) {
        user := jwt.ExtractClaims(c)["id"].(string)
        c.JSON(200, gin.H{
            "user": user,
        })
    })
    r.Run(":8080")
}
```
87. 使用Go语言的sqlx库进行数据库操作
```go
import (
    "github.com/jmoiron/sqlx"
    _ "github.com/mattn/go-sqlite3"
)
type User struct {
    ID    int    `db:"id"`
    Name  string `db:"name"`
    Email string `db:"email"`
}
func main() {
    db, err := sqlx.Open("sqlite3", "test.db")
    if err != nil {
        panic(err)
    }
    db.MustExec("CREATE TABLE users (id INTEGER PRIMARY KEY, name TEXT, email TEXT)")
    user := User{Name: "Alice", Email: "alice@example.com"}
    db.MustExec("INSERT INTO users (name, email) VALUES (?, ?)", user.Name, user.Email)
    var users []User
    db.Select(&users, "SELECT * FROM users")
    for _, u := range users {
        fmt.Println(u.Name, u.Email)
    }
}
```
88. 使用Go语言的govalidator库进行验证
```go
import (
    "github.com/asaskevich/govalidator"
)
type User struct {
    Name  string `valid:"required"`
    Email string `valid:"email"`
}
func main() {
    user := User{Name: "", Email: "notanemail"}
    _, err := govalidator.ValidateStruct(user)
    if err != nil {
        fmt.Println(err)
    }
}
```
89. 使用Go语言的cron库进行定时任务管理
```go
import (
    "fmt"
    "github.com/robfig/cron/v3"
)
func main() {
    c := cron.New()
    c.AddFunc("0 0 * * *", func() {
        fmt.Println("Every day at midnight")
    })
    c.Start()
    select {}
}
```
90. 使用Go语言的websocket库进行实时通信
```go
import (
    "fmt"
    "net/http"
    "github.com/gorilla/websocket"
)
var upgrader = websocket.Upgrader{
    ReadBufferSize:  1024,
    WriteBufferSize: 1024,
}
func main() {
    http.HandleFunc("/websocket", func(w http.ResponseWriter, r *http.Request) {
        conn, err := upgrader.Upgrade(w, r, nil)
        if err != nil {
            panic(err)
        }
        defer conn.Close()
        for {
            messageType, p, err := conn.ReadMessage()
            if err != nil {
                return
            }
            fmt.Println(string(p))
            err = conn.WriteMessage(messageType, p)
            if err != nil {
                return
            }
        }
    })
    http.ListenAndServe(":8080", nil)
}
```
91. 使用Go语言的go-cache库进行内存缓存
```go
import (
    "fmt"
    "github.com/patrickmn/go-cache"
    "time"
)
func main() {
    c := cache.New(5*time.Minute, 10*time.Minute)
    c.Set("key", "value", cache.DefaultExpiration)
    value, found := c.Get("key")
    if found {
        fmt.Println(value)
    }
    c.Delete("key")
}
```
92. 使用Go语言的gomail库进行邮件发送
```go
import (
    "fmt"
    "net/smtp"
    "github.com/go-gomail/gomail"
)
func main() {
    m := gomail.NewMessage()
    m.SetHeader("From", "from@example.com")
    m.SetHeader("To", "to@example.com")
    m.SetHeader("Subject", "Hello!")
    m.SetBody("text/plain", "Hello!")
    d := gomail.NewDialer("smtp.gmail.com", 587, "user", "password")
    if err := d.DialAndSend(m); err != nil {
        fmt.Println(err)
    }
}
```
93. 使用Go语言的redigo库进行Redis操作
```go
import (
    "fmt"
    "github.com/gomodule/redigo/redis"
)
func main() {
    c, err := redis.Dial("tcp", "localhost:6379")
    if err != nil {
        panic(err)
    }
    defer c.Close()
    c.Do("SET", "key", "value")
    value, err := redis.String(c.Do("GET", "key"))
    if err != nil {
        fmt.Println(err)
    }
    fmt.Println(value)
}
```
94. 使用Go语言的go-micro库进行微服务开发
```go
import (
    "fmt"
    "github.com/micro/go-micro/v2"
    "github.com/micro/go-micro/v2/client"
    "github.com/micro/go-micro/v2/server"
)
type Greeter struct{}
func (g *Greeter) Hello(ctx context.Context, req *pb.HelloRequest, rsp *pb.HelloResponse) error {
    rsp.Greeting = "Hello, " + req.Name + "!"
    return nil
}
func main() {
    service := micro.NewService(
        micro.Name("greeter"),
        micro.Version("latest"),
    )
    service.Init()
    pb.RegisterGreeterHandler(service.Server(), new(Greeter))
    if err := service.Run(); err != nil {
        fmt.Println(err)
    }
}
```
95. 使用Go语言的grpc库进行远程调用
```go
import (
    "context"
    "fmt"
    "google.golang.org/grpc"
    pb "path/to/your/proto"
)
func main() {
    conn, err := grpc.Dial("localhost:8080", grpc.WithInsecure())
    if err != nil {
        panic(err)
    }
    defer conn.Close()
    client := pb.NewGreeterClient(conn)
    rsp, err := client.Hello(context.Background(), &pb.HelloRequest{Name: "Alice"})
    if err != nil {
        fmt.Println(err)
    }
    fmt.Println(rsp.Greeting)
}
```
96. 使用Go语言的testing库进行单元测试
```go
import (
    "testing"
)
func TestAdd(t *testing.T) {
    result := Add(2, 3)
    if result != 5 {
        t.Errorf("Add(2, 3) = %!d(MISSING); want 5", result)
    }
}
```
97. 使用Go语言的benchmark库进行性能测试
```go
import (
    "testing"
)
func BenchmarkAdd(b *testing.B) {
    for i := 0; i < b.N; i++ {
        Add(2, 3)
    }
}
```
98. 使用Go语言的pprof库进行性能分析
```go
import (
    "log"
    "net/http"
    _ "net/http/pprof"
)
func main() {
    go func() {
        log.Println(http.ListenAndServe("localhost:6060", nil))
    }()
    // your code here
}
```
99. 使用Go语言的context库进行上下文管理
```go
import (
    "context"
    "net/http"
)
func handler(w http.ResponseWriter, r *http.Request) {
    ctx, cancel := context.WithTimeout(context.Background(), time.Second)
    defer cancel()
    // your code here
}
```
100. 使用Go语言的sync库进行并发编程
```go
import (
    "sync"
)
func main() {
    var wg sync.WaitGroup
    wg.Add(2)
    go func() {
        defer wg.Done()
        // goroutine 1
    }()
    go func() {
        defer wg.Done()
        // goroutine 2
    }()
    wg.Wait()
}
```
