public class InMemoryGraphStorage implements GraphStorage {

    private Map<String, Node> nodes;

    private Map<String, List<Edge>> incomingEdgesIndex;

    private Map<String, List<Edge>> outgoingEdgesIndex;

    public InMemoryGraphStorage() {

        this.nodes = new HashMap<>();

        this.incomingEdgesIndex = new HashMap<>();

        this.outgoingEdgesIndex = new HashMap<>();

    }

    @Override

    public void addNode(Node node) {

        nodes.put(node.getId(), node);

    }

    @Override

    public void addEdge(Edge edge) {

        Node from = edge.getFrom();

        Node to = edge.getTo();

        String fromId = from.getId();

        String toId = to.getId();

        incomingEdgesIndex.computeIfAbsent(toId, k -> new ArrayList<>()).add(edge);

        outgoingEdgesIndex.computeIfAbsent(fromId, k -> new ArrayList<>()).add(edge);

    }

    @Override

    public Node getNodeById(String id) {

        return nodes.get(id);

    }

    @Override

    public List<Edge> getIncomingEdgesByNodeId(String id) {

        return incomingEdgesIndex.getOrDefault(id, new ArrayList<>());

    }

    @Override

    public List<Edge> getOutgoingEdgesByNodeId(String id) {

        return outgoingEdgesIndex.getOrDefault(id, new ArrayList<>());

    }

}
