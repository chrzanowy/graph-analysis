package com.chrzanowy.graphanalysis.algorithm;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import com.chrzanowy.graphanalysis.analysis.model.GraphAnalysisResult;
import com.chrzanowy.graphanalysis.rest.model.AlgorithmStatusResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class A2GraphAnalysisAlgorithmHandlerTest {

    @Mock
    private AlgorighmService algorighmService;

    @InjectMocks
    private A2GraphAnalysisAlgorithmHandler a2GraphAnalysisAlgorithmHandler;

    public static Stream<Arguments> graphsEdgesWithDegrees() {
        return Stream.of(
            Arguments.of(
                Stream.of(new GraphEdge(1L, 2L),
                    new GraphEdge(2L, 3L),
                    new GraphEdge(3L, 1L),
                    new GraphEdge(3L, 4L)),
                List.of(4L)
            ),
            Arguments.of(
                Stream.of(new GraphEdge(29L, 24L),
                    new GraphEdge(0L, 24L),
                    new GraphEdge(24L, 1L),
                    new GraphEdge(3L, 0L),
                    new GraphEdge(29L, 0L),
                    new GraphEdge(5L, 1L)),
                List.of(5L, 3L)
            ), Arguments.of(
                Stream.of(new GraphEdge(12L, 15L),
                    new GraphEdge(15L, 16L),
                    new GraphEdge(21L, 17L),
                    new GraphEdge(16L, 38L),
                    new GraphEdge(33L, 32L),
                    new GraphEdge(38L, 12L)),
                List.of(32L, 33L, 17L, 21L)
            )
        );
    }

    @ParameterizedTest
    @MethodSource("graphsEdgesWithDegrees")
    void shouldAnalyzeDataset(Stream<GraphEdge> edges, List<Long> vertex) {
        //given
        var usedAlgorithm = Algorithm.A2;

        ///when
        Mockito.when(algorighmService.getAlgorithmStatus(usedAlgorithm.name()))
            .thenReturn(Optional.of(new AlgorithmStatusResponse(Algorithm.A2.name(), true)));
        GraphAnalysisResult result = a2GraphAnalysisAlgorithmHandler.analyze(edges);

        //then
        Assertions.assertThat(a2GraphAnalysisAlgorithmHandler.isActive()).isTrue();
        Assertions.assertThat(result.algorithm()).isEqualTo(usedAlgorithm);
        Assertions.assertThat(result.analysisData()).containsKey("vertexIdsWithDegreeOfOne");
        Assertions.assertThat(result.analysisData().get("vertexIdsWithDegreeOfOne"))
            .asList()
            .containsExactlyInAnyOrderElementsOf(vertex);

    }

    @Test
    void shouldNotAnalyzeDatasetIfAlgorithmIsDisabled() {
        //given
        var usedAlgorithm = Algorithm.A2;
        Mockito.when(algorighmService.getAlgorithmStatus(usedAlgorithm.name()))
            .thenReturn(Optional.of(new AlgorithmStatusResponse(Algorithm.A2.name(), false)));

        ///when
        //then
        Assertions.assertThat(a2GraphAnalysisAlgorithmHandler.isActive()).isFalse();
    }

    @Test
    void shouldNotAnalyzeDatasetIfAlgorithmIsDifferent() {
        //given
        var usedAlgorithm = Algorithm.A1;

        ///when
        //then
        Assertions.assertThat(a2GraphAnalysisAlgorithmHandler.getSupportedAlgorithm()).isNotEqualTo(usedAlgorithm);
    }

}