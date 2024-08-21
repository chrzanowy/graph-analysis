package com.chrzanowy.graphanalysis.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AlgorithmNotFound extends ResponseStatusException {

    public AlgorithmNotFound(String algorithmName) {
        super(HttpStatus.BAD_REQUEST, "Algorithm %s nof found".formatted(algorithmName));
    }
}
