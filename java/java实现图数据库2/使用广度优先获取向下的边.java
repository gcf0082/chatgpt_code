public List<Edge> getEdgesDownstream(Node node, Graph graph) {
    List<Edge> result = new ArrayList<>();
    Queue<Node> queue = new LinkedList<>();
    Set<Node> visited = new HashSet<>();
    queue.offer(node);
    visited.add(node);
    while (!queue.isEmpty()) {
        Node current = queue.poll();
        List<Edge> edges = graph.getEdgesByNode(current);
        for (Edge edge : edges) {
            result.add(edge);
            Node next = edge.getTo();
            if (!visited.contains(next)) {
                visited.add(next);
                queue.offer(next);
            }
        }
    }
    return result;
}
