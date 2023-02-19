import sys

seen = set()

for line in sys.stdin:
    line = line.strip()
    if line not in seen:
        seen.add(line)
        print(line)
