import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.factory.GraphDatabaseSettings.BoltConnector;
import org.neo4j.graphdb.factory.GraphDatabaseSettings.Builder;
import org.neo4j.graphdb.schema.ConstraintDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.helpers.collection.Iterables;
import org.neo4j.procedure.Procedure;
import org.neo4j.procedure.Procedure.Mode;
import org.neo4j.procedure.ProcedureContext;
import org.neo4j.procedure.ProcedureResult;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.util.ArrayList;

import apoc.load.LoadCsv;
import apoc.result.MapResult;

public class Neo4jQuery {

    private static final String NEO4J_PATH = "path/to/neo4j/database";

    public static void main(String[] args) {
        GraphDatabaseService graphDb = new GraphDatabaseFactory()
            .newEmbeddedDatabaseBuilder(new File(NEO4J_PATH))
            .setConfig(GraphDatabaseSettings.procedure_unrestricted, "apoc.*")
            .newGraphDatabase();
        registerShutdownHook(graphDb);

        try (Transaction tx = graphDb.beginTx()) {
            Schema schema = graphDb.schema();
            if (!schema.getConstraints(Labels.Person).iterator().hasNext()) {
                schema.constraintFor(Labels.Person)
                      .assertPropertyIsUnique("id")
                      .create();
            }
            tx.success();
        }

        try (Transaction tx = graphDb.beginTx()) {
            // 使用 APOC 存储过程来读取 CSV 文件
            String query = "CALL apoc.load.csv('file:///path/to/csv', {header:true}) YIELD map RETURN map";
            List<Map<String, Object>> results = new ArrayList<>();
            Result result = graphDb.execute(query);
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                results.add(row);
            }

            // 处理查询结果
            for (Map<String, Object> row : results) {
                String id = (String) row.get("id");
                String name = (String) row.get("name");
                int age = (int) row.get("age");
                Node node = getOrCreatePersonNode(graphDb, id);
                node.setProperty("name", name);
                node.setProperty("age", age);
            }
            tx.success();
        }

        try (Transaction tx = graphDb.beginTx()) {
            // 查询 Person 节点的信息
            String query = "MATCH (p:Person) RETURN p.id AS id, p.name AS name, p.age AS age";
            Result result = graphDb.execute(query);
            while (result.hasNext()) {
                Map<String, Object> row = result.next();
                String id = (String) row.get("id");
                String name = (String) row.get("name");
                int age = (int) row.get("age");
                System.out.printf("Person(id=%s, name=%s, age=%d)\n", id, name, age);
            }
            tx.success();
        }

        graphDb.shutdown();
   
}  //--!!这个一行chatgpt断了，手工补的，不确定是否正确

@Procedure(value = "example.updatePerson", mode = Mode.WRITE)
public void updatePerson(@Name("id") String id, @Name("name") String name, @Name("age") int age) {
    try (Transaction tx = db.beginTx()) {
        Node node = getOrCreatePersonNode(db, id);
        node.setProperty("name", name);
        node.setProperty("age", age);
        tx.success();
    }
}

@UserFunction(value = "example.getPerson")
public Map<String, Object> getPerson(@Name("id") String id) {
    try (Transaction tx = db.beginTx()) {
        Node node = getPersonNode(db, id);
        if (node != null) {
            Map<String, Object> result = new HashMap<>();
            result.put("id", id);
            result.put("name", node.getProperty("name"));
            result.put("age", node.getProperty("age"));
            return result;
        }
    }
    return null;
}

@Procedure(value = "example.deletePerson", mode = Mode.WRITE)
public void deletePerson(@Name("id") String id) {
    try (Transaction tx = db.beginTx()) {
        Node node = getPersonNode(db, id);
        if (node != null) {
            node.delete();
        }
        tx.success();
    }
}

@Procedure(value = "example.listPersons")
public Stream<MapResult> listPersons() {
    try (Transaction tx = db.beginTx()) {
        String query = "MATCH (p:Person) RETURN p.id AS id, p.name AS name, p.age AS age";
        Result result = db.execute(query);
        return Iterables.stream(result)
                        .map(row -> MapResult.of("id", row.get("id"), "name", row.get("name"), "age", row.get("age")));
    }
}

private static Node getOrCreatePersonNode(GraphDatabaseService graphDb, String id) {
    Node node = getPersonNode(graphDb, id);
    if (node == null) {
        node = graphDb.createNode(Labels.Person);
        node.setProperty("id", id);
    }
    return node;
}

private static Node getPersonNode(GraphDatabaseService graphDb, String id) {
    String query = "MATCH (p:Person {id: $id}) RETURN p";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    Result result = graphDb.execute(query, params);
    if (result.hasNext()) {
        Node node = (Node) result.next().get("p");
        return node;
    } else {
        return null;
    }
}

private static void registerShutdownHook(final GraphDatabaseService graphDb) {
    Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
            graphDb.shutdown();
        }
    });
}

private static enum Labels implements Label {
    Person
}
