import ognl.Ognl;
import ognl.OgnlException;

import java.util.ArrayList;
import java.util.List;

public class OgnlExample {
    public static void main(String[] args) throws OgnlException {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("Alice", 18));
        persons.add(new Person("Bob", 20));
        persons.add(new Person("Charlie", 22));
        persons.add(new Person("David", 24));
        persons.add(new Person("Eve", 16));

        // 使用 OGNL 过滤 persons 中年龄大于等于 18 的人，并返回他们的 name 属性
        List<String> result = new ArrayList<>();
        for (Person person : persons) {
            if ((Integer) Ognl.getValue("age >= 18", person)) {
                result.add((String) Ognl.getValue("name", person));
            }
        }

        // 输出结果
        System.out.println(result); // [Alice, Bob, Charlie, David]
    }
}

class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }
}
