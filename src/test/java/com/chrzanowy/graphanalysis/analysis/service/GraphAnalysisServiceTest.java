package com.chrzanowy.graphanalysis.analysis.service;

import com.chrzanowy.graphanalysis.algorithm.GraphAnalysisAlgorithmHandler;
import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import com.chrzanowy.graphanalysis.analysis.model.GraphAnalysisResponse;
import com.chrzanowy.graphanalysis.analysis.model.GraphAnalysisResult;
import com.chrzanowy.graphanalysis.config.TimeProvider;
import com.chrzanowy.graphanalysis.db.AnalysisRepository;
import com.chrzanowy.graphanalysis.db.DatasetRepository;
import com.chrzanowy.graphanalysis.db.model.Analysis;
import com.chrzanowy.graphanalysis.db.model.Dataset;
import com.chrzanowy.graphanalysis.exception.AlgorithmHandlerNotFound;
import com.chrzanowy.graphanalysis.exception.DatasetNotFound;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;

class GraphAnalysisServiceTest {

    private AnalysisRepository analysisRepository = Mockito.mock(AnalysisRepository.class);

    private GraphAnalysisAlgorithmHandler a1GraphAnalysisHandler = Mockito.mock(GraphAnalysisAlgorithmHandler.class);

    private GraphAnalysisAlgorithmHandler a2GraphAnalysisHandler = Mockito.mock(GraphAnalysisAlgorithmHandler.class);

    private DatasetProviderService datasetProviderService = Mockito.mock(DatasetProviderService.class);

    private DatasetRepository datasetRepository = Mockito.mock(DatasetRepository.class);

    private GraphAnalysisService graphAnalysisService;

    @BeforeEach
    void setUp() {
        Mockito.reset(analysisRepository, a1GraphAnalysisHandler, a2GraphAnalysisHandler, datasetProviderService);
        Mockito.when(a1GraphAnalysisHandler.getSupportedAlgorithm()).thenReturn(Algorithm.A1);
        Mockito.when(a2GraphAnalysisHandler.getSupportedAlgorithm()).thenReturn(Algorithm.A2);
        Mockito.when(a1GraphAnalysisHandler.isActive()).thenReturn(true);
        Mockito.when(a2GraphAnalysisHandler.isActive()).thenReturn(true);
    }

    public GraphAnalysisServiceTest() {
        this.graphAnalysisService = new GraphAnalysisService(datasetRepository, analysisRepository, List.of(a1GraphAnalysisHandler, a2GraphAnalysisHandler),
            datasetProviderService);
    }

    @Test
    void shouldThrowExceptionIfAlgorithmIsNotHandler() {
        //given
        var usedAlgorithm = Algorithm.A3;
        var datasetId = 1L;
        var graphEdgeStream = Stream.of(new GraphEdge(1L, 2L),
            new GraphEdge(2L, 3L),
            new GraphEdge(3L, 1L));
        Mockito.when(datasetRepository.findById(datasetId)).thenReturn(Optional.of(new Dataset("test")));
        Mockito.when(datasetProviderService.getGraphEdgesFromDataset(datasetId)).thenReturn(graphEdgeStream);
        Mockito.when(a1GraphAnalysisHandler.analyze(graphEdgeStream)).thenReturn(new GraphAnalysisResult(usedAlgorithm, Map.of()));
        Mockito.when(analysisRepository.save(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());

        //when
        //then
        Assertions.assertThatThrownBy(() -> graphAnalysisService.analyzeDatasetUsingAlgorithm(datasetId, usedAlgorithm, true))
            .isInstanceOf(AlgorithmHandlerNotFound.class);
    }

    @Test
    void shouldAnalyzeDatasetUsingAlgorithm() {
        //given
        var usedAlgorithm = Algorithm.A1;
        var datasetId = 1L;
        var graphEdgeStream = Stream.of(new GraphEdge(1L, 2L),
            new GraphEdge(2L, 3L),
            new GraphEdge(3L, 1L));
        Mockito.when(datasetRepository.findById(datasetId)).thenReturn(Optional.of(new Dataset("test")));
        Mockito.when(datasetProviderService.getGraphEdgesFromDataset(datasetId)).thenReturn(graphEdgeStream);
        Mockito.when(a1GraphAnalysisHandler.analyze(graphEdgeStream)).thenReturn(new GraphAnalysisResult(usedAlgorithm, Map.of()));
        Mockito.when(analysisRepository.save(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());

        //when
        GraphAnalysisResponse graphAnalysisResult = graphAnalysisService.analyzeDatasetUsingAlgorithm(datasetId, usedAlgorithm, true);
        //then
        Assertions.assertThat(graphAnalysisResult.algorithm()).isEqualTo(usedAlgorithm);
        Assertions.assertThat(graphAnalysisResult.analysisData()).isEmpty();
    }

    @Test
    void shouldThrowExceptionIfDatasetNotFound() {
        //given
        var usedAlgorithm = Algorithm.A1;
        var datasetId = 1L;
        Mockito.when(datasetRepository.findById(datasetId)).thenReturn(Optional.empty());

        //when
        //then
        Assertions.assertThatThrownBy(() -> graphAnalysisService.analyzeDatasetUsingAlgorithm(datasetId, usedAlgorithm, true))
            .isInstanceOf(DatasetNotFound.class);
    }

    @Test
    void shouldGetCachedResultIfForceIsFalse() {
        //given
        var usedAlgorithm = Algorithm.A1;
        var datasetId = 1L;
        var graphEdgeStream = Stream.of(new GraphEdge(1L, 2L),
            new GraphEdge(2L, 3L),
            new GraphEdge(3L, 1L));
        Mockito.when(datasetRepository.findById(datasetId)).thenReturn(Optional.of(new Dataset("test")));
        Mockito.when(datasetProviderService.getGraphEdgesFromDataset(datasetId)).thenReturn(graphEdgeStream);
        Mockito.when(a1GraphAnalysisHandler.analyze(graphEdgeStream)).thenReturn(new GraphAnalysisResult(usedAlgorithm, Map.of()));
        Mockito.when(analysisRepository.save(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());

        //when
        GraphAnalysisResponse graphAnalysisResult = graphAnalysisService.analyzeDatasetUsingAlgorithm(datasetId, usedAlgorithm, true);
        //then
        Mockito.verify(analysisRepository, Mockito.times(1)).save(Mockito.any());
        Assertions.assertThat(graphAnalysisResult.algorithm()).isEqualTo(usedAlgorithm);
        Assertions.assertThat(graphAnalysisResult.analysisData()).isEmpty();

        //when
        graphAnalysisService.analyzeDatasetUsingAlgorithm(datasetId, usedAlgorithm, false);
        Mockito.verifyNoMoreInteractions(analysisRepository);

    }

    @Test
    void shouldAnalyzeDatasetWithMultipleAlgorithms() {
        //given
        var datasetId = 1L;
        var graphEdgeStream = Stream.of(new GraphEdge(1L, 2L),
            new GraphEdge(2L, 3L),
            new GraphEdge(3L, 1L));
        Mockito.when(datasetRepository.findById(datasetId)).thenReturn(Optional.of(new Dataset("test")));
        Mockito.when(datasetProviderService.getGraphEdgesFromDataset(datasetId)).thenReturn(graphEdgeStream);
        Mockito.when(a1GraphAnalysisHandler.analyze(graphEdgeStream)).thenReturn(new GraphAnalysisResult(Algorithm.A1, Map.of()));
        Mockito.when(a2GraphAnalysisHandler.analyze(graphEdgeStream)).thenReturn(new GraphAnalysisResult(Algorithm.A2, Map.of()));
        Mockito.when(analysisRepository.save(Mockito.any())).then(AdditionalAnswers.returnsFirstArg());

        //when
        Map<Algorithm, GraphAnalysisResponse> graphAnalysisResult = graphAnalysisService.analyzeDataset(datasetId, true);
        //then
        Assertions.assertThat(graphAnalysisResult).containsKeys(Algorithm.A1, Algorithm.A2);
        Assertions.assertThat(graphAnalysisResult.get(Algorithm.A1)).satisfies(result -> {
            Assertions.assertThat(result.algorithm()).isEqualTo(Algorithm.A1);
            Assertions.assertThat(result.analysisData()).isEmpty();
        });
        Assertions.assertThat(graphAnalysisResult.get(Algorithm.A2)).satisfies(result -> {
            Assertions.assertThat(result.algorithm()).isEqualTo(Algorithm.A2);
            Assertions.assertThat(result.analysisData()).isEmpty();
        });
    }

    @Test
    void shouldGetAnalysisResult() {
        //given
        var analysisId = UUID.randomUUID();
        var usedAlgorithm = Algorithm.A1;
        var datasetId = 1L;
        var testStart = TimeProvider.getCurrentTime();
        Mockito.when(analysisRepository.findById(analysisId))
            .thenReturn(Optional.of(new Analysis(datasetId, usedAlgorithm, Map.of("arg", 1), testStart, testStart)));

        //when
        Optional<GraphAnalysisResponse> graphAnalysisResult = graphAnalysisService.getAnalysisResult(analysisId);
        //then
        Assertions.assertThat(graphAnalysisResult)
            .isPresent()
            .get()
            .satisfies(result -> {
                Assertions.assertThat(result.algorithm()).isEqualTo(usedAlgorithm);
                Assertions.assertThat(result.analysisData()).containsExactlyEntriesOf(Map.of("arg", 1));
            });
    }

    @Test
    void shouldGetAllAnalysisResults() {
        //given
        var usedAlgorithm = Algorithm.A1;
        var datasetId = 1L;
        var testStart = TimeProvider.getCurrentTime();
        Mockito.when(analysisRepository.findAll()).thenReturn(List.of(new Analysis(datasetId, usedAlgorithm, Map.of("arg", 1), testStart, testStart)));

        //when
        List<GraphAnalysisResponse> graphAnalysisResult = graphAnalysisService.getAllAnalysisResults();
        //then
        Assertions.assertThat(graphAnalysisResult)
            .hasSize(1)
            .first()
            .satisfies(result -> {
                Assertions.assertThat(result.algorithm()).isEqualTo(usedAlgorithm);
                Assertions.assertThat(result.analysisData()).containsExactlyEntriesOf(Map.of("arg", 1));
            });
    }
}