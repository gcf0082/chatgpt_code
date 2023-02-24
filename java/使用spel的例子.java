import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.ArrayList;
import java.util.List;

public class SpelExample {

    public static void main(String[] args) {

        // 创建包含多个User对象的列表
        List<User> userList = new ArrayList<>();
        userList.add(new User(1, "John", "Doe"));
        userList.add(new User(2, "Jane", "Doe"));
        userList.add(new User(3, "Bob", "Smith"));

        // 创建SpEL表达式解析器
        ExpressionParser parser = new SpelExpressionParser();

        // 创建SpEL表达式
        String spelExpression = "#userList.?[id == 2]";

        // 创建SpEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        context.setVariable("userList", userList);

        // 解析SpEL表达式并获取结果
        List<User> result = parser.parseExpression(spelExpression).getValue(context, List.class);

        // 输出结果
        System.out.println(result.get(0).getFirstName() + " " + result.get(0).getLastName());
    }
}

class User {
    private int id;
    private String firstName;
    private String lastName;

    public User(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
