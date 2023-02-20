package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}

@RestController
class FileController {

    @GetMapping("/get_file_tree")
    public List<FileNode> getFileTree() {
        List<FileNode> fileNodes = new ArrayList<>();
        File root = new File("/");
        FileNode rootNode = new FileNode(root.getName(), true, false, new ArrayList<>());
        addFileNodes(rootNode, root);
        fileNodes.add(rootNode);
        return fileNodes;
    }

    private void addFileNodes(FileNode parentNode, File parentFile) {
        File[] files = parentFile.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    FileNode fileNode = new FileNode(file.getName(), true, false, new ArrayList<>());
                    parentNode.getChildren().add(fileNode);
                    addFileNodes(fileNode, file);
                } else {
                    FileNode fileNode = new FileNode(file.getName(), false, true, null);
                    parentNode.getChildren().add(fileNode);
                }
            }
        }
    }
}

class FileNode {
    private String text;
    private boolean children;
    private boolean type;
    private List<FileNode> nodes;

    public FileNode(String text, boolean children, boolean type, List<FileNode> nodes) {
        this.text = text;
        this.children = children;
        this.type = type;
        this.nodes = nodes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isChildren() {
        return children;
    }

    public void setChildren(boolean children) {
        this.children = children;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public List<FileNode> getChildren() {
        return nodes;
    }

    public void setNodes(List<FileNode> nodes) {
        this.nodes = nodes;
    }
}
