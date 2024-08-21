package com.chrzanowy.graphanalysis.algorithm;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import com.chrzanowy.graphanalysis.analysis.model.GraphAnalysisResult;
import com.chrzanowy.graphanalysis.config.TimeProvider;
import com.chrzanowy.graphanalysis.rest.model.AlgorithmStatusResponse;
import java.util.Map;
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
class A1GraphAnalysisAlgorithmHandlerTest {

    @Mock
    private TimeProvider timeProvider;

    @Mock
    private AlgorighmService algorighmService;

    @InjectMocks
    private A1GraphAnalysisAlgorithmHandler a1GraphAnalysisAlgorithmHandler;

    public static Stream<Arguments> graphsEdgesWithDegrees() {
        return Stream.of(
            Arguments.of(
                Stream.of(new GraphEdge(1L, 2L),
                    new GraphEdge(2L, 3L),
                    new GraphEdge(3L, 1L),
                    new GraphEdge(3L, 4L)),
                1
            ),
            Arguments.of(
                Stream.of(new GraphEdge(10L, 22L),
                    new GraphEdge(22L, 11L),
                    new GraphEdge(1L, 26L)),
                2
            ),
            Arguments.of(
                Stream.of(new GraphEdge(31L, 30L),
                    new GraphEdge(2L, 3L),
                    new GraphEdge(3L, 1L),
                    new GraphEdge(3L, 4L)),
                2
            )
        );
    }

    @ParameterizedTest
    @MethodSource("graphsEdgesWithDegrees")
    void shouldAnalyzeDataset(Stream<GraphEdge> edges, Integer disconnectedGraphs) {
        //given
        var usedAlgorithm = Algorithm.A1;

        ///when
        GraphAnalysisResult result = a1GraphAnalysisAlgorithmHandler.analyze(edges);

        //then
        Assertions.assertThat(result.algorithm()).isEqualTo(usedAlgorithm);
        Assertions.assertThat(result.analysisData()).containsExactlyEntriesOf(Map.of("numberOfDisconnectedGraphs", disconnectedGraphs));
    }

    @Test
    void shouldNotAnalyzeDatasetIfAlgorithmIsDisabled() {
        //given
        var usedAlgorithm = Algorithm.A1;
        Mockito.when(algorighmService.getAlgorithmStatus(usedAlgorithm.name()))
            .thenReturn(Optional.of(new AlgorithmStatusResponse(Algorithm.A2.name(), false)));

        ///when
        //then
        Assertions.assertThat(a1GraphAnalysisAlgorithmHandler.isActive()).isFalse();
    }

    @Test
    void shouldNotAnalyzeDatasetIfAlgorithmIsDifferent() {
        //given
        var usedAlgorithm = Algorithm.A1;

        ///when
        //then
        Assertions.assertThat(a1GraphAnalysisAlgorithmHandler.isActive()).isFalse();
    }
}