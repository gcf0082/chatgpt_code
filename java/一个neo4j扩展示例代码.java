package com.example.neo4jextension;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.api.procedure.GlobalProcedures;
import org.neo4j.logging.Log;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Mode;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.Procedure;

import java.util.stream.Stream;

public class ShortestPathExtension {

    // 定义存储过程的名称和输入参数
    @Procedure(name = "com.example.shortestPath", mode = Mode.READ)
    public Stream<ShortestPathResult> shortestPath(
            @Name("startNodeId") long startNodeId,
            @Name("endNodeId") long endNodeId) {
        try (Transaction tx = db.beginTx()) {
            Node startNode = db.getNodeById(startNodeId);
            Node endNode = db.getNodeById(endNodeId);
            Path shortestPath = db.traversalDescription()
                    .breadthFirst()
                    .evaluator(org.neo4j.graphdb.traversal.Evaluators.toDepth(1))
                    .traverse(startNode, endNode)
                    .single();
            tx.success();
            return Stream.of(new ShortestPathResult(shortestPath.length()));
        }
    }

    // 定义一个返回结果的类
    public static class ShortestPathResult {
        public int length;
        public ShortestPathResult(int length) {
            this.length = length;
        }
    }

    // 注入 Neo4j 的上下文对象和日志对象
    @Context
    public GraphDatabaseService db;

    @Context
    public Log log;

    @Context
    public GlobalProcedures globalProcedures;
}
