import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.visitor.CtScanner;

import java.util.*;

public class DataFlowAnalysisExample {

    public static void main(String[] args) {
        // 解析源代码，生成抽象语法树（AST）
        Launcher launcher = new Launcher();
        launcher.addInputResource("path/to/your/java/file");
        CtModel model = launcher.buildModel();

        // 遍历AST，找到所有函数调用节点并进行分析
        model.processWith(new CtScanner() {
            @Override
            public void visitCtInvocation(CtInvocation<?> invocation) {
                CtExecutableReference<?> executableRef = invocation.getExecutable();

                // 找到目标函数 "add"
                if (executableRef.getSimpleName().equals("add") &&
                        executableRef.getDeclaringType().getQualifiedName().equals("DataFlowAnalysisExample")) {
                    CtMethod<?> method = (CtMethod<?>) executableRef.getExecutableDeclaration();

                    // 分析函数内部的数据流
                    DataFlowAnalyzer analyzer = new DataFlowAnalyzer();
                    analyzer.analyze(method);
                }
            }
        });
    }

    private static class DataFlowAnalyzer {
        private Map<CtElement, Set<CtElement>> variableDefs = new HashMap<>();
        private Map<CtElement, Set<CtElement>> variableUses = new HashMap<>();
        private Set<CtElement> paramDefs = new HashSet<>();
        private Set<CtElement> paramUses = new HashSet<>();
        private Set<CtElement> returnDefs = new HashSet<>();
        private Set<CtElement> returnUses = new HashSet<>();

        public void analyze(CtMethod<?> method) {
            // 找到所有局部变量定义和参数定义节点
            method.getBody().getStatements().stream()
                    .filter(stmt -> stmt instanceof CtLocalVariable || stmt instanceof CtAssignment)
                    .forEach(stmt -> {
                        if (stmt instanceof CtLocalVariable) {
                            CtLocalVariable<?> varDecl = (CtLocalVariable<?>) stmt;
                            variableDefs.put(varDecl, new HashSet<>());
                        } else if (stmt instanceof CtAssignment) {
                            CtAssignment<?, ?> assign = (CtAssignment<?, ?>) stmt;
                            CtExpression<?> lhs = assign.getAssigned();
                            if (lhs instanceof CtVariableRead) {
                                CtVariableRead<?> varRead = (CtVariableRead<?>) lhs;
                                CtElement varDecl = varRead.getVariable().getDeclaration();
                                if (varDecl instanceof CtLocalVariable) {
                                    variableDefs.putIfAbsent(varDecl, new HashSet<>());
                                                                  variableUses.putIfAbsent(varDecl, new HashSet<>());
                                variableUses.get(varDecl).add(assign.getAssigned());
                            }
                        }
                    }
                });

        // 找到所有参数节点
        method.getParameters().forEach(param -> {
            paramDefs.add(param);
            paramUses.add(param);
        });

        // 找到所有 return 语句节点
        method.getBody().getStatements().stream()
                .filter(stmt -> stmt instanceof CtReturn)
                .forEach(stmt -> {
                    CtReturn<?> ret = (CtReturn<?>) stmt;
                    if (ret.getReturnedExpression() != null) {
                        returnUses.add(ret.getReturnedExpression());
                    }
                    returnDefs.add(ret);
                });

        // 通过递归分析变量使用情况
        method.getBody().getStatements().forEach(stmt -> analyzeStatement(stmt));

        // 输出分析结果
        System.out.println("Variable definitions: " + variableDefs);
        System.out.println("Variable uses: " + variableUses);
        System.out.println("Parameter definitions: " + paramDefs);
        System.out.println("Parameter uses: " + paramUses);
        System.out.println("Return definitions: " + returnDefs);
        System.out.println("Return uses: " + returnUses);
    }

    private void analyzeStatement(CtStatement stmt) {
        // 处理二元操作符语句
        if (stmt instanceof CtBinaryOperator) {
            CtBinaryOperator<?, ?> binOp = (CtBinaryOperator<?, ?>) stmt;
            analyzeExpression(binOp.getLeftHandOperand());
            analyzeExpression(binOp.getRightHandOperand());
        }
        // 处理赋值语句
        else if (stmt instanceof CtAssignment) {
            CtAssignment<?, ?> assign = (CtAssignment<?, ?>) stmt;
            analyzeExpression(assign.getAssigned());
            analyzeExpression(assign.getAssignment());
        }
        // 处理函数调用语句
        else if (stmt instanceof CtInvocation) {
            CtInvocation<?> invocation = (CtInvocation<?>) stmt;
            invocation.getArguments().forEach(this::analyzeExpression);
            CtExecutableReference<?> executableRef = invocation.getExecutable();
            if (executableRef.getDeclaration() instanceof CtMethod) {
                CtMethod<?> method = (CtMethod<?>) executableRef.getDeclaration();
                analyzeMethodCall(method, invocation);
            }
        }
        // 处理 return 语句
        else if (stmt instanceof CtReturn) {
            CtReturn<?> ret = (CtReturn<?>) stmt;
            if (ret.getReturnedExpression() != null) {
                analyzeExpression(ret.getReturnedExpression());
            }
        }
    }

    private void analyzeMethodCall(CtMethod<?> method, CtInvocation<?> invocation) {
        // 找到函数参数的定义和使用
        List<CtExpression<?>> args = invocation.getArguments();
        List<CtElement> paramDefs = method.getParameters();
        for (int i = 0; i < args.size(); i++) {
            CtExpression<?> arg = args.get(i);
            if (arg instanceof CtVariableRead) {
                CtVariableRead<?> varRead = (CtVariableRead<?>) arg;
                CtElement varDecl = varRead.getVariable().getDeclaration();
                if (varDecl instanceof CtLocalVariable) {
                    if (paramDefs.contains(varDecl)) {
                        this.paramUses.add(varRead);
                    }
                }
            }
        }
        // 递归分析函数内部的数据流
        method.getBody().getStatements().forEach(this::analyzeStatement);
        // 找到函数返回值的定义和使用
        if (method.getType() != null) {
                      CtTypeReference<?> returnType = method.getType();
            CtElement retVar = method.getElements(new TypeFilter<>(CtReturn.class)).stream()
                    .filter(ret -> ((CtReturn<?>) ret).getReturnedExpression() != null)
                    .map(ret -> ((CtReturn<?>) ret).getReturnedExpression())
                    .findFirst().orElse(null);
            if (retVar != null && retVar.getType() != null && retVar.getType().equals(returnType)) {
                this.returnDefs.add(retVar);
            }
            method.getBody().getElements(new TypeFilter<>(CtReturn.class)).stream()
                    .filter(ret -> ((CtReturn<?>) ret).getReturnedExpression() != null)
                    .forEach(ret -> {
                        CtExpression<?> retExp = ((CtReturn<?>) ret).getReturnedExpression();
                        if (retExp.getType() != null && retExp.getType().equals(returnType)) {
                            this.returnUses.add(retExp);
                        }
                    });
        }
    }

    private void analyzeExpression(CtExpression<?> expr) {
        if (expr instanceof CtVariableRead) {
            CtVariableRead<?> varRead = (CtVariableRead<?>) expr;
            CtElement varDecl = varRead.getVariable().getDeclaration();
            if (varDecl instanceof CtLocalVariable) {
                this.variableUses.putIfAbsent(varDecl, new HashSet<>());
                this.variableUses.get(varDecl).add(varRead);
            }
        } else if (expr instanceof CtFieldRead) {
            CtFieldRead<?> fieldRead = (CtFieldRead<?>) expr;
            CtElement fieldDecl = fieldRead.getVariable().getDeclaration();
            if (fieldDecl instanceof CtField) {
                this.fieldUses.putIfAbsent(fieldDecl, new HashSet<>());
                this.fieldUses.get(fieldDecl).add(fieldRead);
            }
        }
    }
}
  


public static void main(String[] args) throws Exception {
    // 创建 Spoon 编译器
    Launcher launcher = new Launcher();
    // 设置 classpath
    launcher.getEnvironment().setSourceClasspath(System.getProperty("java.class.path"));
    // 读取 Java 源代码文件
    File inputFile = new File("src/main/java/example/Example.java");
    // 解析 Java 文件并构建模型
    CtModel model = launcher.buildModel(Arrays.asList(inputFile));
    // 分析模型中所有方法的数据流
    DataFlowAnalyzer analyzer = new DataFlowAnalyzer();
    model.processWith(analyzer);
}
  
  private void printResults() {
    System.out.println("Variable uses:");
    this.variableUses.forEach((varDecl, uses) -> {
        System.out.println("  " + varDecl.toString() + " used in:");
        uses.forEach(use -> System.out.println("    " + use.toString()));
    });

    System.out.println("Field uses:");
    this.fieldUses.forEach((fieldDecl, uses) -> {
        System.out.println("  " + fieldDecl.toString() + " used in:");
        uses.forEach(use -> System.out.println("    " + use.toString()));
    });

    System.out.println("Return definitions:");
    this.returnDefs.forEach(def -> System.out.println("  " + def.toString()));

    System.out.println("Return uses:");
    this.returnUses.forEach(use -> System.out.println("  " + use.toString()));
} 
  /*   这里打开注释测试另外一个main
  public static void main(String[] args) throws Exception {
    // 创建 Spoon 编译器
    Launcher launcher = new Launcher();
    // 设置 classpath
    launcher.getEnvironment().setSourceClasspath(System.getProperty("java.class.path"));
    // 读取 Java 源代码文件
    File inputFile = new File("src/main/java/example/Example.java");
    // 解析 Java 文件并构建模型
    CtModel model = launcher.buildModel(Arrays.asList(inputFile));
    // 分析模型中所有方法的数据流
    DataFlowAnalyzer analyzer = new DataFlowAnalyzer();
    model.processWith(analyzer);
    // 输出分析结果
    analyzer.printResults();
}*/
}

