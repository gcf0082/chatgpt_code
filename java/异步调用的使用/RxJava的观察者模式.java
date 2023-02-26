import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class RxJavaObservableExample {
    public static void main(String[] args) {
        Observable<String> observable = Observable.create(emitter -> {
            // 做一些异步操作
            new Thread(() -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                emitter.onNext("Hello, World!");
                emitter.onComplete();
            }).start();
        });

        Observer<String> observer = new Observer<String>() {
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
        };

        observable.subscribe(observer);
    }
}
