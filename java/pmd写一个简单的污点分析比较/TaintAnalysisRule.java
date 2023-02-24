public class TaintAnalysisRule extends AbstractJavaRule {
    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        TypeNode typeNode = node.getFirstChildOfType(TypeNode.class);
        String typeName = typeNode.getTypeImage();
        if ("String".equals(typeName)) {
            List<ASTVariableDeclarator> declarators = node.findDescendantsOfType(ASTVariableDeclarator.class);
            for (ASTVariableDeclarator declarator : declarators) {
                String variableName = declarator.getImage();
                VariableUsageVisitor visitor = new VariableUsageVisitor(variableName);
                node.getFirstParentOfType(ASTBlockStatement.class).jjtAccept(visitor, null);
                if (visitor.isTainted()) {
                    addViolation(data, declarator);
                }
            }
        }
        return super.visit(node, data);
    }
}

class VariableUsageVisitor extends AbstractJavaParserVisitor<Void, Void> {
    private final String variableName;
    private boolean isTainted;

    public VariableUsageVisitor(String variableName) {
        this.variableName = variableName;
    }

    public boolean isTainted() {
        return isTainted;
    }

    @Override
    public Void visit(ASTPrimaryExpression node, Void data) {
        if (node.getImage().equals(variableName)) {
            isTainted = true;
        }
        return super.visit(node, data);
    }
}
