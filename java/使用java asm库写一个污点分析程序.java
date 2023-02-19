import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class TaintAnalysis {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Usage: java TaintAnalysis <class file> <method signature>");
            return;
        }

        String classFile = args[0];
        String methodSignature = args[1];

        InputStream is = TaintAnalysis.class.getResourceAsStream(classFile);
        ClassReader reader = new ClassReader(is);
        TaintAnalysisVisitor visitor = new TaintAnalysisVisitor(methodSignature);
        reader.accept(visitor, 0);

        for (Map.Entry<Integer, String> taintedVar : visitor.getTaintedVariables().entrySet()) {
            System.out.println("Tainted variable " + taintedVar.getValue() + " at line " + taintedVar.getKey());
        }
    }

    static class TaintAnalysisVisitor extends ClassVisitor {
        private final String methodSignature;
        private final Map<Integer, String> taintedVariables = new HashMap<>();

        public TaintAnalysisVisitor(String methodSignature) {
            super(Opcodes.ASM7);
            this.methodSignature = methodSignature;
        }

        public Map<Integer, String> getTaintedVariables() {
            return taintedVariables;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);

            String fullMethodName = name + descriptor;
            if (!fullMethodName.equals(methodSignature)) {
                return mv;
            }

            return new MethodVisitor(Opcodes.ASM7, mv) {
                private int currentLine = -1;

                @Override
                public void visitLineNumber(int line, org.objectweb.asm.Label start) {
                    currentLine = line;
                    super.visitLineNumber(line, start);
                }

                @Override
                public void visitVarInsn(int opcode, int var) {
                    if (opcode == Opcodes.ILOAD) {
                        taintedVariables.put(currentLine, "var" + var);
                    }
                    super.visitVarInsn(opcode, var);
                }

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                    if (opcode == Opcodes.GETFIELD) {
                        Type type = Type.getType(descriptor);
                        if (type.getSort() == Type.OBJECT) {
                            taintedVariables.put(currentLine, name);
                        }
                    }
                    super.visitFieldInsn(opcode, owner, name, descriptor);
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                    if (opcode == Opcodes.INVOKESTATIC && owner.equals("java/lang/System") && name.equals("getenv")) {
                        taintedVariables.put(currentLine, "environment variable");
                    }
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }
            };
        }
    }
}
