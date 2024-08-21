package com.chrzanowy.graphanalysis.algorithm;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import com.chrzanowy.graphanalysis.analysis.model.GraphAnalysisResult;
import com.chrzanowy.graphanalysis.rest.model.AlgorithmStatusResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class A1GraphAnalysisAlgorithmHandler implements GraphAnalysisAlgorithmHandler {

    private final static Algorithm SUPPORTED_ALGORITHM = Algorithm.A1;

    private final AlgorighmService algorighmService;

    @Override
    public Algorithm getSupportedAlgorithm() {
        return SUPPORTED_ALGORITHM;
    }

    @Override
    public boolean isActive() {
        return algorighmService.getAlgorithmStatus(getSupportedAlgorithm().name())
            .filter(AlgorithmStatusResponse::enabled)
            .isPresent();
    }

    @Override
    public GraphAnalysisResult analyze(Stream<GraphEdge> graphEdges) {
        Map<Integer, List<Integer>> list = unwrapEdges(graphEdges);
        int countDisconnectedGraphs = countDisconnectedGraphs(list);
        return new GraphAnalysisResult(SUPPORTED_ALGORITHM, Map.of("numberOfDisconnectedGraphs", countDisconnectedGraphs));
    }

    private static Map<Integer, List<Integer>> unwrapEdges(Stream<GraphEdge> graphEdges) {
        Map<Integer, List<Integer>> list = new HashMap<>();
        graphEdges.forEach(edge -> {
            list.compute(Math.toIntExact(edge.sourceId()), (k, v) -> {
                if (v == null) {
                    v = new ArrayList<>();
                }
                v.add(Math.toIntExact(edge.targetId()));
                return v;
            });
            list.compute(Math.toIntExact(edge.targetId()), (k, v) -> {
                if (v == null) {
                    v = new ArrayList<>();
                }
                v.add(Math.toIntExact(edge.sourceId()));
                return v;
            });
        });
        return list;
    }

    private static int countDisconnectedGraphs(Map<Integer, List<Integer>> mapOfNodesWithNeighbours) {
        Set<Integer> visitedNodes = new HashSet<>();
        List<List<Integer>> disconnectedGraphs = new ArrayList<>();
        mapOfNodesWithNeighbours.keySet()
            .forEach(node -> {
                if (!visitedNodes.contains(node)) {
                    List<Integer> currentComponent = new ArrayList<>();
                    List<Integer> disconnectedGraph = dfs(mapOfNodesWithNeighbours, visitedNodes, currentComponent, node);
                    disconnectedGraphs.add(disconnectedGraph);
                }
            });
        return disconnectedGraphs.size();
    }

    private static List<Integer> dfs(Map<Integer, List<Integer>> graph, Set<Integer> visited, List<Integer> currentGraph, int currentVertex) {
        visited.add(currentVertex);
        currentGraph.add(currentVertex);
        graph.getOrDefault(currentVertex, new ArrayList<>())
            .forEach(neighbor -> {
                if (!visited.contains(neighbor)) {
                    dfs(graph, visited, currentGraph, neighbor);
                }
            });
        return currentGraph;
    }
}
