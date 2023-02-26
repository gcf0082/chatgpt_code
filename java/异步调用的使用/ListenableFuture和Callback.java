import java.util.concurrent.*;
import org.springframework.util.concurrent.*;

public class ListenableFutureCallbackExample {
    public static void main(String[] args) {
        ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));

        ListenableFuture<String> future = executor.submit(() -> {
            // 做一些异步操作
            Thread.sleep(5000);
            return "Hello, World!";
        });

        Futures.addCallback(future, new FutureCallback<String>() {
            public void onSuccess(String result) {
                System.out.println(result);
            }

            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }
        }, executor);

        executor.shutdown();
    }
}
