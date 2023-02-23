import java.io.IOException;
import java.util.List;
import org.json.JSONObject;

public class Example {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        IndexSearcher searcher = new IndexSearcher("index.ser");
        searcher.buildIndex("src/main/java/com/mycompany/app");
        searcher.saveIndex();
        searcher.loadIndex();
        String[] keywords = {"main", "banana"};
        String regex = args[0];
        List<JSONObject> results = searcher.search(keywords, regex);
        for (JSONObject result : results) {
            System.out.println(result.toString());
        }
    }
}
