public List<Edge> getAncestorEdges(Node node) {
    List<Edge> edges = new ArrayList<>();
    Stack<Node> stack = new Stack<>();
    Map<Long, Integer> visited = new HashMap<>();

    // 初始化栈
    stack.push(node);
    visited.put(node.getId(), 0);

    // 遍历节点
    while (!stack.isEmpty()) {
        Node current = stack.peek();

        // 处理当前节点
        if (visited.get(current.getId()) == 0) {
            visited.put(current.getId(), 1);
            List<Node> parents = getParents(current);
            for (Node parent : parents) {
                if (!visited.containsKey(parent.getId())) {
                    // 将父节点入栈
                    stack.push(parent);
                    visited.put(parent.getId(), 0);
                } else if (visited.get(parent.getId()) == 1) {
                    // 如果父节点已经被访问过，说明存在循环，跳过
                    continue;
                }
                edges.add(new Edge(parent, current));
            }
        } else {
            // 弹出已经处理过的节点
            stack.pop();
            visited.put(current.getId(), 2);
        }
    }

    return edges;
}
