package com.chrzanowy.graphanalysis.rest.controller;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.model.AlgorithmExecutionMetricsResponse;
import com.chrzanowy.graphanalysis.db.AnalysisRepository;
import com.chrzanowy.graphanalysis.db.model.Analysis;
import io.restassured.common.mapper.TypeRef;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class MetricsControllerTest extends RestIntegrationTest {

    private static final String API_V1_ALGORITHM = "api/v1/metrics";

    @Autowired
    private AnalysisRepository analysisRepository;

    @AfterEach
    void cleanUp() {
        analysisRepository.deleteAll();
    }

    @Test
    void shouldGetResultForAnalysis() {
        //given
        ;
        var newAnalysis = new Analysis(1L, Algorithm.A1, Map.of("test", "value"), LocalDateTime.now(ZoneOffset.UTC).minusSeconds(1),
            LocalDateTime.now(ZoneOffset.UTC));
        analysisRepository.save(newAnalysis);

        //when
        AlgorithmExecutionMetricsResponse response = givenRequest()
            .get(getBaseUrl() + API_V1_ALGORITHM + "/A1")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<>() {
            });

        //then
        Assertions.assertThat(newAnalysis)
            .satisfies(analysis -> {
                Assertions.assertThat(response.algorithm()).isEqualTo(Algorithm.A1);
                Assertions.assertThat(response.averageExecutionTimeInMs()).isEqualByComparingTo(BigDecimal.valueOf(1000));
                Assertions.assertThat(response.executionCount()).isEqualTo(1);
            });
    }

}