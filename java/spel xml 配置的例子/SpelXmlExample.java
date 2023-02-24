import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class SpelXmlExample {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("#input.toUpperCase()");
        String result = expression.getValue(context, "Hello, SpEL!", String.class);
        System.out.println(result); // 输出：HELLO, SPEL!
    }
}
