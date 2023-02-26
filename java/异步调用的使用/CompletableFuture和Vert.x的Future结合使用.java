import io.vertx.core.*;
import io.vertx.core.Future;
import java.util.concurrent.*;

public class CompletableFutureAndVertxExample {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Context context = vertx.getOrCreateContext();

        CompletableFuture.supplyAsync(() -> {
            Future<String> future = Future.future();
            context.runOnContext(event -> {
                // 做一些异步操作
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                future.complete("Hello, World!");
            });
            return future;
        })
        .thenComposeAsync(result -> {
            CompletableFuture<String> future = new CompletableFuture<>();
            result.setHandler(asyncResult -> {
                if (asyncResult.succeeded()) {
                    future.complete(asyncResult.result());
                } else {
                    future.completeExceptionally(asyncResult.cause());
                }
            });
            return future;
        })
        .thenAcceptAsync(result -> {
            System.out.println(result);
        });

        vertx.close();
    }
}
