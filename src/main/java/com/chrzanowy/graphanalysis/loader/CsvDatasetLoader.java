package com.chrzanowy.graphanalysis.loader;

import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CsvDatasetLoader implements DatasetLoader {

    private static final String CSV_FILE_TYPE = "csv";

    @Override
    public boolean supports(String fileType) {
        return fileType.equals(CSV_FILE_TYPE);
    }

    @Override
    public List<GraphEdge> loadFileToEdges(File file) throws IOException, CsvException {
        try (Reader reader = Files.newBufferedReader(file.toPath())) {
            CSVParser parser = new CSVParserBuilder()
                .withSeparator(',')
                .withIgnoreQuotations(true)
                .build();
            try (CSVReader csvReader = new CSVReaderBuilder(reader)
                .withSkipLines(1)
                .withCSVParser(parser)
                .build()) {
                return csvReader
                    .readAll()
                    .stream()
                    .map(line -> new GraphEdge(Long.parseLong(line[0]), Long.parseLong(line[1])))
                    .toList();
            }
        }
    }

}
