import java.io.*;
import java.util.*;
import java.util.regex.*;

public class IndexSearcher {
    private Map<String, List<Integer>> index; // 索引
    private String indexFile; // 索引文件路径

    public IndexSearcher(String indexFile) {
        index = new HashMap<String, List<Integer>>();
        this.indexFile = indexFile;
    }

    // 构建索引
    public void buildIndex(String directory) {
        File dir = new File(directory);
        if (!dir.isDirectory()) {
            System.err.println("Error: " + directory + " is not a directory.");
            return;
        }

        int fileCount = 0;
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;
                    int lineNumber = 0;
                    while ((line = reader.readLine()) != null) {
                        lineNumber++;
                        String[] words = line.split("\\s+");
                        for (String word : words) {
                            word = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                            if (!word.isEmpty()) {
                                if (!index.containsKey(word)) {
                                    index.put(word, new ArrayList<Integer>());
                                }
                                index.get(word).add(fileCount * 1000 + lineNumber);
                            }
                        }
                    }
                    reader.close();
                    fileCount++;
                } catch (IOException e) {
                    System.err.println("Error reading " + file.getAbsolutePath() + ": " + e.getMessage());
                }
            }
        }

        System.out.println("Built index for " + fileCount + " files.");
    }

    // 保存索引到文件
    public void saveIndex() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(indexFile));
            oos.writeObject(index);
            oos.close();
            System.out.println("Index saved to " + indexFile);
        } catch (IOException e) {
            System.err.println("Error saving index to " + indexFile + ": " + e.getMessage());
        }
    }

    // 加载索引文件
    public void loadIndex() {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(indexFile));
            index = (Map<String, List<Integer>>) ois.readObject();
            ois.close();
            System.out.println("Index loaded from " + indexFile);
        } catch (IOException e) {
            System.err.println("Error loading index from " + indexFile + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading index from " + indexFile + ": " + e.getMessage());
        }
    }

    // 搜索正则表达式
    public void search(String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        for (String word : index.keySet()) {
            matcher = pattern.matcher(word);
            if (matcher.find()) {
                System.out.println(word + ": " + index.get(word));
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: java IndexSearcher <command> <directory or index file> [regex]");
            System.err.println("  Commands:");
            System.err.println("    build <directory>");
            System.err.println("    save");
            System.err.println("    load");
            System.err.println("    search <regex>");
            return;
        }

       
    String command = args[0];
    String arg = args[1];

    IndexSearcher searcher = new IndexSearcher("index.dat");
    if (command.equals("build")) {
        searcher.buildIndex(arg);
        searcher.saveIndex();
    } else if (command.equals("save")) {
        searcher.saveIndex();
    } else if (command.equals("load")) {
        searcher.loadIndex();
    } else if (command.equals("search")) {
        if (args.length < 3) {
            System.err.println("Error: missing regex.");
            return;
        }
        String regex = args[2];
        searcher.loadIndex();
        searcher.search(regex);
    } else {
        System.err.println("Error: invalid command.");
    }
}
}
