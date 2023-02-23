import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteGraphStorage implements GraphStorage {
    private Connection connection;
    private PreparedStatement addNodeStatement;
    private PreparedStatement addEdgeStatement;
    private PreparedStatement getNodeByIdStatement;
    private PreparedStatement getIncomingEdgesByNodeIdStatement;
    private PreparedStatement getOutgoingEdgesByNodeIdStatement;

    public SQLiteGraphStorage(String databaseUrl) throws SQLException {
        this.connection = DriverManager.getConnection(databaseUrl);
        createTables();
        prepareStatements();
    }

    private void createTables() throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS nodes (id TEXT PRIMARY KEY)");
        statement.executeUpdate(
            "CREATE TABLE IF NOT EXISTS edges (id TEXT PRIMARY KEY,
}

    addEdgeStatement = connection.prepareStatement(

        "INSERT INTO edges (id, from_id, to_id) VALUES (?, ?, ?)");

    getNodeByIdStatement = connection.prepareStatement(

        "SELECT id FROM nodes WHERE id = ?");

    getIncomingEdgesByNodeIdStatement = connection.prepareStatement(

        "SELECT id, from_id, to_id FROM edges WHERE to_id = ?");

    getOutgoingEdgesByNodeIdStatement = connection.prepareStatement(

        "SELECT id, from_id, to_id FROM edges WHERE from_id = ?");

}

@Override

public void addNode(Node node) throws SQLException {

    addNodeStatement.setString(1, node.getId());

    addNodeStatement.executeUpdate();

}

@Override

public void addEdge(Edge edge) throws SQLException {

    addEdgeStatement.setString(1, edge.getId());

    addEdgeStatement.setString(2, edge.getFrom().getId());

    addEdgeStatement.setString(3, edge.getTo().getId());

    addEdgeStatement.executeUpdate();

}

@Override

public Node getNodeById(String id) throws SQLException {

    getNodeByIdStatement.setString(1, id);

    ResultSet resultSet = getNodeByIdStatement.executeQuery();

    if (resultSet.next()) {

        return new Node(id);

    } else {

        return null;

    }

}

@Override

public List<Edge> getIncomingEdgesByNodeId(String id) throws SQLException {

    getIncomingEdgesByNodeIdStatement.setString(1, id);

    ResultSet resultSet = getIncomingEdgesByNodeIdStatement.executeQuery();

    List<Edge> edges = new ArrayList<>();

    while (resultSet.next()) {

        String edgeId = resultSet.getString("id");

        String fromId = resultSet.getString("from_id");

        String toId = resultSet.getString("to_id");

        Node from = new Node(fromId);

        Node to = new Node(toId);

        Edge edge = new Edge(edgeId, from, to);

        edges.add(edge);

    }

    return edges;

}

@Override

public List<Edge> getOutgoingEdgesByNodeId(String id) throws SQLException {

    getOutgoingEdgesByNodeIdStatement.setString(1, id);

    ResultSet resultSet = getOutgoingEdgesByNodeIdStatement.executeQuery();

    List<Edge> edges = new ArrayList<>();

    while (resultSet.next()) {

        String edgeId = resultSet.getString("id");

        String fromId = resultSet.getString("from_id");

        String toId = resultSet.getString("to_id");

        Node from = new Node(fromId);

        Node to = new Node(toId);

        Edge edge = new Edge(edgeId, from, to);

        edges.add(edge);

    }

    return edges;

}
