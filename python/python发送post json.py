import requests
import json

# 要发送的数据
data = {
    "name": "John",
    "age": 30,
    "city": "New York"
}

# 将数据转换为JSON格式
json_data = json.dumps(data)

# 设置HTTP头
headers = {'Content-type': 'application/json'}

# 发送POST请求
response = requests.post(url, data=json_data, headers=headers)

# 打印响应
print(response.text)
