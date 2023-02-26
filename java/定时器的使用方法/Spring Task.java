import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class SpringTaskExample {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringTaskExample.class);
    }

    @Bean
    public Task task() {
        return new Task();
    }

    public static class Task {
        @Scheduled(fixedDelay = 5000)
        public void execute() {
            System.out.println("Task executed!");
        }
    }
}
