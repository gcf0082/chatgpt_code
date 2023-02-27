import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

import java.util.Arrays;

public class SpoonAnalysisExample {
    public static void main(String[] args) {
        // 创建Spoon Launcher
        SpoonAPI spoon = new Launcher();

        // 设置源代码路径和文件名
        spoon.getEnvironment().setSourceClasspath(Arrays.asList("src/main/java"));
        spoon.getEnvironment().setInputResource("src/main/java/com/example/Main.java");

        // 设置类路径，包含所需的jar包
        spoon.getEnvironment().setClasspath(Arrays.asList("lib/*.jar"));

        // 构建模型并获取根节点
        CtModel model = spoon.buildModel();
        CtClass<?> root = model.getRootPackage().getElements(new SpoonFilter<CtClass<?>>(CtClass.class)).get(0);

        // 输出类名和方法名
        System.out.println("Class name: " + root.getSimpleName());
        for (CtMethod<?> method : root.getMethods()) {
            System.out.println("Method name: " + method.getSimpleName());
        }
    }
}
