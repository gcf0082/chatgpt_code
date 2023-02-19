import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class CallChainFinder {

    // 记录方法调用图
    private final Map<String, Set<String>> methodCalls = new HashMap<>();
    // 记录每个方法所在的 Jar 包
    private final Map<String, String> methodJars = new HashMap<>();

    public void analyzeJar(String jarPath) throws IOException {
        // 使用 ASM 解析 Jar 包中的所有类
        try (InputStream jarStream = getClass().getClassLoader().getResourceAsStream(jarPath)) {
            JarClassVisitor jarClassVisitor = new JarClassVisitor(jarPath);
            JarReader jarReader = new JarReader(jarStream, jarClassVisitor);
            jarReader.read();
        }
    }

    public Stack<String> findCallers(String methodName, String methodDesc) {
        // 查找目标函数所在的 Jar 包
        String jarPath = methodJars.get(methodName + methodDesc);
        if (jarPath == null) {
            return null;
        }

        // 遍历目标 Jar 包中的所有类，找到目标函数并记录其所在的类
        Set<String> targetClasses = new HashSet<>();
        try (InputStream jarStream = getClass().getClassLoader().getResourceAsStream(jarPath)) {
            JarClassVisitor jarClassVisitor = new JarClassVisitor(jarPath, methodName, methodDesc, targetClasses);
            JarReader jarReader = new JarReader(jarStream, jarClassVisitor);
            jarReader.read();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // 在目标函数所在的类中，查找其被调用的方法，并记录这些方法所在的类
        Set<String> callerClasses = new HashSet<>();
        for (String className : targetClasses) {
            Set<String> callers = methodCalls.get(className + "." + methodName + methodDesc);
            if (callers != null) {
                for (String caller : callers) {
                    String callerClass = caller.substring(0, caller.lastIndexOf('.'));
                    callerClasses.add(callerClass);
                }
            }
        }

        // 遍历这些类，并查找它们被调用的方法，重复该过程，直到找到根方法
            Stack<String> callStack = new Stack<>();
    for (String callerClass : callerClasses) {
        findCallers(callerClass, methodName, methodDesc, callStack);
    }
    return callStack;
}

private void findCallers(String className, String methodName, String methodDesc, Stack<String> callStack) {
    String key = className + "." + methodName + methodDesc;
    if (methodCalls.containsKey(key)) {
        Set<String> callers = methodCalls.get(key);
        for (String caller : callers) {
            String callerClass = caller.substring(0, caller.lastIndexOf('.'));
            callStack.push(caller);
            findCallers(callerClass, methodName, methodDesc, callStack);
        }
    }
}

// 解析 Jar 包中的所有类
private class JarClassVisitor extends ClassVisitor {

    private final String jarPath;
    private final String methodName;
    private final String methodDesc;
    private final Set<String> targetClasses;

    private String className;

    public JarClassVisitor(String jarPath) {
        super(Opcodes.ASM9);
        this.jarPath = jarPath;
        this.methodName = null;
        this.methodDesc = null;
        this.targetClasses = null;
    }

    public JarClassVisitor(String jarPath, String methodName, String methodDesc, Set<String> targetClasses) {
        super(Opcodes.ASM9);
        this.jarPath = jarPath;
        this.methodName = methodName;
        this.methodDesc = methodDesc;
        this.targetClasses = targetClasses;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        className = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (methodName == null || name.equals(methodName) && desc.equals(methodDesc)) {
            // 找到目标函数
            if (targetClasses != null) {
                targetClasses.add(className);
            }
            // 记录方法调用图
            MethodVisitor mv = new MethodCallVisitor(className + "." + name + desc);
            return mv == null ? super.visitMethod(access, name, desc, signature, exceptions) : mv;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}

// 记录方法调用图
private class MethodCallVisitor extends MethodVisitor {

    private final String method;

    public MethodCallVisitor(String method) {
        super(Opcodes.ASM9);
        this.method = method;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        String key = owner + "." + name + desc;
        Set<String> callers = methodCalls.computeIfAbsent(key, k -> new HashSet<>());
        callers.add(method);
    }
}

// 解析 Jar 包
private static class JarReader {

    private final InputStream inputStream;
    private final JarClassVisitor classVisitor;

    public JarReader(InputStream inputStream, JarClassVisitor classVisitor) {
        this.inputStream = inputStream;
        this.classVisitor = classVisitor;
    }

    public void read() throws IOException {
        JarInputStream jarStream = new JarInputStream(inputStream);
        JarEntry jarEntry;
        while ((jarEntry = jarStream.getNextJarEntry()) != null) {
            String entryName = jarEntry.getName();
            if (entryName.endsWith(".class")) {
                String className = entryName.substring(0, entryName.length() - ".class".length()).replace('/', '.');
                ClassReader classReader
                = new ClassReader(jarStream);
                classReader.accept(classVisitor, 0);
            }
        }
    }
}
}

//调用方式
CallChainFinder finder = new CallChainFinder();
Stack<String> callStack = finder.findUpstreamCallChain("/path/to/my.jar", "com.example.MyClass", "myMethod", "()V");
while (!callStack.isEmpty()) {
    System.out.println(callStack.pop());
}
