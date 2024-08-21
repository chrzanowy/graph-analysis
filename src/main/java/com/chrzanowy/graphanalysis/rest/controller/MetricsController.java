package com.chrzanowy.graphanalysis.rest.controller;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.model.AlgorithmExecutionMetricsResponse;
import com.chrzanowy.graphanalysis.analysis.service.MetricsService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping(value = "api/v1/metrics/{algorithmName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns the metrics result for the specified algorithm")
    })
    @Parameters(value = {
        @Parameter(name = "algorithmName", description = "The algorithm name", required = true)
    })
    public AlgorithmExecutionMetricsResponse getAlgorithmExecutionMetrics(@PathVariable Algorithm algorithmName) {
        return metricsService.getAlgorithmExecutionMetrics(algorithmName);
    }
}
