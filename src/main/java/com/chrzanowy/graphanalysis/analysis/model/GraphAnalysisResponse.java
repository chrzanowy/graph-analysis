package com.chrzanowy.graphanalysis.analysis.model;

import com.chrzanowy.graphanalysis.db.model.Analysis;
import com.chrzanowy.graphanalysis.analysis.Algorithm;
import java.util.Map;
import java.util.UUID;

public record GraphAnalysisResponse(Algorithm algorithm, UUID id, Long datasetId, Map<String, Object> analysisData) {

    public static GraphAnalysisResponse from(Analysis savedAnalysis) {
        return new GraphAnalysisResponse(savedAnalysis.getAlgorithmName(), savedAnalysis.getId(),
            savedAnalysis.getDatasetId(), savedAnalysis.getAnalysisData());
    }
}
