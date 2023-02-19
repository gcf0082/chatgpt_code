import java.lang.instrument.Instrumentation;
import java.util.concurrent.Callable;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatchers;

public class MyAgent {

    public static void premain(String args, Instrumentation instrumentation) {
        new AgentBuilder.Default()
            .type(ElementMatchers.nameStartsWith("com.example."))
            .transform((builder, typeDescription, classLoader, module) ->
                builder.visit(Advice
                    .to(MyAgent.class)
                    .on(ElementMatchers.isMethod()
                        .and(ElementMatchers.any()))
                )
            )
            .installOn(instrumentation);
    }

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin MethodDescription method, @Advice.AllArguments Object[] args) {
        System.out.println("Method " + method.getName() + " called with arguments: " + args);
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onExit(@Advice.Origin MethodDescription method, @Advice.Return Object returnValue) {
        System.out.println("Method " + method.getName() + " returned with value: " + returnValue);
    }

    public static void unhook() {
        // 可以在这里实现恢复原始类的代码
    }
}
