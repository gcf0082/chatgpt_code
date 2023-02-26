import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.*;

@SpringBootApplication
@RestController
@EnableWebSocket
public class WebSocketServerApplication implements WebSocketConfigurer {

    private static final Object lock = new Object(); // 定义一个锁对象

    @GetMapping("/send-message")
    public String sendMessage() throws IOException, ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture < String > future = new CompletableFuture < > (); // 创建一个CompletableFuture对象

        // 获取所有连接的WebSocket会话
        for (WebSocketSession session: sessions) {
            // 向客户端发送消息
            session.sendMessage(new TextMessage("Hello, client!"));

            // 注册消息处理器，以便在收到客户端响应时解析响应并完成CompletableFuture
            session.setTextMessageSizeLimit(1024 * 1024);
            session.setHandler(new TextWebSocketHandler() {
                @Override
                public void handleTextMessage(WebSocketSession session, TextMessage message) {
                    String response = message.getPayload();
                    future.complete(response); // 将响应设置到CompletableFuture中
                }
            });

            // 等待客户端响应，设置超时时间为1分钟
            try {
                String response = future.get(1, TimeUnit.MINUTES); // 等待CompletableFuture完成并获取响应，设置超时时间为1分钟
                System.out.println("Response from client: " + response);
            } catch (TimeoutException e) {
                future.completeExceptionally(e); // CompletableFuture在超时时抛出异常
                System.out.println("Timeout waiting for client response");
            } finally {
                session.setHandler(null); // 解除消息处理器的注册
            }
        }
        return "Message sent to all clients";
    }

    public static void main(String[] args) {
        SpringApplication.run(WebSocketServerApplication.class, args);
    }

    private static final ConcurrentLinkedQueue < WebSocketSession > sessions = new ConcurrentLinkedQueue < > (); // 定义WebSocket会话队列

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(myHandler(), "/my-handler").setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler myHandler() {
        return new TextWebSocketHandler() {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                sessions.add(session); // 将新的WebSocket会话添加到队列中
            }

            @Override
            public void handleMessage(WebSocketSession session, WebSocketMessage << ? > message) throws Exception {
                // 处理收到的WebSocket消息
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
                sessions.remove(session); // 将WebSocket会话从队列中移除
            }
        };
    }
}
