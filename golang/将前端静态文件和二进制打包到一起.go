package main

import (
	"embed"
	"io/fs"
	"log"
	"net/http"
)

//go:embed index.html js/* css/*
var content embed.FS

func main() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		file, err := content.Open("index.html")
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		defer file.Close()

		stat, err := file.Stat()
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		if !stat.IsDir() {
			http.ServeContent(w, r, stat.Name(), stat.ModTime(), file.(fs.File))
		} else {
			http.Error(w, "Invalid file", http.StatusInternalServerError)
			return
		}
	})

	http.HandleFunc("/js/", func(w http.ResponseWriter, r *http.Request) {
		name := r.URL.Path[len("/js/"):]
		file, err := content.Open("js/" + name)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		defer file.Close()

		stat, err := file.Stat()
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		if !stat.IsDir() {
			http.ServeContent(w, r, stat.Name(), stat.ModTime(), file.(fs.File))
		} else {
			http.Error(w, "Invalid file", http.StatusInternalServerError)
			return
		}
	})

	http.HandleFunc("/css/", func(w http.ResponseWriter, r *http.Request) {
		name := r.URL.Path[len("/css/"):]
		file, err := content.Open("css/" + name)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}
		defer file.Close()

		stat, err := file.Stat()
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
			return
		}

		if !stat.IsDir() {
			http.ServeContent(w, r, stat.Name(), stat.ModTime(), file.(fs.File))
		} else {
			http.Error(w, "Invalid file", http.StatusInternalServerError)
			return
		}
	})

	log.Fatal(http.ListenAndServe(":8080", nil))
}
