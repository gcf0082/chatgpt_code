import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class RxJavaExample {
    public static void main(String[] args) {
        Observable.fromCallable(() -> {
            // 做一些异步操作
            Thread.sleep(5000);
            return "Hello, World!";
        })
        .subscribeOn(Schedulers.io())
        .subscribe(result -> {
            System.out.println(result);
        });

        try {
            Thread.sleep(6000); // 等待异步操作完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
