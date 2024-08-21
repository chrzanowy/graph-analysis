package com.chrzanowy.graphanalysis.analysis.service;

import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import com.chrzanowy.graphanalysis.db.EdgeRepository;
import com.chrzanowy.graphanalysis.db.model.Edge;
import java.util.stream.Stream;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DatasetProviderServiceTest {

    @Mock
    private EdgeRepository edgeRepository;

    @InjectMocks
    private DatasetProviderService datasetProviderService;

    @Test
    void shouldGetGraphEdgesFromDataset() {
        // given
        var datasetId = 1L;
        var edge = new Edge(21L, 37L, datasetId);
        Mockito.when(edgeRepository.findAllByDatasetId(datasetId)).thenReturn(Stream.of(edge));
        // when
        Stream<GraphEdge> graphEdgeStream = datasetProviderService.getGraphEdgesFromDataset(datasetId);
        // then
        Assertions.assertThat(graphEdgeStream)
            .hasSize(1)
            .containsExactly(new GraphEdge(21L, 37L));
    }

}