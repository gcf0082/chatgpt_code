import time

for i in range(1, 11):
    # 将光标移动到行首，并清除该行
    print("\r\033[K", end="")

    # 显示更新的内容
    print(f"Countdown: {10-i}", end="")

    # 等待一段时间
    time.sleep(1)

# 输出一个空行
print()
