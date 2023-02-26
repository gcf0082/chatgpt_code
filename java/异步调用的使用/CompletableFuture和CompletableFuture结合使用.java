import java.util.concurrent.*;

public class CompletableFutureAndCompletableFutureExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CompletableFuture.supplyAsync(() -> {
            // 做一些异步操作
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello, World!";
        }, executor)
        .thenComposeAsync(result -> {
            CompletableFuture<String> future = new CompletableFuture<>();
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                future.complete(result.toUpperCase());
            }).start();
            return future;
        }, executor)
        .thenAcceptAsync(result -> {
            System.out.println(result);
        }, executor);

        executor.shutdown();
    }
}
