package com.chrzanowy.graphanalysis.analysis.service;

import com.chrzanowy.graphanalysis.db.EdgeRepository;
import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DatasetProviderService {

    private final EdgeRepository edgeRepository;

    public Stream<GraphEdge> getGraphEdgesFromDataset(long datasetId) {
        return edgeRepository.findAllByDatasetId(datasetId)
            .map(edge -> new GraphEdge(edge.getSourceId(), edge.getTargetId()));
    }

}
