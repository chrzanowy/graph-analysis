package com.chrzanowy.graphanalysis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DatasetNotFound extends ResponseStatusException {

    public DatasetNotFound(Long datasetId) {
        super(HttpStatus.BAD_REQUEST, "Dataset %s nof found".formatted(datasetId));
    }
}
