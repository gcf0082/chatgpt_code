import org.jgrapht.Graph;

import org.jgrapht.graph.DefaultEdge;

import org.jgrapht.graph.DirectedAcyclicGraph;

import java.util.ArrayList;

import java.util.LinkedList;

import java.util.List;

import java.util.Queue;

public class DirectedGraphCycleExample {

    public static void main(String[] args) {

        // 创建一个有向有环图

        Graph<String, DefaultEdge> graph = new DirectedAcyclicGraph<>(DefaultEdge.class);

        // 添加顶点

        graph.addVertex("A");

        graph.addVertex("B");

        graph.addVertex("C");

        graph.addVertex("D");

        // 添加边

        graph.addEdge("A", "B");

        graph.addEdge("B", "C");

        graph.addEdge("C", "D");

        graph.addEdge("D", "B");

        // 输出图的信息

        System.out.println("Graph: " + graph);

        // 获取顶点 B 向上的边

        List<DefaultEdge> edges = new ArrayList<>();

        getIncomingEdges(graph, "B", edges);

        // 输出顶点 B 向上的边

        System.out.println("Incoming edges of vertex B: " + edges);

    }

    // 广度优先递归获取某个节点向上的边

    private static void getIncomingEdges(Graph<String, DefaultEdge> graph, String vertex, List<DefaultEdge> edges) {

        // 创建一个队列，用于存储待处理的节点

        Queue<String> queue = new LinkedList<>();

        // 将初始节点加入队列

        queue.offer(vertex);

        // 广度优先搜索图

        while (!queue.isEmpty()) {

            String v = queue.poll();

            for (DefaultEdge e : graph.incomingEdgesOf(v)) {

                edges.add(e);

                String source = graph.getEdgeSource(e);

                queue.offer(source);

            }

        }

    }

}
