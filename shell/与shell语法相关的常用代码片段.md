121. 定义变量：
```
variable_name=value
```
122. 使用变量：
```
echo $variable_name
```
123. 读取用户输入到变量中：
```
read variable_name
```
124. 检查变量是否为空：
```
if [ -z $variable_name ]; then
    echo "Variable is empty"
fi
```
125. 检查文件是否存在：
```
if [ -f /path/to/file ]; then
    echo "File exists"
fi
```
126. 检查目录是否存在：
```
if [ -d /path/to/directory ]; then
    echo "Directory exists"
fi
```
127. 检查文件是否可读：
```
if [ -r /path/to/file ]; then
    echo "File is readable"
fi
```
128. 检查文件是否可写：
```
if [ -w /path/to/file ]; then
    echo "File is writable"
fi
```
129. 检查文件是否可执行：
```
if [ -x /path/to/file ]; then
    echo "File is executable"
fi
```
130. 使用for循环：
```
for variable_name in item1 item2 item3; do
    command1
    command2
    command3
done
```
131. 使用while循环读取文件中的每一行：
```
while read line; do
    echo $line
done < /path/to/file
```
132. 使用if语句判断条件：
```
if [ condition ]; then
    command1
    command2
else
    command3
    command4
fi
```
133. 使用case语句判断多个条件：
```
case variable_name in
    pattern1) command1;;
    pattern2) command2;;
    pattern3) command3;;
    *) default_command;;
esac
```
134. 使用函数封装代码：
```
function_name() {
    command1
    command2
    command3
}
# 调用函数
function_name
```
135. 使用命令行参数传递参数：
```
#!/bin/bash
echo "Script name: $0"
echo "First argument: $1"
echo "Second argument: $2"
```
136. 使用getopts命令解析命令行选项：
```
while getopts ":a:b:" opt; do
    case $opt in
        a) arg1="$OPTARG";;
        b) arg2="$OPTARG";;
        \?) echo "Invalid option: -$OPTARG";;
    esac
done
echo "Argument 1: $arg1"
echo "Argument 2: $arg2"
```
137. 使用数组存储多个值：
```
my_array=(value1 value2 value3)
echo ${my_array[0]} # 输出value1
echo ${my_array[@]} # 输出所有值
```
138. 使用for循环遍历数组：
```
my_array=(value1 value2 value3)
for item in ${my_array[@]}; do
    echo $item
done
```
139. 使用while循环读取数组中的值：
```
my_array=(value1 value2 value3)
i=0
while [ $i -lt ${#my_array[@]} ]; do
    echo ${my_array[$i]}
    i=$(($i+1))
done
```
140. 使用shift命令移动命令行参数：
```
while [ $# -gt 0 ]; do
    case $1 in
        -a) arg1="$2"; shift;;
        -b) arg2="$2"; shift;;
        *) echo "Invalid option: $1";;
    esac
    shift
done
echo "Argument 1: $arg1"
echo "Argument 2: $arg2"
```
141. 使用declare命令声明变量的属性：
```
declare -a my_array # 声明my_array为数组
declare -i my_integer # 声明my_integer为整数
declare -r my_constant="value" # 声明my_constant为只读变量
```
142. 使用trap命令处理信号：
```
trap "echo 'Interrupt signal received'; exit" SIGINT
```
