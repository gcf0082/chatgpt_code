import sys
import time
import paramiko

HOST = sys.argv[1]
USER = sys.argv[2]
PASSWORD = sys.argv[3]
NEW_PASSWORD = sys.argv[4]
COMMANDS = sys.argv[5]

# 建立 SSH 连接
client = paramiko.SSHClient()
client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
client.connect(HOST, username=USER, password=PASSWORD)

# 切换用户
channel = client.invoke_shell()
channel.send("su -\n")
time.sleep(1)
out = channel.recv(1024)
channel.send(NEW_PASSWORD + "\n")
time.sleep(1)
out += channel.recv(1024)

# 执行多个命令
for cmd in COMMANDS.split(";"):
    stdin, stdout, stderr = client.exec_command(cmd)
    print(f"Command output:\n{stdout.read().decode()}")

# 关闭连接
client.close()
