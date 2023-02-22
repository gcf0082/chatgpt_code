import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@RestController
public class FileBrowserApplication {

    @GetMapping("/files")
    public List<FileItem> getFiles(@RequestParam(required = false) String path) throws IOException {
        Path directory = Paths.get(path == null ? "." : path);
        return Files.list(directory)
                .map(FileItem::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/file/{path}")
    public String getFileContent(@PathVariable String path) throws IOException {
        return Files.readString(Paths.get(path));
    }

    public static void main(String[] args) {
        SpringApplication.run(FileBrowserApplication.class, args);
    }
}

class FileItem {
    private final String name;
    private final String path;
    private final String type;

    public FileItem(Path file) {
        this.name = file.getFileName().toString();
        this.path = file.toAbsolutePath().toString();
        this.type = Files.isDirectory(file) ? "directory" : "file";
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return type;
    }
}
