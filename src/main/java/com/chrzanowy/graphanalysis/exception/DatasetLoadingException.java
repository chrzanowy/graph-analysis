package com.chrzanowy.graphanalysis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DatasetLoadingException extends ResponseStatusException {

    public DatasetLoadingException(String fileName) {
        super(HttpStatus.BAD_REQUEST, "Could not load dataset from file %s".formatted(fileName));
    }
}
