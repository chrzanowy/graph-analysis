package com.chrzanowy.graphanalysis.analysis.model;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import java.math.BigDecimal;

public record AlgorithmExecutionMetricsResponse(Algorithm algorithm, BigDecimal averageExecutionTimeInMs, long executionCount) {

}
