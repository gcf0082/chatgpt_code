import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.CtVisitor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class App {
    public static void main(String[] args) {
        Launcher launcher = new Launcher();
        launcher.addInputResource("path/to/your/source/code");
        CtModel model = launcher.buildModel();

        model.processWith(new CtVisitor() {
            @Override
    public void visitCtElement(CtElement element) {
    if (element instanceof CtMethod) {
        CtMethod method = (CtMethod) element;
        for (CtExecutableReference<?> reference : method.getExecutableReferences()) {
            if (reference.isExecutable()) {
                CtMethod calledMethod = (CtMethod) reference.getExecutable().getDeclaration();
                try {
                    Connection conn = DriverManager.getConnection("jdbc:sqlite:path/to/database.db");
                    PreparedStatement stmt = conn.prepareStatement("INSERT INTO function_call_relations (caller, callee) VALUES (?, ?)");
                    stmt.setString(1, method.getSimpleName());
                    stmt.setString(2, calledMethod.getSimpleName());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
        });
    }
}
