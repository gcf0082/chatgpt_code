<?php
class FileNode {
    public $text;
    public $children;
    public $type;
    public $nodes;
    
    public function __construct($text, $children, $type, $nodes) {
        $this->text = $text;
        $this->children = $children;
        $this->type = $type;
        $this->nodes = $nodes;
    }
}

class FileController {
    public function getFileTree() {
        $fileNodes = array();
        $root = new File("/", true);
        $rootNode = new FileNode($root->getName(), true, false, array());
        $this->addFileNodes($rootNode, $root);
        $fileNodes[] = $rootNode;
        return json_encode($fileNodes);
    }
    
    private function addFileNodes($parentNode, $parentFile) {
        $files = scandir($parentFile->getPath());
        foreach ($files as $file) {
            if ($file == "." || $file == "..") {
                continue;
            }
            $filePath = $parentFile->getPath() . "/" . $file;
            if (is_dir($filePath)) {
                $fileNode = new FileNode($file, true, false, array());
                $parentNode->nodes[] = $fileNode;
                $this->addFileNodes($fileNode, new File($filePath, true));
            } else {
                $fileNode = new FileNode($file, false, true, null);
                $parentNode->nodes[] = $fileNode;
            }
        }
    }
}

class File {
    public $path;
    public $isDirectory;
    public $name;
    
    public function __construct($path, $isDirectory) {
        $this->path = $path;
        $this->isDirectory = $isDirectory;
        $this->name = basename($path);
    }
    
    public function getPath() {
        return $this->path;
    }
    
    public function getName() {
        return $this->name;
    }
    
    public function isDirectory() {
        return $this->isDirectory;
    }
}

$controller = new FileController();
echo $controller->getFileTree();
