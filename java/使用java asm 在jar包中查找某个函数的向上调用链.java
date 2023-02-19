import org.objectweb.asm.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JarCallChain {

    private static Map<String, Set<String>> methodCalls = new HashMap<>();
    private static Set<String> visitedMethods = new HashSet<>();
    private static List<String> callChain = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        // Jar包路径
        String jarPath = "path/to/your/jar/file.jar";
        // 要查找的函数名
        String targetMethod = "com.example.MyClass.myMethod(Ljava/lang/String;)V";

        // 遍历Jar包中的所有class文件
        Enumeration<JarEntry> entries = new JarFile(jarPath).entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().endsWith(".class")) {
                // 加载class文件的字节码
                InputStream inputStream = new JarFile(jarPath).getInputStream(entry);
                ClassReader reader = new ClassReader(inputStream);

                // 使用ClassVisitor来访问class文件中的方法
                ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        // 使用MethodVisitor来访问方法的字节码
                        MethodVisitor methodVisitor = new MethodVisitor(Opcodes.ASM5) {
                            @Override
                            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                                // 如果该方法调用了目标函数，则记录该方法调用目标函数，并向上遍历调用链
                                if ((owner + "." + name + descriptor).equals(targetMethod)) {
                                    callChain.add(owner.replace("/", ".") + "." + name + descriptor);
                                    visitedMethods.add(owner + "." + name + descriptor);
                                    traceCallers(owner, name, descriptor);
                                }
                            }
                        };
                        return methodVisitor;
                    }
                };
                reader.accept(classVisitor, 0);
            }
        }

        // 输出函数的向上调用链
        if (!callChain.isEmpty()) {
            System.out.println("Call chain for method " + targetMethod + ":");
            for (int i = callChain.size() - 1; i >= 0; i--) {
                System.out.println("\t" + callChain.get(i));
            }
        } else {
            System.out.println("Method " + targetMethod + " not found in the jar.");
        }
    }

    private static void traceCallers(String owner, String name, String descriptor) {
        String methodKey = owner + "." + name + descriptor;
        if (visitedMethods.contains(methodKey)) {
            return;
        }
        visitedMethods.add(methodKey);

        for (String method : methodCalls.keySet()) {
            if (methodCalls.get(method).contains(methodKey)) {
                callChain.add(method);
                traceCallers(method.substring(0, method.lastIndexOf(".")), method.substring(method.lastIndexOf(".") + 1), method.substring(method.lastIndexOf("(")));
                return;
            }
        }
    }
}
