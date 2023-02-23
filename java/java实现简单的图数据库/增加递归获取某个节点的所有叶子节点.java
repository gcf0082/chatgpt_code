public List<Long> getDescendants(long nodeId) throws SQLException {
    List<Long> descendants = new ArrayList<>();
    Stack<Long> stack = new Stack<>();
    stack.push(nodeId);
    while (!stack.isEmpty()) {
        long current = stack.pop();
        List<Long> children = getChildren(current);
        for (long child : children) {
            descendants.add(child);
            stack.push(child);
        }
    }
    return descendants;
}
