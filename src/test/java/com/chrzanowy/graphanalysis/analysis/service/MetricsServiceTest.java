package com.chrzanowy.graphanalysis.analysis.service;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.model.AlgorithmExecutionMetricsResponse;
import com.chrzanowy.graphanalysis.db.AnalysisRepository;
import com.chrzanowy.graphanalysis.db.model.Analysis;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {

    @Mock
    private AnalysisRepository analysisRepository;

    @InjectMocks
    private MetricsService metricsService;


    @Test
    void shouldGetAnalysisResultForAlgorithm() {
        // given
        var algorithm = Algorithm.A1;
        var testTime = LocalDateTime.now();
        Mockito.when(analysisRepository.findAllByAlgorithmName(algorithm)).thenReturn(List.of(
            new Analysis(1L, algorithm, Map.of(), testTime.minusSeconds(1), testTime),
            new Analysis(1L, algorithm, Map.of(), testTime.minusSeconds(2), testTime),
            new Analysis(1L, algorithm, Map.of(), testTime.minusSeconds(3), testTime)
        ));
        // when
        AlgorithmExecutionMetricsResponse analysisResultForAlgorithm = metricsService.getAlgorithmExecutionMetrics(algorithm);
        // then
        Assertions.assertThat(analysisResultForAlgorithm.algorithm()).isEqualTo(algorithm);
        Assertions.assertThat(analysisResultForAlgorithm.averageExecutionTimeInMs()).isEqualByComparingTo(BigDecimal.valueOf(2000));
        Assertions.assertThat(analysisResultForAlgorithm.executionCount()).isEqualTo(3);
    }

}