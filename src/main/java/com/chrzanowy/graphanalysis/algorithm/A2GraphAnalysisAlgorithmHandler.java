package com.chrzanowy.graphanalysis.algorithm;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import com.chrzanowy.graphanalysis.analysis.model.GraphAnalysisResult;
import com.chrzanowy.graphanalysis.rest.model.AlgorithmStatusResponse;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class A2GraphAnalysisAlgorithmHandler implements GraphAnalysisAlgorithmHandler {

    private final static Algorithm SUPPORTED_ALGORITHM = Algorithm.A2;

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
        Map<Long, Long> nodeDegrees = graphEdges
            .flatMap(edge -> Stream.of(edge.sourceId(), edge.targetId()))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        List<Long> listOfNodesWithSingleDegree = nodeDegrees.entrySet().stream()
            .filter(entry -> entry.getValue() == 1)
            .map(Entry::getKey)
            .toList();
        return new GraphAnalysisResult(SUPPORTED_ALGORITHM, Map.of("vertexIdsWithDegreeOfOne", listOfNodesWithSingleDegree));
    }
}
