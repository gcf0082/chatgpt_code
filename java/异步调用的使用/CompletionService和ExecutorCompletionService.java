import java.util.concurrent.*;

public class CompletionServiceExample {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        Callable<String> task1 = () -> {
            // 做一些异步操作
            Thread.sleep(5000);
            return "Task 1 executed";
        };

        Callable<String> task2 = () -> {
            // 做一些异步操作
            Thread.sleep(3000);
            return "Task 2 executed";
        };

        completionService.submit(task1);
        completionService.submit(task2);

        for (int i = 0; i < 2; i++) {
            Future<String> future = completionService.take(); // 取出已完成的任务
            String result = future.get();
            System.out.println(result);
        }

        executor.shutdown();
    }
}
