package com.chrzanowy.graphanalysis.rest.controller;

import com.chrzanowy.graphanalysis.analysis.service.GraphAnalysisService;
import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.model.GraphAnalysisResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Positive;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GraphAnalysisController {

    private final GraphAnalysisService graphAnalysisService;

    @PostMapping(value = "api/v1/analysis/dataset/{dataSetId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns the analysis results for all algorithms"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @Parameters(value = {
        @Parameter(name = "dataSetId", description = "The dataset id", required = true),
        @Parameter(name = "force", description = "Force the analysis")
    })
    public Map<Algorithm, GraphAnalysisResponse> analyzeDataset(@PathVariable @Positive Long dataSetId, @RequestParam(required = false) boolean force) {
        return graphAnalysisService.analyzeDataset(dataSetId, force);
    }

    @PostMapping(value = "api/v1/analysis/dataset/{dataSetId}/{algorithmName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns the analysis results for the specified algorithm"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @Parameters(value = {
        @Parameter(name = "dataSetId", description = "The dataset id", required = true),
        @Parameter(name = "algorithmName", description = "The algorithm name", required = true),
        @Parameter(name = "force", description = "Force the analysis")
    })
    public GraphAnalysisResponse analyzeDatasetUsingAlgorithm(@PathVariable @Positive Long dataSetId, @PathVariable Algorithm algorithmName,
        @RequestParam(required = false) boolean force) {
        return graphAnalysisService.analyzeDatasetUsingAlgorithm(dataSetId, algorithmName, force);
    }

    @GetMapping(value = "api/v1/analysis/{analysisId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns the analysis result for the specified analysis id"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @Parameters(value = {
        @Parameter(name = "analysisId", description = "The analysis id", required = true)
    })
    public Optional<GraphAnalysisResponse> analysisResult(@PathVariable UUID analysisId) {
        return graphAnalysisService.getAnalysisResult(analysisId);
    }

    @GetMapping(value = "api/v1/analysis", produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns all analysis results"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public List<GraphAnalysisResponse> getAllAnalysisResults() {
        return graphAnalysisService.getAllAnalysisResults();
    }
}
