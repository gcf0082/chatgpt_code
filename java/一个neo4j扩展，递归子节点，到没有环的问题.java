import org.neo4j.graphdb.*;
import org.neo4j.procedure.*;

import java.util.*;

public class MyExtension {

    @Context
    public GraphDatabaseService db;

    @Procedure("my.extension.getDependencyTree")
    @Description("Get the dependency tree for a given node")
    public Map<String, Object> getDependencyTree(@Name("nodeId") Long nodeId) {
        Map<String, Object> result = new HashMap<>();
        Node rootNode = db.getNodeById(nodeId);
        result.put("id", rootNode.getId());
        result.put("children", getChildren(rootNode, new HashSet<>()));
        return result;
    }

    private List<Map<String, Object>> getChildren(Node node, Set<Long> visited) {
        List<Map<String, Object>> children = new ArrayList<>();
        visited.add(node.getId());
        for (Relationship relationship : node.getRelationships(Direction.OUTGOING)) {
            Node childNode = relationship.getEndNode();
            if (!visited.contains(childNode.getId())) {
                visited.add(childNode.getId());
                List<Map<String, Object>> subChildren = getChildren(childNode, new HashSet<>(visited));
                Map<String, Object> childMap = new HashMap<>();
                childMap.put("id", childNode.getId());
                if (!subChildren.isEmpty()) {
                    childMap.put("children", subChildren);
                }
                children.add(childMap);
            }
        }
        return children;
    }
}
