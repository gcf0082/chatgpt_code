import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ElasticsearchQuery {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Elasticsearch query DSL to retrieve the last 5 minutes and up to 1000 latest logs
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"bool\": {\n" +
                "      \"must\": [\n" +
                "        {\n" +
                "          \"range\": {\n" +
                "            \"@timestamp\": {\n" +
                "              \"gte\": \"now-5m\"\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  },\n" +
                "  \"sort\": [\n" +
                "    {\n" +
                "      \"@timestamp\": {\n" +
                "        \"order\": \"desc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"size\": 1000\n" +
                "}";

        // Elasticsearch endpoint URL and index name
        String url = "http://localhost:9200/myindex/_search";

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), query))
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println(response.body().string());
        }
    }
}
