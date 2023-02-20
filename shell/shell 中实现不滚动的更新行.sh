#!/bin/bash

for i in {1..10}; do
    # 将光标移动到行首，并清除该行
    printf "\r\033[K"

    # 显示更新的内容
    printf "Countdown: %d" $((10-i))

    # 等待一段时间
    sleep 1
done

# 输出一个空行
echo
