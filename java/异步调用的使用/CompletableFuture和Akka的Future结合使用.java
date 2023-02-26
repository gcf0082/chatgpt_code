import akka.actor.*;
import akka.dispatch.*;
import akka.pattern.*;
import scala.concurrent.*;
import scala.concurrent.duration.*;

public class CompletableFutureAndAkkaExample {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("MyActorSystem");
        ExecutionContext ec = system.dispatcher();

        Future<Object> future = Patterns.ask(system.actorOf(Props.create(MyActor.class)), "Hello, World!", 5000);
        CompletableFuture<Object> completableFuture = FutureConverters.toJava(future);

        completableFuture.thenAccept(result -> {
            System.out.println(result);
            system.terminate();
        });

        system.awaitTermination(Duration.Inf());
    }

    static class MyActor extends UntypedAbstractActor {
        @Override
        public void onReceive(Object message) throws Throwable {
            // 做一些异步操作
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            getSender().tell(message.toString().toUpperCase(), getSelf());
        }
    }
}
