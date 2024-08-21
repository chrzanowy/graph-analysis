package com.chrzanowy.graphanalysis.rest.controller;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.model.GraphAnalysisResponse;
import com.chrzanowy.graphanalysis.config.TimeProvider;
import com.chrzanowy.graphanalysis.db.AlgorithmRepository;
import com.chrzanowy.graphanalysis.db.AnalysisRepository;
import com.chrzanowy.graphanalysis.db.DatasetRepository;
import com.chrzanowy.graphanalysis.db.EdgeRepository;
import com.chrzanowy.graphanalysis.db.NodeRepository;
import com.chrzanowy.graphanalysis.db.model.AlgorithmStatus;
import com.chrzanowy.graphanalysis.db.model.Analysis;
import com.chrzanowy.graphanalysis.db.model.Dataset;
import com.chrzanowy.graphanalysis.db.model.Edge;
import com.chrzanowy.graphanalysis.db.model.Node;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;

class GraphAnalysisControllerTest extends RestIntegrationTest {

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private NodeRepository nodeRepository;

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private AlgorithmRepository algorithmRepository;

    private static final String API_V1_ANALYSIS = "api/v1/analysis";

    @AfterEach
    void cleanUp() {
        algorithmRepository.deleteAll();
        datasetRepository.deleteAll();
        nodeRepository.deleteAll();
        edgeRepository.deleteAll();
        analysisRepository.deleteAll();
    }

    @Test
    void shouldAnalyzeDataset() {
        //given
        var datasetId = prepareDataset();
        //when
        Map<Algorithm, GraphAnalysisResponse> response = givenRequest()
            .post(getBaseUrl() + API_V1_ANALYSIS + "/dataset/" + datasetId)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<Map<Algorithm, GraphAnalysisResponse>>() {
            });
        //then
        Assertions.assertThat(response)
            .containsKeys(Algorithm.A1, Algorithm.A2);
        Assertions.assertThat(response.get(Algorithm.A1))
            .satisfies(graphAnalysisResponse -> {
                Assertions.assertThat(graphAnalysisResponse.id()).isNotNull();
                Assertions.assertThat(graphAnalysisResponse.algorithm()).isEqualTo(Algorithm.A1);
                Assertions.assertThat(graphAnalysisResponse.datasetId()).isEqualTo(datasetId);
                Assertions.assertThat(graphAnalysisResponse.analysisData()).containsEntry("numberOfDisconnectedGraphs", 2);
            });
        Assertions.assertThat(response.get(Algorithm.A2))
            .satisfies(graphAnalysisResponse -> {
                Assertions.assertThat(graphAnalysisResponse.id()).isNotNull();
                Assertions.assertThat(graphAnalysisResponse.algorithm()).isEqualTo(Algorithm.A2);
                Assertions.assertThat(graphAnalysisResponse.datasetId()).isEqualTo(datasetId);
                Assertions.assertThat(graphAnalysisResponse.analysisData())
                    .containsExactlyEntriesOf(Map.of("vertexIdsWithDegreeOfOne", List.of(4, 5)));
            });
    }

    @ParameterizedTest
    @EnumSource(value = Algorithm.class, names = {"A1", "A2"})
    void shouldAnalyzeDatasetUsingAlgorithm(Algorithm algorithm) {
        //given
        var datasetId = prepareDataset();
        //when
        GraphAnalysisResponse response = givenRequest()
            .post(getBaseUrl() + API_V1_ANALYSIS + "/dataset/" + datasetId + "/" + algorithm.name())
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<GraphAnalysisResponse>() {
            });
        //then
        Assertions.assertThat(response)
            .satisfies(graphAnalysisResponse -> {
                Assertions.assertThat(graphAnalysisResponse.id()).isNotNull();
                Assertions.assertThat(graphAnalysisResponse.algorithm()).isEqualTo(algorithm);
                Assertions.assertThat(graphAnalysisResponse.datasetId()).isEqualTo(datasetId);
                if (algorithm == Algorithm.A1) {
                    Assertions.assertThat(graphAnalysisResponse.analysisData()).containsExactlyEntriesOf(Map.of("numberOfDisconnectedGraphs", 2));
                } else {
                    Assertions.assertThat(graphAnalysisResponse.analysisData())
                        .containsExactlyEntriesOf(Map.of("vertexIdsWithDegreeOfOne", List.of(4, 5)));
                }
            });
    }

    @Test
    void shouldReturn400IfDatasetNotFound() {
        //given
        algorithmRepository.saveAll(List.of(new AlgorithmStatus(Algorithm.A1.name(), true),
            new AlgorithmStatus(Algorithm.A2.name(), true)));
        var datasetId = Long.MAX_VALUE;
        //when
        //then
        givenRequest()
            .post(getBaseUrl() + API_V1_ANALYSIS + "/dataset/" + datasetId)
            .then()
            .statusCode(400);
    }

    @Test
    void shouldReturnAnalyseResult() {
        //given
        var startTime = TimeProvider.getCurrentTime();
        var analysis = new Analysis(1L, Algorithm.A1, Map.of("test", "value"), startTime, startTime.plusSeconds(1));
        analysisRepository.save(analysis);
        //when
        GraphAnalysisResponse singleAnalysis = givenRequest()
            .get(getBaseUrl() + API_V1_ANALYSIS + "/" + analysis.getId())
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<GraphAnalysisResponse>() {
            });
        //then
        Assertions.assertThat(singleAnalysis)
            .satisfies(graphAnalysisResponse -> {
                Assertions.assertThat(graphAnalysisResponse.algorithm()).isEqualTo(Algorithm.A1);
                Assertions.assertThat(graphAnalysisResponse.datasetId()).isEqualTo(1L);
                Assertions.assertThat(graphAnalysisResponse.analysisData()).containsEntry("test", "value");
            });
        //when
        List<GraphAnalysisResponse> allAnalysis = givenRequest()
            .get(getBaseUrl() + API_V1_ANALYSIS)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<GraphAnalysisResponse>>() {
            });
        //then
        Assertions.assertThat(allAnalysis)
            .hasSize(1)
            .first()
            .satisfies(graphAnalysisResponse -> {
                Assertions.assertThat(graphAnalysisResponse.algorithm()).isEqualTo(Algorithm.A1);
                Assertions.assertThat(graphAnalysisResponse.datasetId()).isEqualTo(1L);
                Assertions.assertThat(graphAnalysisResponse.analysisData()).containsEntry("test", "value");
            });
    }

    private Long prepareDataset() {
        Dataset dataset = datasetRepository.save(new Dataset("1"));
        algorithmRepository.saveAll(List.of(new AlgorithmStatus(Algorithm.A1.name(), true),
            new AlgorithmStatus(Algorithm.A2.name(), true)));
        nodeRepository.saveAll(List.of(new Node(1L, dataset.getId()),
            new Node(2L, dataset.getId()),
            new Node(3L, dataset.getId()),
            new Node(4L, dataset.getId()),
            new Node(5L, dataset.getId())));
        edgeRepository.saveAll(List.of(new Edge(1L, 2L, dataset.getId()),
            new Edge(2L, 3L, dataset.getId()),
            new Edge(3L, 1L, dataset.getId()),
            new Edge(4L, 5L, dataset.getId())));
        return dataset.getId();
    }
}