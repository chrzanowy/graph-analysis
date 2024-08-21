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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GraphAnalysisService {

    private final DatasetRepository datasetRepository;

    private final AnalysisRepository analysisRepository;

    private final List<GraphAnalysisAlgorithmHandler> graphAnalysisAlgorithmHandlers;

    private final DatasetProviderService datasetProviderService;

    private final Map<Algorithm, ConcurrentHashMap<LocalDateTime, GraphAnalysisResponse>> analysisResultCache = new ConcurrentHashMap<>();

    @Transactional
    public Map<Algorithm, GraphAnalysisResponse> analyzeDataset(Long dataSetId, boolean force) {
        return graphAnalysisAlgorithmHandlers.stream()
            .filter(GraphAnalysisAlgorithmHandler::isActive)
            .map(handler -> analyzeDatasetUsingAlgorithm(dataSetId, handler.getSupportedAlgorithm(), force))
            .collect(Collectors.toMap(GraphAnalysisResponse::algorithm, Function.identity()));
    }

    @Transactional
    public GraphAnalysisResponse analyzeDatasetUsingAlgorithm(Long dataSetId, Algorithm algorithm, boolean force) {
        Dataset workingDataset = datasetRepository.findById(dataSetId)
            .orElseThrow(() -> new DatasetNotFound(dataSetId));
        GraphAnalysisAlgorithmHandler foundHandler = graphAnalysisAlgorithmHandlers.stream()
            .filter(handler -> handler.getSupportedAlgorithm().equals(algorithm))
            .filter(GraphAnalysisAlgorithmHandler::isActive)
            .findFirst()
            .orElseThrow(() -> new AlgorithmHandlerNotFound(algorithm.name()));

        if (force || !analysisResultCache.containsKey(algorithm)
            || !analysisResultCache.get(algorithm).containsKey(workingDataset.getLastUpdated())) {
            Analysis analysis = runAnalysis(foundHandler, algorithm, dataSetId);
            GraphAnalysisResponse response = GraphAnalysisResponse.from(analysis);
            analysisResultCache.compute(algorithm, (key, valueMap) -> {
                if (valueMap == null) {
                    valueMap = new ConcurrentHashMap<>();
                }
                valueMap.put(workingDataset.getLastUpdated(), response);
                return valueMap;
            });
        }

        return analysisResultCache
            .get(algorithm)
            .get(workingDataset.getLastUpdated());

    }

    public Analysis runAnalysis(GraphAnalysisAlgorithmHandler foundHandler, Algorithm algorithm, Long dataSetId) {
        Stream<GraphEdge> graphEdgesFromDataset = datasetProviderService.getGraphEdgesFromDataset(dataSetId);
        LocalDateTime executionTime = TimeProvider.getCurrentTime();
        GraphAnalysisResult result = foundHandler.analyze(graphEdgesFromDataset);
        LocalDateTime completionTime = TimeProvider.getCurrentTime();
        return analysisRepository.save(new Analysis(dataSetId, algorithm, result.analysisData(), executionTime, completionTime));
    }

    public Optional<GraphAnalysisResponse> getAnalysisResult(UUID analysisId) {
        return analysisRepository.findById(analysisId)
            .map(GraphAnalysisResponse::from);
    }

    public List<GraphAnalysisResponse> getAllAnalysisResults() {
        return StreamSupport.stream(analysisRepository.findAll().spliterator(), false)
            .map(GraphAnalysisResponse::from)
            .collect(Collectors.toList());
    }
}
