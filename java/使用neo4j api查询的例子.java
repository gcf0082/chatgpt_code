// 导入 Neo4j Java API 所需的库
import org.neo4j.driver.*;

public class Neo4jQuery {
    public static void main(String[] args) {

        // 建立数据库连接
        Driver driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "password"));
        Session session = driver.session();

        // 运行 Cypher 查询
        Result result = session.run("MATCH (p:Person)-[:FRIEND]->(f:Person) RETURN p.name, f.name");

        // 处理查询结果
        while (result.hasNext()) {
            Record record = result.next();
            String personName = record.get("p.name").asString();
            String friendName = record.get("f.name").asString();
            System.out.println(personName + " is friends with " + friendName);
        }

        // 关闭数据库连接
        session.close();
        driver.close();
    }
}
