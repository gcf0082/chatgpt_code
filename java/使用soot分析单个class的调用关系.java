import java.io.File;
import java.util.Collections;

import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;

public class SingleClassAnalysis {
    public static void main(String[] args) {
        // 设置Soot的类路径和分析的类
        String classpath = "/path/to/classfiles";
        String classname = "com.example.MyClass";

        // 加载分析的类并初始化Soot
        Options.v().set_soot_classpath(classpath);
        SootClass sootClass = Scene.v().loadClassAndSupport(classname);
        Scene.v().loadNecessaryClasses();

        // 设置分析选项，只分析指定的类
        Options.v().setPhaseOption("cg", "verbose:false");
        Options.v().setPhaseOption("cg", "trim-clinit:true");
        Options.v().setPhaseOption("cg.cha", "enabled:false");
        Options.v().setPhaseOption("cg.spark", "enabled:false");
        Options.v().setPhaseOption("cg", "all-reachable:true");
        Options.v().setPhaseOption("cg", "exclude-all:true");
        Options.v().setPhaseOption("cg", "include:" + classname);

        // 构建调用图
        PackManager.v().runPacks();

        // 获取调用图
        CallGraph cg = Scene.v().getCallGraph();

        // 打印调用图
        for (Iterator<Edge> it = cg.iterator(); it.hasNext(); ) {
            Edge edge = it.next();
            SootMethod src = edge.getSrc();
            SootMethod tgt = edge.getTgt();
            System.out.println(src.getSignature() + " -> " + tgt.getSignature());
        }
    }
}
