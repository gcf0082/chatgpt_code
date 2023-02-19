import soot.*;
import soot.jimple.*;
import soot.tagkit.*;

public class TaintAnalysis extends ForwardFlowAnalysis<Unit, Set<String>> {

    public TaintAnalysis(UnitGraph graph) {
        super(graph);
    }

    @Override
    protected void flowThrough(Set<String> in, Unit unit, Set<String> out) {
        if (unit instanceof AssignStmt) {
            AssignStmt assign = (AssignStmt) unit;
            if (assign.containsInvokeExpr()) {
                SootMethod method = assign.getInvokeExpr().getMethod();
                if (method.getSignature().equals("<java.io.PrintStream: void println(java.lang.String)>")) {
                    // 检测到污点汇
                    System.out.println("Tainted value is printed!");
                }
            } else {
                Value rightOp = assign.getRightOp();
                Value leftOp = assign.getLeftOp();
                if (in.contains(rightOp.toString())) {
                    // 如果右操作数是污点，则将其传播到左操作数
                    out.add(leftOp.toString());
                }
            }
        } else {
            in.copy(out);
        }
    }

    @Override
    protected Set<String> newInitialFlow() {
        return new HashSet<String>();
    }

    @Override
    protected Set<String> entryInitialFlow() {
        return new HashSet<String>();
    }

    @Override
    protected void merge(Set<String> in1, Set<String> in2, Set<String> out) {
        in1.addAll(in2);
        in1.copy(out);
    }

    @Override
    protected void copy(Set<String> source, Set<String> dest) {
        source.copy(dest);
    }

    public static void main(String[] args) {
        String className = "Test";
        SootClass sootClass = Scene.v().loadClassAndSupport(className);
        Scene.v().loadNecessaryClasses();
        sootClass.setApplicationClass();

        // 遍历每个方法
        for (SootMethod method : sootClass.getMethods())
            Body body = method.retrieveActiveBody();
            System.out.println("Analyzing method: " + method.getSignature());

            // 标记参数变量为污点源
            for (Local param : body.getParameterLocals()) {
                String paramName = param.getName();
                System.out.println("Marking parameter " + paramName + " as a source of taint");
                Set<String> taint = new HashSet<String>();
                taint.add(paramName);
                FlowSet<Local> locals = new ArrayPackedFlowSet<Local>();
                locals.add(param);
                this.getFlowBefore(body.getUnits().getFirst()).put(locals, taint);
            }

            // 执行污点分析
            TaintAnalysis analysis = new TaintAnalysis(new ExceptionalUnitGraph(body));
            analysis.doAnalysis();

            // 打印结果
            System.out.println("Tainted variables in method " + method.getSignature() + ":");
            for (Unit unit : body.getUnits()) {
                if (analysis.getFlowBefore(unit) != null) {
                    System.out.println(unit + " -> " + analysis.getFlowBefore(unit));
                }
            }
        }
    }
}
