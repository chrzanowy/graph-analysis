package com.chrzanowy.graphanalysis.algorithm;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import com.chrzanowy.graphanalysis.analysis.model.GraphAnalysisResult;
import java.util.stream.Stream;

public interface GraphAnalysisAlgorithmHandler {

    Algorithm getSupportedAlgorithm();

    boolean isActive();

    GraphAnalysisResult analyze(Stream<GraphEdge> graphEdges);
}
