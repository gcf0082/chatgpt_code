/*
WebFlux是Spring框架的响应式编程扩展，支持HTTP、WebSocket、TCP等协议。通过Flux和Mono等Reactor的接口，可以实现异步调用。
*/
import org.springframework.web.reactive.function.client.*;
import reactor.core.publisher.*;

public class CompletableFutureAndWebFluxExample {
    public static void main(String[] args) {
        WebClient webClient = WebClient.create("http://www.google.com");

        Mono<String> mono = webClient.get().uri("/")
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(result -> System.out.println(result));

        mono.subscribe();
    }
}
