import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

@Component
public class SpelAnnotationExample {

    @Value("#{ T(java.lang.Math).random() * 100.0 }")
    private double randomDouble;

    public double getRandomDouble() {
        return randomDouble;
    }

    public static void main(String[] args) {
        SpelAnnotationExample example = new SpelAnnotationExample();
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("#root.randomDouble");
        double result = expression.getValue(example, Double.class);
        System.out.println(result);
    }
}
