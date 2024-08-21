package com.chrzanowy.graphanalysis.analysis.model;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import java.util.Map;

public record GraphAnalysisResult(Algorithm algorithm, Map<String, Object> analysisData) {

}
