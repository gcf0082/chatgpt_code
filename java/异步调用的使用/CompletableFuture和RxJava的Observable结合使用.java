import io.reactivex.*;
import io.reactivex.schedulers.*;
import java.util.concurrent.*;

public class CompletableFutureAndRxJavaExample {
    public static void main(String[] args) {
        Observable<String> observable = Observable.create(emitter -> {
            // 做一些异步操作
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            emitter.onNext("Hello, World!");
            emitter.onComplete();
        });

        observable.subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe(result -> {
                    System.out.println(result);
                });
    }
}
