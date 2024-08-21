package com.chrzanowy.graphanalysis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AlgorithmHandlerNotFound extends ResponseStatusException {

    public AlgorithmHandlerNotFound(String algorithmName) {
        super(HttpStatus.BAD_REQUEST, "Algorithm %s handler nof found".formatted(algorithmName));
    }
}
