public List<Edge> getEdgesDownstream(Node node, Graph graph) {
    List<Edge> result = new ArrayList<>();
    Set<Node> visited = new HashSet<>();
    Set<Node> visitedFrom = new HashSet<>(); //已经处理过的源
    Queue<Node> queue = new LinkedList<>();
    visited.add(node);
    queue.offer(node);
    while (!queue.isEmpty()) {
        Node current = queue.poll();
        List<Edge> edges = graph.getEdgesByNodeTo(current);
        for (Edge edge : edges) {
            Node from = edge.getFrom();
            Node next = edge.getTo();
            if (visitedFrom.contains(next)) {
                continue;
            }
            result.add(edge);
            visitedFrom.add(from);
            if (!visited.contains(next)) {
                visited.add(next);
                queue.offer(next);
            }
        }
    }
    return result;
}
