package main

import (
    "database/sql"
    "fmt"
    "io/fs"
    "os"
    "path/filepath"

    _ "github.com/mattn/go-sqlite3"
)

type File struct {
    Path  string
    IsDir bool
}

func main() {
    db, err := sql.Open("sqlite3", "./file_index.db")
    if err != nil {
        fmt.Println(err)
        return
    }
    defer db.Close()

    if err = createTable(db); err != nil {
        fmt.Println(err)
        return
    }

    if len(os.Args) != 2 {
        fmt.Println("Usage: go run main.go path")
        return
    }
    path := os.Args[1]

    files := make([]File, 0)
    err = filepath.WalkDir(path, func(path string, d fs.DirEntry, err error) error {
        if err != nil {
            fmt.Println(err)
            return nil
        }
        files = append(files, File{Path: path, IsDir: d.IsDir()})
        return nil
    })
    if err != nil {
        fmt.Println(err)
        return
    }

    if err = insertFiles(db, files); err != nil {
        fmt.Println(err)
        return
    }
    fmt.Println("Index created successfully!")
}

func createTable(db *sql.DB) error {
    _, err := db.Exec(`
        CREATE TABLE IF NOT EXISTS files (
            id INTEGER PRIMARY KEY,
            path TEXT,
            is_dir INTEGER
        );
    `)
    if err != nil {
        return err
    }
    return nil
}

func insertFiles(db *sql.DB, files []File) error {
    tx, err := db.Begin()
    if err != nil {
        return err
    }
    stmt, err := tx.Prepare("INSERT INTO files(path, is_dir) VALUES(?, ?)")
    if err != nil {
        return err
    }
    defer stmt.Close()

    for _, f := range files {
        _, err := stmt.Exec(f.Path, f.IsDir)
        if err != nil {
            tx.Rollback()
            return err
        }
    }
    return tx.Commit()
}
