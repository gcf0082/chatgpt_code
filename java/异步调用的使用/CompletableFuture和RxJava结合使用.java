import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import java.util.concurrent.*;

public class CompletableFutureAndRxJavaExample {
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
        .thenApplyAsync(result -> {
            return Observable.just(result);
        }, executor)
        .thenAcceptAsync(obs -> {
            obs.subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(String result) {
                    System.out.println(result);
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {
                }
            });
        }, executor);

        executor.shutdown();
    }
}
