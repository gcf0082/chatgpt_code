import net.bytebuddy.ClassFileVersion;

import net.bytebuddy.asm.Advice;

import net.bytebuddy.description.method.MethodDescription;

import net.bytebuddy.description.type.TypeDescription;

import net.bytebuddy.dynamic.DynamicType;

import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;

import net.bytebuddy.dynamic.scaffold.InstrumentedType;

import net.bytebuddy.matcher.ElementMatchers;

import org.objectweb.asm.ClassVisitor;

import org.objectweb.asm.FieldVisitor;

import org.objectweb.asm.MethodVisitor;

import org.objectweb.asm.Opcodes;

import org.objectweb.asm.Type;

import org.objectweb.asm.commons.Method;

import java.io.File;

import java.io.IOException;

import java.lang.reflect.Method;

import java.nio.file.Files;

import java.nio.file.Path;

import java.nio.file.Paths;

import java.util.*;

public class ClassCallGraphAnalyzer {

    private Map<String, Set<String>> callGraph;

    public static void main(String[] args) throws IOException {

        ClassCallGraphAnalyzer analyzer = new ClassCallGraphAnalyzer();

        analyzer.analyzeClass("com.example.Main");

        analyzer.saveCallGraph("call-graph.txt");

    }

    public ClassCallGraphAnalyzer() {

        callGraph = new HashMap<>();

    }

    public void analyzeClass(String className) throws IOException {

        // 使用 ClassFileLocator 来定位类文件

        ClassFileLocator classFileLocator = ClassFileLocator.ForClassLoader.of(ClassCallGraphAnalyzer.class.getClassLoader());

        byte[] classFile = classFileLocator.locate(className).resolve();

        // 使用 ClassReader 读取类文件的字节码

        ClassReader classReader = new ClassReader(classFile);

        // 使用 ClassVisitor 分析类的字节码

        ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM5) {

            private String currentClassName;

            @Override

            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

                currentClassName = name.replace('/', '.');

                callGraph.putIfAbsent(currentClassName, new HashSet<>());

                super.visit(version, access, name, signature, superName, interfaces);

            }

            @Override

            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);

                return new MethodVisitor(Opcodes.ASM5, methodVisitor) {

                    @Override

                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {

                        // 分析方法调用指令

                        String calledClassName = owner.replace('/', '.');

                        callGraph.putIfAbsent(calledClassName, new HashSet<>());

                        callGraph.get(currentClassName).add(calledClassName);

                        super.visitMethodInsn(opcode, owner, name, desc, itf);

                    }

                };

            }

            @Override

            public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {

                // 分析字段调用关系

                return super.visitField(access, name, desc, signature, value);

            }

        };

        classReader.accept(classVisitor, ClassReader.SKIP_DEBUG);

        // 递归分析依赖的类

        Set<String> dependencies = new HashSet<>(callGraph.keySet());

        for (String dependency : dependencies) {

            if (!dependency.equals(className)) {

                analyzeClass(dependency);

            }

        }

    }

    public void saveCallGraph(String fileName) throws IOException {

        // 将分析结果写入文件

    StringBuilder sb = new StringBuilder();

    for (String className : callGraph.keySet()) {

        sb.append(className).append(" -> ");

        Set<String> calledClasses = callGraph.get(className);

        if (!calledClasses.isEmpty()) {

            List<String> sortedCalledClasses = new ArrayList<>(calledClasses);

            sortedCalledClasses.sort(String::compareTo);

            sb.append(sortedCalledClasses.get(0));

            for (int i = 1; i < sortedCalledClasses.size(); i++) {

                sb.append(", ").append(sortedCalledClasses.get(i));

            }

        }

        sb.append("\n");

    }

    Path filePath = Paths.get(fileName);

    Files.writeString(filePath, sb.toString());

}

}
