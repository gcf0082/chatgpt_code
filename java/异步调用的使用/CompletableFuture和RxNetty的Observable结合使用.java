import io.netty.buffer.*;
import io.reactivex.netty.protocol.http.client.*;
import rx.Observable;

public class CompletableFutureAndRxNettyExample {
    public static void main(String[] args) {
        HttpClient<ByteBuf, ByteBuf> httpClient = HttpClient.newClient("www.google.com", 80);

        Observable<String> observable = httpClient.createGet("/")
                .flatMap(response -> response.getContent().map(buf -> buf.toString(response.getCharset())))
                .doOnNext(result -> System.out.println(result));

        observable.subscribe();
    }
}
