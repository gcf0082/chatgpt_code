import java.util.List;

public class Graph {
    private final GraphStorage storage;

    public Graph(GraphStorage storage) {
        this.storage = storage;
    }

    public void addNode(Node node) {
        storage.addNode(node);
    }

    public void addEdge(Edge edge) {
        storage.addEdge(edge);
    }

    public Node getNodeById(String id) {
        return storage.getNodeById(id);
    }

    public List<Node> getAllNodes() {
        return storage.getAllNodes();
    }

    public List<Edge> getAllEdges() {
        return storage.getAllEdges();
    }

    public List<Edge> getOutgoingEdgesByNodeId(String nodeId) {
        return storage.getOutgoingEdgesByNodeId(nodeId);
    }

    public List<Edge> getIncomingEdgesByNodeId(String nodeId) {
        return storage.getIncomingEdgesByNodeId(nodeId);
    }

    public List<Edge> getEdgeByNodes(String fromNodeId, String toNodeId) {
        return storage.getEdgeByNodes(fromNodeId, toNodeId);
    }

    public List<Edge> getDescendantEdges(String nodeId) {
        return storage.getDescendantEdges(nodeId);
    }

    public List<Edge> getAncestorEdges(String nodeId) {
        return storage.getAncestorEdges(nodeId);
    }
}
