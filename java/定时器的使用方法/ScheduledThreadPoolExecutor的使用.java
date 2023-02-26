/*
ScheduledThreadPoolExecutor 是一个定时器线程池，可以按照一定的时间间隔执行任务。
*/
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolExecutorExample {
    public static void main(String[] args) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
        executor.scheduleAtFixedRate(() -> {
            System.out.println("Task executed!");
        }, 0, 5, TimeUnit.SECONDS);
    }
}
