import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;

public class TaintAnalysis {

    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        launcher.addInputResource("path/to/java/file");
        CtModel model = launcher.buildModel();

        CtClass<?> clazz = model.getElements(new TypeFilter<>(CtClass.class)).get(0);

        for (CtMethod<?> method : clazz.getMethods()) {
            System.out.println("Analyzing method: " + method.getSimpleName());

            List<CtLocalVariable<?>> taintedVariables = new ArrayList<>();
            List<CtAssignment<?, ?>> assignments = method.getElements(new TypeFilter<>(CtAssignment.class));
            for (CtAssignment<?, ?> assignment : assignments) {
                if (isTainted(assignment.getAssigned(), taintedVariables)) {
                    taintedVariables.add((CtLocalVariable<?>) assignment.getAssigned());
                }
            }

            if (taintedVariables.size() > 0) {
                System.out.println("Tainted variables in method: " + method.getSimpleName());
                for (CtLocalVariable<?> variable : taintedVariables) {
                    System.out.println(variable.getSimpleName() + " at " + variable.getPosition().getLine());
                }
            } else {
                System.out.println("No tainted variables in method: " + method.getSimpleName());
            }
        }
    }

    private static boolean isTainted(CtExpression<?> expression, List<CtLocalVariable<?>> taintedVariables) {
        if (taintedVariables.stream().anyMatch(v -> v.getReference().equals(expression))) {
            return true;
        }

        if (expression instanceof CtLocalVariable) {
            return isTainted(((CtLocalVariable<?>) expression).getDefaultExpression(), taintedVariables);
        }

        if (expression instanceof CtInvocation) {
            CtInvocation<?> invocation = (CtInvocation<?>) expression;
            CtExecutableReference<?> methodRef = invocation.getExecutable();
            if (methodRef != null && methodRef.getDeclaringType().getQualifiedName().equals("java.lang.System")
                    && methodRef.getSimpleName().equals("getProperty")) {
                List<CtExpression<?>> args = invocation.getArguments();
                if (args.size() == 1 && args.get(0) instanceof CtLiteral) {
                    String key = ((CtLiteral<String>) args.get(0)).getValue();
                    if (key.equals("user.home") || key.equals("user.dir")) {
                        return false;
                    }
                }
            }

            if (isTainted(methodRef, taintedVariables)) {
                return true;
            }

            List<CtExpression<?>> arguments = invocation.getArguments();
            for (
        (CtExpression<?> argument : arguments) {
            if (isTainted(argument, taintedVariables)) {
                return true;
            }
        }

        return false;
    }

    if (expression instanceof CtAbstractInvocation) {
        CtAbstractInvocation<?> invocation = (CtAbstractInvocation<?>) expression;
        if (invocation.getExecutable() instanceof CtExecutableReference) {
            if (isTainted((CtExecutableReference<?>) invocation.getExecutable(), taintedVariables)) {
                return true;
            }
        }

        List<CtExpression<?>> arguments = invocation.getArguments();
        for (CtExpression<?> argument : arguments) {
            if (isTainted(argument, taintedVariables)) {
                return true;
            }
        }

        return false;
    }

    if (expression instanceof CtCodeElement) {
        List<CtCodeElement> elements = ((CtCodeElement) expression).getElements(new Filter<CtCodeElement>() {
            @Override
            public boolean matches(CtCodeElement element) {
                return element instanceof CtStatement;
            }
        });

        for (CtCodeElement element : elements) {
            if (element instanceof CtAssignment) {
                CtAssignment<?, ?> assignment = (CtAssignment<?, ?>) element;
                if (isTainted(assignment.getAssigned(), taintedVariables)) {
                    taintedVariables.add((CtLocalVariable<?>) assignment.getAssigned());
                    return true;
                }
            }
        }
    }

    return false;
}

private static boolean isTainted(CtExecutableReference<?> methodRef, List<CtLocalVariable<?>> taintedVariables) {
    if (methodRef.getDeclaringType().getQualifiedName().equals("java.util.Scanner")
            && methodRef.getSimpleName().equals("nextLine")) {
        return true;
    }

    if (methodRef.getDeclaringType().getQualifiedName().equals("java.lang.System")
            && methodRef.getSimpleName().equals("getenv")) {
        return true;
    }

    return false;
}
 }
