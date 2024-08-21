package com.chrzanowy.graphanalysis.loader;

import com.chrzanowy.graphanalysis.db.DatasetRepository;
import com.chrzanowy.graphanalysis.db.EdgeRepository;
import com.chrzanowy.graphanalysis.db.NodeRepository;
import com.chrzanowy.graphanalysis.db.model.Dataset;
import com.chrzanowy.graphanalysis.db.model.Edge;
import com.chrzanowy.graphanalysis.db.model.Node;
import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import com.chrzanowy.graphanalysis.exception.DatasetLoadingException;
import com.opencsv.exceptions.CsvException;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"local", "test"})
class DatasetLoaderService {

    private final List<DatasetLoader> datasetLoaders;

    private final NodeRepository nodeRepository;

    private final EdgeRepository edgeRepository;

    private final DatasetRepository datasetRepository;

    @EventListener
    public void loadDatasets(ApplicationReadyEvent event) {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Arrays.stream(resolver.getResources("data_set_*.csv"))
                .forEach(this::loadDatasetFromFile);
        } catch (IOException e) {
            log.error("Could not load datasets", e);
        }
    }

    @Transactional
    public void loadDatasetFromFile(Resource dataset) {
        log.info("Loading dataset from file {}", dataset.getFilename());
        String fileType = getFileType(dataset);
        Optional<DatasetLoader> foundHandler = datasetLoaders.stream()
            .filter(loader -> loader.supports(fileType))
            .findFirst();
        if (foundHandler.isPresent()) {
            Dataset workingDataset = datasetRepository.save(new Dataset(dataset.getFilename()));
            foundHandler
                .map(loader -> {
                    try {
                        return loader.loadFileToEdges(dataset.getFile());
                    } catch (IOException | CsvException e) {
                        log.warn("Could not load dataset from file {}", dataset.getFilename(), e);
                        throw new DatasetLoadingException(dataset.getFilename());
                    }
                })
                .ifPresent(edges -> insertEdges(workingDataset.getId(), edges));
        }
        log.info("Dataset {} loaded", dataset.getFilename());
    }

    private static String getFileType(@NotNull Resource dataset) {
        Objects.requireNonNull(dataset.getFilename());
        return dataset.getFilename()
            .substring(dataset.getFilename().lastIndexOf(".") + 1);
    }

    private void insertEdges(Long datasetId, List<GraphEdge> edges) {
        edges.stream()
            .flatMap(edge -> Stream.of(new Node(edge.sourceId(), datasetId), new Node(edge.targetId(), datasetId)))
            .distinct()
            .forEach(nodeRepository::save);

        edges
            .stream()
            .map(entry -> new Edge(entry.sourceId(), entry.targetId(), datasetId))
            .forEach(edgeRepository::save);
    }

}