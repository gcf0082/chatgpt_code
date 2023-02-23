public class Graph {

    private List<Node> nodes;

    private List<Edge> edges;

    public Graph() {

        this.nodes = new ArrayList<>();

        this.edges = new ArrayList<>();

    }

    public Node createNode(String label) {

        Node node = new Node(label);

        this.nodes.add(node);

        return node;

    }

    public Edge createEdge(String label, Node from, Node to) {

        Edge edge = new Edge(label, from, to);

        this.edges.add(edge);

        return edge;

    }

    public List<Node> getNodesByLabel(String label) {

        List<Node> result = new ArrayList<>();

        for (Node node : nodes) {

            if (node.getLabel().equals(label)) {

                result.add(node);

            }

        }

        return result;

    }

    public List<Edge> getEdgesByLabel(String label) {

        List<Edge> result = new ArrayList<>();

        for (Edge edge : edges) {

            if (edge.getLabel().equals(label)) {

                result.add(edge);

            }

        }

        return result;

    }

    public List<Edge> getEdgesByNode(Node node) {

        List<Edge> result = new ArrayList<>();

        for (Edge edge : edges) {

            if (edge.getFrom().equals(node) || edge.getTo().equals(node)) {

                result.add(edge);

            }

        }

        return result;

    }

}
