public List<Edge> getEdgesDownstream(Node node, Graph graph) {
    List<Edge> result = new ArrayList<>();
    Set<Node> visited = new HashSet<>();
    bfs(node, graph, visited, result);
    return result;
}

private void bfs(Node node, Graph graph, Set<Node> visited, List<Edge> result) {
    Queue<Node> queue = new LinkedList<>();
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
                bfs(next, graph, visited, result);
            }
        }
    }
}
