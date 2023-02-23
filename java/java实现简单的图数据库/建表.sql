CREATE TABLE nodes (
    id INTEGER PRIMARY KEY,
    label TEXT,
    properties TEXT
);

CREATE TABLE edges (
    id INTEGER PRIMARY KEY,
    from_node INTEGER,
    to_node INTEGER,
    label TEXT,
    properties TEXT,
    FOREIGN KEY (from_node) REFERENCES nodes(id),
    FOREIGN KEY (to_node) REFERENCES nodes(id)
);
