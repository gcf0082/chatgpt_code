import java.io.IOException;
import java.util.List;
import org.json.JSONObject;

public class Example {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        IndexSearcher searcher = new IndexSearcher("index.ser");
        searcher.loadIndex();
        String[] keywords = {"apple", "banana"};
        String regex = ".*pie.*";
        List<JSONObject> results = searcher.search(keywords, regex);
        for (JSONObject result : results) {
            System.out.println(result.toString());
        }
    }
}
