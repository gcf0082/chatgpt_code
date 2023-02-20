package main

import (
	"encoding/json"
	"io/ioutil"
	"log"
	"net/http"
	"os"
	"path/filepath"
)

type FileNode struct {
	Text     string      `json:"text"`
	Children bool        `json:"children"`
	Type     string      `json:"type"`
	Nodes    []*FileNode `json:"nodes"`
}

func NewFileNode(text string, children bool, fileType string, nodes []*FileNode) *FileNode {
	return &FileNode{
		Text:     text,
		Children: children,
		Type:     fileType,
		Nodes:    nodes,
	}
}

type FileController struct{}

func (fc *FileController) GetFileTree(w http.ResponseWriter, r *http.Request) {
	var rootNodes []*FileNode
	rootNode := NewFileNode("/", true, "", []*FileNode{})
	fc.addFileNodes(rootNode, "/", true)
	rootNodes = append(rootNodes, rootNode)
	json.NewEncoder(w).Encode(rootNodes)
}

func (fc *FileController) addFileNodes(parentNode *FileNode, parentPath string, isDir bool) {
	files, err := ioutil.ReadDir(parentPath)
	if err != nil {
		log.Println(err)
		return
	}
	for _, file := range files {
		filePath := filepath.Join(parentPath, file.Name())
		if file.IsDir() {
			fileNode := NewFileNode(file.Name(), true, "", []*FileNode{})
			parentNode.Nodes = append(parentNode.Nodes, fileNode)
			fc.addFileNodes(fileNode, filePath, true)
		} else {
			fileNode := NewFileNode(file.Name(), false, filepath.Ext(file.Name()), nil)
			parentNode.Nodes = append(parentNode.Nodes, fileNode)
		}
	}
}

func main() {
	fileController := &FileController{}
	http.HandleFunc("/api/files/tree", fileController.GetFileTree)
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatal(err)
	}
}
