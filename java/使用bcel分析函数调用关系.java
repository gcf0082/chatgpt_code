import java.io.IOException;

import org.apache.bcel.Repository;

import org.apache.bcel.classfile.JavaClass;

import org.apache.bcel.classfile.Method;

import org.apache.bcel.generic.ConstantPoolGen;

import org.apache.bcel.generic.Instruction;

import org.apache.bcel.generic.InstructionHandle;

import org.apache.bcel.generic.InstructionList;

import org.apache.bcel.generic.MethodGen;

import org.apache.bcel.generic.ObjectType;

import org.apache.bcel.generic.ReferenceType;

import org.apache.bcel.generic.Type;

public class BcelExample {

    public static void main(String[] args) throws IOException {

        String className = "com.example.MyClass";

        JavaClass jc = Repository.lookupClass(className);

        for (Method method : jc.getMethods()) {

            if (method.getCode() == null) {

                continue;

            }

            MethodGen methodGen = new MethodGen(method, jc.getClassName(), new ConstantPoolGen(jc.getConstantPool()));

            InstructionList instructions = methodGen.getInstructionList();

            if (instructions == null) {

                continue;

            }

            InstructionHandle[] handles = instructions.getInstructionHandles();

            for (InstructionHandle handle : handles) {

                Instruction instruction = handle.getInstruction();

                if (instruction instanceof org.apache.bcel.generic.InvokeInstruction) {

                    org.apache.bcel.generic.InvokeInstruction invokeInstruction = (org.apache.bcel.generic.InvokeInstruction) instruction;

                    String methodName = invokeInstruction.getMethodName(cp);

                    String className = invokeInstruction.getReferenceType(cp).toString();

                    System.out.println("Method " + method.getName() + " calls " + methodName + " from " + className);

                }

            }

        }

    }

}
