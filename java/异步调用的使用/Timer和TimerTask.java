import java.util.*;

public class TimerExample {
    public static void main(String[] args) {
        TimerTask task = new TimerTask() {
            public void run() {
                // 做一些异步操作
                System.out.println("Hello, World!");
            }
        };

        Timer timer = new Timer();
        timer.schedule(task, 5000); // 5秒后执行任务
    }
}
