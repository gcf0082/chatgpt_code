public List<Long> getNodesWithoutParents() throws SQLException {
    List<Long> nodes = getAllNodes();
    List<Long> rootAncestors = new ArrayList<>();
    for (long node : nodes) {
        List<Long> ancestors = getAncestors(node);
        if (ancestors.isEmpty()) {
            rootAncestors.add(node);
        }
    }
    return rootAncestors;
}
