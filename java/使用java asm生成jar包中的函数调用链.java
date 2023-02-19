import org.objectweb.asm.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JarCallChain {

    private static Map<String, Set<String>> methodCalls = new HashMap<>();

    public static void main(String[] args) throws IOException {
        // Jar包路径
        String jarPath = "path/to/your/jar/file.jar";

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
                                // 如果该方法调用了其他方法，则记录该方法调用的方法名
                                if (!owner.startsWith("java")) {
                                    String methodKey = String.format("%s.%s%s", owner.replace("/", "."), name, descriptor);
                                    Set<String> calledMethods = methodCalls.getOrDefault(name + descriptor, new HashSet<>());
                                    calledMethods.add(methodKey);
                                    methodCalls.put(name + descriptor, calledMethods);
                                }
                            }
                        };
                        return methodVisitor;
                    }
                };
                reader.accept(classVisitor, 0);
            }
        }

        // 输出函数调用链
        for (Map.Entry<String, Set<String>> entry : methodCalls.entrySet()) {
            System.out.println("Method " + entry.getKey() + " calls:");
            for (String calledMethod : entry.getValue()) {
                System.out.println("\t" + calledMethod);
            }
            System.out.println();
        }
    }
}
