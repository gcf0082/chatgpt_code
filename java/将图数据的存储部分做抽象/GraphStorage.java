public interface GraphStorage {

    void addNode(Node node);

    void addEdge(Edge edge);

    Node getNodeById(String id);

    List<Edge> getIncomingEdgesByNodeId(String id);

    List<Edge> getOutgoingEdgesByNodeId(String id);

}
