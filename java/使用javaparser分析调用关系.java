import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.utils.SourceRoot;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MethodCallVisitor extends VoidVisitorAdapter<Void> {

    private Map<String, Set<String>> callGraph = new HashMap<>();

    @Override
    public void visit(MethodDeclaration method, Void arg) {
        Set<String> calledMethods = new HashSet<>();
        method.findAll(MethodCallExpr.class).forEach(expr -> calledMethods.add(expr.getNameAsString()));
        if (!calledMethods.isEmpty()) {
            callGraph.put(method.getNameAsString(), calledMethods);
        }
    }

    public Map<String, Set<String>> getCallGraph() {
        return callGraph;
    }

    public static void main(String[] args) throws IOException {
        String projectPath = "path/to/your/project";
        SourceRoot sourceRoot = new SourceRoot(Paths.get(projectPath));
        MethodCallVisitor visitor = new MethodCallVisitor();
        sourceRoot.parse("", new JavaParserConfiguration(), visitor);
        Map<String, Set<String>> callGraph = visitor.getCallGraph();
        for (String method : callGraph.keySet()) {
            System.out.print(method + " calls ");
            for (String calledMethod : callGraph.get(method)) {
                System.out.print(calledMethod + " ");
            }
            System.out.println();
        }
    }
}
