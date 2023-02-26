import java.util.concurrent.*;

public class CompletableFutureExample {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            // 做一些异步操作，例如访问数据库或调用远程服务
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello, World!";
        }).thenAccept(result -> {
            System.out.println(result);
        });

        try {
            Thread.sleep(6000); // 等待异步操作完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
