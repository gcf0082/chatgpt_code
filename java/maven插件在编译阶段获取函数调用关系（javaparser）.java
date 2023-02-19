/***
<build>
  <plugins>
    <plugin>
      <groupId>com.example</groupId>
      <artifactId>call-graph-plugin</artifactId>
      <version>1.0.0</version>
      <executions>
        <execution>
          <phase>compile</phase>
          <goals>
            <goal>generate-call-graph</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
  </plugins>
</build>
***/
package com.example;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Mojo(
    name = "call-graph",
    defaultPhase = LifecyclePhase.COMPILE,
    requiresDependencyResolution = ResolutionScope.COMPILE
)
public class CallGraphMojo extends AbstractMojo {

    private final Set<String> methods = new HashSet<>();

    public void execute() throws MojoExecutionException {
        MavenProject project = (MavenProject) getPluginContext().get("project");
        for (String sourceRoot : project.getCompileSourceRoots()) {
            visitSourceDirectory(new File(sourceRoot));
        }
        for (String method : methods) {
            getLog().info(method);
        }
    }

    private void visitSourceDirectory(File directory) throws MojoExecutionException {
        if (!directory.exists()) {
            return;
        }
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                visitSourceDirectory(file);
            }
        } else if (directory.isFile() && directory.getName().endsWith(".java")) {
            visitSourceFile(directory);
        }
    }

    private void visitSourceFile(File file) throws MojoExecutionException {
        try {
            CompilationUnit cu = JavaParser.parse(file);
            new MethodVisitor().visit(cu, null);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to parse source file: " + file, e);
        }
    }

    private class MethodVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration methodDeclaration, Void arg) {
            String methodName = methodDeclaration.getSignature().toString();
            for (MethodCallExpr methodCallExpr : methodDeclaration.findAll(MethodCallExpr.class)) {
                String calledMethodName = methodCallExpr.getName().getIdentifier();
                String calledMethod = calledMethodName + "(" + methodCallExpr.getArguments().size() + ")";
                methods.add(methodName + " -> " + calledMethod);
            }
        }
    }
}
