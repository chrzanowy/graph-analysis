package com.chrzanowy.graphanalysis.loader;

import static com.chrzanowy.graphanalysis.FileTestUtils.createTempFileWithContent;

import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import java.util.List;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class CsvDatasetLoaderTest {

    private CsvDatasetLoader csvDatasetLoader = new CsvDatasetLoader();

    @Test
    void shouldSupportOnlyCsv() {
        //given
        //when
        //then
        Assertions.assertThat(csvDatasetLoader.supports("csv")).isTrue();
    }

    @SneakyThrows
    @Test
    void shouldLoadFileToEdges() {
        //given
        var tempFileWithContent = createTempFileWithContent();

        //when
        List<GraphEdge> stringStringMap = csvDatasetLoader.loadFileToEdges(tempFileWithContent);

        //then
        Assertions.assertThat(stringStringMap)
            .contains(new GraphEdge(27, 24));
    }
}