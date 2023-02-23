public List<Long> getAncestors(long nodeId) throws SQLException {
    List<Long> ancestors = new ArrayList<>();
    Stack<Long> stack = new Stack<>();
    stack.push(nodeId);
    while (!stack.isEmpty()) {
        long current = stack.pop();
        List<Long> parents = getParents(current);
        for (long parent : parents) {
            ancestors.add(parent);
            stack.push(parent);
        }
    }
    return ancestors;
}
