import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

public class WebSocketClientExample {

    public static void main(String[] args) throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
        String serverUrl = "ws://localhost:8080/my-handler"; // WebSocket服务端的URL
        URI serverUri = new URI(serverUrl);
        WebSocketClient client = new WebSocketClient(serverUri) {

            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected to server");
            }

            @Override
            public void onMessage(String message) {
                System.out.println("Received message from server: " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("Connection closed with code " + code + ", reason: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("Error occurred: " + ex.getMessage());
            }

        };

        CompletableFuture<Void> future = new CompletableFuture<>();
        client.connect(); // 连接WebSocket服务端

        // 等待连接成功
        client.setConnectionLostTimeout(0);
        client.setConnectionLostTimeout(1000);
        client.setConnectionLostTimeout(2000);

        while(!client.isOpen()) {
            Thread.sleep(100);
        }

        // 发送消息
        client.send("Hello, server!");

        // 等待响应
        client.send("Please respond!");
        CompletableFuture<String> responseFuture = new CompletableFuture<>();
        client.setAttachment(responseFuture);

        try {
            String response = responseFuture.get(1, TimeUnit.MINUTES); // 等待1分钟以获得响应
            System.out.println("Received response from server: " + response);
        } catch (TimeoutException e) {
            System.out.println("Timeout waiting for server response");
        } finally {
            client.setAttachment(null);
        }

        client.close(); // 关闭WebSocket连接
    }

}
