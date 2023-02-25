import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ElasticsearchQuery {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient();

        String startTime = "2023-02-01T00:00:00Z"; // 查询开始时间
        String endTime = "2023-02-25T23:59:59Z"; // 查询结束时间
        String query = "{\"query\": {\"range\": {\"@timestamp\": {\"gte\": \"" + startTime + "\", \"lte\": \"" + endTime + "\"}}}}"; // Elasticsearch查询语句

        Request request = new Request.Builder()
                .url("http://localhost:9200/my_index/_search")
                .post(RequestBody.create(MediaType.parse("application/json"), query))
                .build();

        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        System.out.println(responseBody);
    }
}
