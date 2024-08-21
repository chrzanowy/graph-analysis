package com.chrzanowy.graphanalysis.loader;

import com.chrzanowy.graphanalysis.analysis.graph.GraphEdge;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface DatasetLoader {

    boolean supports(String fileType);

    List<GraphEdge> loadFileToEdges(File file) throws IOException, CsvException;

}
