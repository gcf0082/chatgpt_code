public class Graph {
    private Map<String, Node> nodes;
    private Map<String, List<Edge>> incomingEdgesIndex; // 入边索引
    private Map<String, List<Edge>> outgoingEdgesIndex; // 出边索引

    public Graph() {
        this.nodes = new HashMap<>();
        this.incomingEdgesIndex = new HashMap<>();
        this.outgoingEdgesIndex = new HashMap<>();
    }

    public void addNode(Node node) {
        nodes.put(node.getId(), node);
    }

    public void addEdge(Edge edge) {
        Node from = edge.getFrom();
        Node to = edge.getTo();
        String fromId = from.getId();
        String toId = to.getId();

        incomingEdgesIndex.computeIfAbsent(toId, k -> new ArrayList<>()).add(edge);
        outgoingEdgesIndex.computeIfAbsent(fromId, k -> new ArrayList<>()).add(edge);
    }

    public Node getNodeById(String id) {
        return nodes.get(id);
    }

    public List<Edge> getIncomingEdgesByNodeId(String id) {
        return incomingEdgesIndex.getOrDefault(id, new ArrayList<>());
    }

    public List<Edge> getOutgoingEdgesByNodeId(String id) {
        return outgoingEdgesIndex.getOrDefault(id, new ArrayList<>());
    }
}
