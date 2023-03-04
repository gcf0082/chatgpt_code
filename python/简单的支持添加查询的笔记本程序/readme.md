默认数据库保存在
instance/notes.db

命令行建表：
sqlite3 notes.db

CREATE TABLE note (
    id INTEGER PRIMARY KEY,
    url VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    tag VARCHAR(50) NOT NULL
);

.quit
