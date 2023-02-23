import java.sql.SQLException;

import java.util.HashMap;

import java.util.Map;

public class SimpleGraphDatabaseExample {

    public static void main(String[] args) {

        try {

            SimpleGraphDatabase db = new SimpleGraphDatabase("mydatabase.db");

            db.createTables();

            // add two nodes

            Map<String, Object> props1 = new HashMap<>();

            props1.put("color", "red");

            db.addNode("A", props1);

            Map<String, Object> props2 = new HashMap<>();

            props2.put("color", "blue");

            db.addNode("B", props2);

            // add an edge between the two nodes

            Map<String, Object> edgeProps = new HashMap<>();

            edgeProps.put("weight", 1);

            db.addEdge(1, 2, "CONNECTS", edgeProps);

            // delete the second node

            db.deleteNode(2);

            // close the database

            db.close();

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

}
