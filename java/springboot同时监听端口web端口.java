/*
这个代码还需要自己根据不同的端口做处理
*/
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.PortInUseException;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@SpringBootApplication
public class MultiPortApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultiPortApplication.class, args);
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addAdditionalTomcatConnectors(createConnector(8081));
        factory.addAdditionalTomcatConnectors(createConnector(8082));
        return factory;
    }

    private Connector createConnector(int port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(port);
        return connector;
    }
}

@Controller
@RequestMapping("/")
class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "Welcome to the Home page!";
    }
}

@Controller
@RequestMapping("/")
class AdminController {

    @GetMapping("/admin")
    @ResponseBody
    public String admin() {
        return "Welcome to the Admin page!";
    }
}
