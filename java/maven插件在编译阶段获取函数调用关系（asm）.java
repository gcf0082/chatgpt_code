package com.example;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Mojo(
    name = "call-graph-asm",
    defaultPhase = LifecyclePhase.COMPILE,
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class CallGraphASM extends AbstractMojo {

    private final Set<String> methods = new HashSet<>();

    public void execute() throws MojoExecutionException {
        MavenProject project = (MavenProject) getPluginContext().get("project");
        for (String dependency : project.getCompileClasspathElements()) {
            visitDependency(new File(dependency));
        }
        for (String method : methods) {
            getLog().info(method);
        }
    }

    private void visitDependency(File file) throws MojoExecutionException {
        try (InputStream is = new FileInputStream(file)) {
            ClassReader cr = new ClassReader(is);
            cr.accept(new MethodVisitor(), ClassReader.SKIP_FRAMES);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to visit dependency: " + file, e);
        }
    }

    private class MethodVisitor extends ClassVisitor {

        private String owner;

        public MethodVisitor() {
            super(Opcodes.ASM9);
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            owner = name;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            return new MethodCallVisitor(owner, name, descriptor);
        }
    }

    private class MethodCallVisitor extends MethodVisitor {

        private final String owner;
        private final String methodName;

        public MethodCallVisitor(String owner, String methodName, String methodDescriptor) {
            super(Opcodes.ASM9);
            this.owner = owner;
            this.methodName = methodName;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            String calledMethod = name + "(" + Type.getArgumentTypes(descriptor).length + ")";
            methods.add(owner + "." + calledMethod + " -> " + this.owner + "." + methodName);
        }
    }
}
