import org.springframework.web.reactive.function.client.*;
import org.springframework.http.MediaType;
import reactor.core.publisher.*;

public class SpringWebFluxExample {
    public static void main(String[] args) {
        WebClient client = WebClient.create("https://jsonplaceholder.typicode.com");

        Mono<String> response = client.get()
                .uri("/posts/1")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);

        response.subscribe(result -> {
            System.out.println(result);
        });
    }
}
