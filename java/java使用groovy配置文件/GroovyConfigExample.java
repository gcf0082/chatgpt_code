import groovy.util.ConfigSlurper;
import java.util.Map;

public class GroovyConfigExample {
    public static void main(String[] args) {
        ConfigSlurper configSlurper = new ConfigSlurper();
        Map config = configSlurper.parse("config.groovy");

        String driver = (String) config.get("database.driver");
        String url = (String) config.get("database.url");
        String username = (String) config.get("database.username");
        String password = (String) config.get("database.password");

        int port = (Integer) config.get("server.port");
        String host = (String) config.get("server.host");

        System.out.println("driver: " + driver);
        System.out.println("url: " + url);
        System.out.println("username: " + username);
        System.out.println("password: " + password);
        System.out.println("port: " + port);
        System.out.println("host: " + host);
    }
}
