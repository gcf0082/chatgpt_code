import java.util.concurrent.*;

public class FutureExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<String> callable = new Callable<String>() {
            public String call() throws Exception {
                // 做一些异步操作，例如访问数据库或调用远程服务
                Thread.sleep(5000);
                return "Hello, World!";
            }
        };

        Future<String> future = executor.submit(callable);

        try {
            String result = future.get(); // 阻塞等待结果
            System.out.println(result);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
