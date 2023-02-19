import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.PackManager;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.internal.JReturnStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

public class TaintAnalysis extends ForwardFlowAnalysis<Unit, FlowSet<String>> {
    // 记录每个函数的参数名称
    private Map<SootMethod, List<String>> methodParamMap = new HashMap<>();

    public TaintAnalysis(UnitGraph graph) {
        super(graph);
        // 收集每个函数的参数名称
        for (SootMethod method : graph.getBody().getMethod().getDeclaringClass().getMethods()) {
            List<String> paramNames = new ArrayList<>();
            for (int i = 0; i < method.getParameterCount(); i++) {
                paramNames.add(method.getParameterName(i));
            }
            methodParamMap.put(method, paramNames);
        }
    }

    @Override
    protected void flowThrough(FlowSet<String> in, Unit unit, FlowSet<String> out) {
        // 复制in到out
        out.clear();
        out.union(in);

        // 处理赋值语句
        if (unit instanceof AssignStmt) {
            AssignStmt assignStmt = (AssignStmt) unit;
            Value left = assignStmt.getLeftOp();
            Value right = assignStmt.getRightOp();
            if (right instanceof Local) {
                // 处理将参数传递给其他变量的情况
                if (in.contains(right.toString())) {
                    System.out.println(right + " is from method parameter.");
                    out.add(left.toString());
                }
            } else if (right instanceof InvokeExpr) {
                // 处理将参数传递给函数调用的情况
                InvokeExpr invokeExpr = (InvokeExpr) right;
                if (invokeExpr.getMethod().getDeclaringClass().isApplicationClass()) {
                    for (Value arg : invokeExpr.getArgs()) {
                        if (in.contains(arg.toString())) {
                            System.out.println(arg + " is from method parameter.");
                            out.add(left.toString());
                            break;
                        }
                    }
                }
            }
        } else if (unit instanceof ReturnStmt) {
            // 处理函数返回值的情况
            ReturnStmt returnStmt = (ReturnStmt) unit;
            Value value = returnStmt.getOp();
            if (in.contains(value.toString())) {
                System.out.println(value + " is from method parameter.");
                out.add(value.toString());
            }
        }  else if (unit instanceof IdentityStmt) {
            // 处理函数参数的情况
            IdentityStmt identityStmt = (IdentityStmt) unit;
            Value right = identityStmt.getRightOp();
            if (right instanceof Local) {
                Local local = (Local) right;
                SootMethod method = ((Stmt) unit).getInvokeExpr().getMethod();
                List<String> paramNames = methodParamMap.get(method);
                int index = paramNames.indexOf(local.getName());
                if (index != -1) {
                    System.out.println(local + " is method parameter.");
                    out.add(local.getName());
                }
            }
        }
    }

@Override
protected void copy(FlowSet<String> source, FlowSet<String> dest) {
    source.copy(dest);
}

@Override
protected FlowSet<String> newInitialFlow() {
    return new ArraySparseSet<>();
}

@Override
protected FlowSet<String> entryInitialFlow() {
    return new ArraySparseSet<>();
}

@Override
protected void merge(FlowSet<String> in1, FlowSet<String> in2, FlowSet<String> out) {
    out.union(in1);
    out.union(in2);
}

public static void main(String[] args) {
    // 设置Soot参数
    String classpath = "bin"; // 类路径
    String classname = "Test"; // 待分析的类名
    String methodname = "foo"; // 待分析的方法名
    PackManager.v().getPack("jtp").add(new Transform("jtp.taintanalysis", new BodyTransformer() {
        @Override
        protected void internalTransform(Body body, String phase, Map options) {
            // 只分析指定的函数
            if (body.getMethod().getName().equals(methodname)) {
                // 构造控制流图
                UnitGraph graph = new ExceptionalUnitGraph(body);
                // 进行污点分析
                TaintAnalysis taintAnalysis = new TaintAnalysis(graph);
                taintAnalysis.doAnalysis();

                // 输出结果
                for (Unit unit : body.getUnits()) {
                    if (unit instanceof JInvokeStmt) {
                        InvokeExpr invokeExpr = ((JInvokeStmt) unit).getInvokeExpr();
                        System.out.println("Call " + invokeExpr.getMethod().getName() + " with arguments:");
                        for (Value arg : invokeExpr.getArgs()) {
                            if (taintAnalysis.getFlowAfter(unit).contains(arg.toString())) {
                                System.out.println("\t" + arg + " is tainted.");
                            } else {
                                System.out.println("\t" + arg + " is untainted.");
                            }
                        }
                    } else if (unit instanceof JReturnStmt) {
                        Value value = ((JReturnStmt) unit).getOp();
                        if (taintAnalysis.getFlowAfter(unit).contains(value.toString())) {
                            System.out.println("Return " + value + " is tainted.");
                        } else {
                            System.out.println("Return " + value + " is untainted.");
                        }
                    }
                }
            }
        }
    }));
    // 加载类并启动Soot
    soot.Main.main(new String[] { "-cp", classpath, classname });
}

