public List<Edge> getEdgesDownstream(Node node, Graph graph) {

    List<Edge> result = new ArrayList<>();

    Set<Node> visited = new HashSet<>();

    dfs(node, graph, visited, result);

    return result;

}

private void dfs(Node node, Graph graph, Set<Node> visited, List<Edge> result) {

    visited.add(node);

    List<Edge> edges = graph.getEdgesByNode(node);

    for (Edge edge : edges) {

        result.add(edge);

        Node next = edge.getTo();

        if (!visited.contains(next)) {

            dfs(next, graph, visited, result);

        }

    }

}
