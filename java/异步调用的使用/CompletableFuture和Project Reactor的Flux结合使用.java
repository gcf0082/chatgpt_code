import reactor.core.publisher.*;
import java.util.concurrent.*;

public class CompletableFutureAndProjectReactorExample {
    public static void main(String[] args) {
        Mono<String> mono = Mono.fromCallable(() -> {
            // 做一些异步操作
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "Hello, World!";
        });

        mono.subscribeOn(Schedulers.elastic())
                .map(result -> result.toUpperCase())
                .subscribe(result -> {
                    System.out.println(result);
                });
    }
}
