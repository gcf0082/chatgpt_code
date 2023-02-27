import okhttp3.*;

import java.io.IOException;

public class OkHttpUtils {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static OkHttpClient client = new OkHttpClient();

    public static String sendJsonPost(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public static String sendJsonPost(String ip, int port, String json) throws IOException {
        String url = "http://" + ip + ":" + port;
        return sendJsonPost(url, json);
    }
}

/*
String ip = "127.0.0.1";
int port = 8080;
String json = "{\"key\": \"value\"}";

String response = OkHttpUtils.sendJsonPost(ip, port, json);
System.out.println(response);

*/
