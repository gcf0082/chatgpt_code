import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import org.json.*;

public class IndexSearcher {
    private Map<String, Map<String, List<Integer>>> index;
    private String indexPath;
    
    public IndexSearcher(String indexPath) {
        this.index = new HashMap<>();
        this.indexPath = indexPath;
    }
    
    public void buildIndex(String directory) throws IOException {
        // 读取目录中的所有文件
        Path dirPath = Paths.get(directory);
        List<Path> files = new ArrayList<>();
        Files.walk(dirPath).forEach(path -> {
            if (Files.isRegularFile(path)) {
                files.add(path);
            }
        });
        
        // 对每个文件进行分词并建立索引
        for (Path file : files) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file.toString()), StandardCharsets.UTF_8));
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\W+");
                for (String word : words) {
                    word = word.toLowerCase();
                    if (!index.containsKey(word)) {
                        index.put(word, new HashMap<>());
                    }
                    if (!index.get(word).containsKey(file.toString())) {
                        index.get(word).put(file.toString(), new ArrayList<>());
                    }
                    index.get(word).get(file.toString()).add(lineNumber);
                }
                lineNumber++;
            }
            reader.close();
        }
    }
    
    public void saveIndex() throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(indexPath));
        out.writeObject(index);
        out.close();
    }
    
    @SuppressWarnings("unchecked")
    public void loadIndex() throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(indexPath));
        index = (Map<String, Map<String, List<Integer>>>) in.readObject();
        in.close();
    }
    
    public List<JSONObject> search(String[] keywords, String regex) {
        List<JSONObject> results = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        for (String keyword : keywords) {
            if (index.containsKey(keyword)) {
                Map<String, List<Integer>> fileMap = index.get(keyword);
                for (String fileName : fileMap.keySet()) {
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8));
                        String line;
                        int lineNumber = 0;
                        while ((line = reader.readLine()) != null) {
                            Matcher matcher = pattern.matcher(line);
                            if (matcher.find()) {
                                JSONObject result = new JSONObject();
                                result.put("regex", regex);
                                result.put("match_text", matcher.group());
                                result.put("line", line);
                                result.put("line_number", lineNumber);
                                result.put("file_name", fileName);
                                results.add(result);
                            }
                            lineNumber++;
                        }
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return results;
    }
}
