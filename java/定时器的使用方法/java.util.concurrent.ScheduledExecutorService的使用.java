import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ScheduledExecutorServiceExample {
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println("Task executed!");
            }
        };
        executor.schedule(task, 5, TimeUnit.SECONDS); // 5秒后执行任务
        executor.shutdown();
    }
}
