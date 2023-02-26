import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncService {
    @Async
    public void doAsyncTask() {
        // 做一些异步操作
        System.out.println("Async task executed");
    }
}

// 调用异步方法
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringAsyncExample {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        AsyncService asyncService = context.getBean(AsyncService.class);
        asyncService.doAsyncTask();
    }
}
