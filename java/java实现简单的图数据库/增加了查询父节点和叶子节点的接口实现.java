public List<Long> getParents(long nodeId) throws SQLException {

    List<Long> parents = new ArrayList<>();

    String query = "SELECT source FROM edges WHERE target = ?";

    try (PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setLong(1, nodeId);

        ResultSet result = statement.executeQuery();

        while (result.next()) {

            parents.add(result.getLong("source"));

        }

    }

    return parents;

}

public List<Long> getChildren(long nodeId) throws SQLException {

    List<Long> children = new ArrayList<>();

    String query = "SELECT target FROM edges WHERE source = ?";

    try (PreparedStatement statement = connection.prepareStatement(query)) {

        statement.setLong(1, nodeId);

        ResultSet result = statement.executeQuery();

        while (result.next()) {

            children.add(result.getLong("target"));

        }

    }

    return children;

}
