import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.unixdomainsockets.UnixDomainSocketFactory;

import java.io.IOException;

public class UDSClient {
    public static void main(String[] args) throws IOException {
        String socketFile = "/tmp/myapp.sock";

        // 创建OkHttpClient，设置Unix Domain Socket连接工厂
        OkHttpClient client = new OkHttpClient.Builder()
                .socketFactory(new UnixDomainSocketFactory(socketFile))
                .build();

        // 创建HTTP请求
        Request request = new Request.Builder()
                .url("http://localhost/")
                .build();

        // 发送HTTP请求
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        System.out.println(responseBody);
    }
}
