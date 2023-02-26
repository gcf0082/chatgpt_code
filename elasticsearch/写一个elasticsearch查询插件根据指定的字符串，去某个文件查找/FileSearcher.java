import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileSearcher {
    
    public static String search(String filePath, String searchString) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(searchString)) {
                    sb.append(line).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
