import java.util.concurrent.*;

public class ExecutorExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Runnable task1 = () -> {
            // 做一些异步操作
            System.out.println("Task 1 executed");
        };

        Runnable task2 = () -> {
            // 做一些异步操作
            System.out.println("Task 2 executed");
        };

        executor.submit(task1);
        executor.submit(task2);

        executor.shutdown();
    }
}
