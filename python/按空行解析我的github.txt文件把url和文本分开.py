import re
import json

# 读取txt文件
with open('github.txt', 'r', encoding='utf-8') as f:
    # 以空行作为分隔符来提取记录
    records = f.read().split('\n\n')

# 构造JSON数据
data = []
for record in records:
    # 构造记录对象
    record_obj = {'urls': [], 'txt': []}

    # 将记录拆分成行，并遍历每行
    for line in record.split('\n'):
        # 如果该行以'http'开头，则将其添加到记录对象的'urls'字段中
        if line.startswith('http'):
            record_obj['urls'].append(line.strip())
        # 否则，将其添加到记录对象的'txt'字段中
        else:
            record_obj['txt'].append(line.strip())

    # 将记录对象添加到数据中
    data.append(record_obj)

# 将JSON数据写入文件
with open('output.json', 'w', encoding='utf-8') as f:
    json.dump(data, f, ensure_ascii=False)
