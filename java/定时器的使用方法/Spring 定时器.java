import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
public class SpringSchedulerExample {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(SpringSchedulerExample.class);
        context.refresh();
    }

    @Scheduled(fixedDelay = 5000) // 任务每隔 5 秒执行一次
    public void executeTask() {
        System.out.println("Task executed!");
    }
}
