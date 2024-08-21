package com.chrzanowy.graphanalysis.rest.controller;


import com.chrzanowy.graphanalysis.rest.model.AlgorithmStatusResponse;
import com.chrzanowy.graphanalysis.algorithm.AlgorighmService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlgorithmController {

    private final AlgorighmService algorighmService;

    @PatchMapping("/api/v1/algorithms/{algorithmName}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns the status of the algorithm")
    })
    @Parameters(value = {
        @Parameter(name = "algorithmName", description = "The algorithm name", required = true),
        @Parameter(name = "status", description = "The status of the algorithm", required = true)
    })
    public AlgorithmStatusResponse toggleAlgorithm(@PathVariable String algorithmName, @RequestParam boolean status) {
        return algorighmService.toggleAlgorithm(algorithmName, status);
    }

    @GetMapping("/api/v1/algorithms")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns the status of all algorithms")
    })
    public List<AlgorithmStatusResponse> getAlgorithmsStatus() {
        return algorighmService.getAlgorithmsStatus();
    }

}

