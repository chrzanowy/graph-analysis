package com.chrzanowy.graphanalysis.loader;

import com.chrzanowy.graphanalysis.FileTestUtils;
import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import com.chrzanowy.graphanalysis.db.DatasetRepository;
import com.chrzanowy.graphanalysis.db.EdgeRepository;
import com.chrzanowy.graphanalysis.db.NodeRepository;
import com.chrzanowy.graphanalysis.db.model.Dataset;
import com.chrzanowy.graphanalysis.exception.DatasetLoadingException;
import java.io.IOException;
import java.util.List;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.FileSystemResource;

class DatasetLoaderServiceTest {

    private DatasetLoader datasetLoaders = Mockito.mock(DatasetLoader.class);

    private NodeRepository nodeRepository = Mockito.mock(NodeRepository.class);

    private EdgeRepository edgeRepository = Mockito.mock(EdgeRepository.class);

    private DatasetRepository datasetRepository = Mockito.mock(DatasetRepository.class);

    private DatasetLoaderService datasetLoaderService;

    public DatasetLoaderServiceTest() {
        this.datasetLoaderService = new DatasetLoaderService(List.of(datasetLoaders), nodeRepository, edgeRepository, datasetRepository);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(datasetLoaders, nodeRepository, edgeRepository, datasetRepository);
    }

    @SneakyThrows
    @Test
    void shouldLoadDatasetFromFile() {
        //given
        var datasetMock = Mockito.mock(Dataset.class);
        Mockito.when(datasetMock.getId()).thenReturn(1L);
        var tempFileWithContent = FileTestUtils.createTempFileWithContent();
        var fileSystemResource = new FileSystemResource(tempFileWithContent);
        Mockito.when(datasetLoaders.supports("csv")).thenReturn(true);
        Mockito.when(datasetLoaders.loadFileToEdges(tempFileWithContent)).thenReturn(List.of(new GraphEdge(27, 24)));
        Mockito.when(datasetRepository.save(Mockito.any())).thenReturn(datasetMock);

        //when
        datasetLoaderService.loadDatasetFromFile(fileSystemResource);

        //then
        Mockito.verify(nodeRepository).save(Mockito.argThat(x -> x.getNodeId() == 27));
        Mockito.verify(nodeRepository).save(Mockito.argThat(x -> x.getNodeId() == 24));
        Mockito.verify(edgeRepository).save(Mockito.argThat(x -> x.getSourceId() == 27 && x.getTargetId() == 24));
    }

    @SneakyThrows
    @Test
    void shouldNotLoadDatasetIfFileIsNotSupported() {
        //given
        var datasetMock = Mockito.mock(Dataset.class);
        Mockito.when(datasetMock.getId()).thenReturn(1L);
        var tempFileWithContent = FileTestUtils.createTempFileWithContent();
        var fileSystemResource = new FileSystemResource(tempFileWithContent);
        Mockito.when(datasetLoaders.supports("csv")).thenReturn(false);

        //when
        datasetLoaderService.loadDatasetFromFile(fileSystemResource);

        //then
        Mockito.verify(datasetRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(nodeRepository, Mockito.never()).save(Mockito.any());
        Mockito.verify(edgeRepository, Mockito.never()).save(Mockito.any());
    }

    @SneakyThrows
    @Test
    void shouldThrowExceptionIfDatasetFileIsInvalid() {
        //given
        var datasetMock = Mockito.mock(Dataset.class);
        Mockito.when(datasetMock.getId()).thenReturn(1L);
        var tempFileWithContent = FileTestUtils.createTempFileWithContent();
        var fileSystemResource = new FileSystemResource(tempFileWithContent);
        Mockito.when(datasetLoaders.supports("csv")).thenReturn(true);
        Mockito.doThrow(new IOException()).when(datasetLoaders).loadFileToEdges(tempFileWithContent);

        //when
        //then
        Assertions.assertThatThrownBy(() -> datasetLoaderService.loadDatasetFromFile(fileSystemResource))
            .isInstanceOf(DatasetLoadingException.class);

    }
}