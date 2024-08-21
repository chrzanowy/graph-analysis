package com.chrzanowy.graphanalysis.analysis.service;

import com.chrzanowy.graphanalysis.db.AnalysisRepository;
import com.chrzanowy.graphanalysis.db.model.Analysis;
import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.model.AlgorithmExecutionMetricsResponse;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.OptionalDouble;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsService {

    public final AnalysisRepository analysisRepository;

    public AlgorithmExecutionMetricsResponse getAlgorithmExecutionMetrics(Algorithm algorithm) {
        List<Analysis> allAnalysisForAlgorithm = analysisRepository.findAllByAlgorithmName(algorithm);

        OptionalDouble averageExecutionTimeAsDouble = allAnalysisForAlgorithm.stream()
            .map(analysis -> Duration.between(analysis.getExecutionTime(), analysis.getCompletionTime()).toMillis())
            .mapToLong(Long::longValue)
            .average();

        return new AlgorithmExecutionMetricsResponse(algorithm,
            averageExecutionTimeAsDouble.isPresent() ? BigDecimal.valueOf(averageExecutionTimeAsDouble.getAsDouble()) : BigDecimal.ZERO,
            allAnalysisForAlgorithm.size());
    }
}
