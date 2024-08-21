package com.chrzanowy.graphanalysis.algorithm;

import com.chrzanowy.graphanalysis.db.AlgorithmRepository;
import com.chrzanowy.graphanalysis.rest.model.AlgorithmStatusResponse;
import com.chrzanowy.graphanalysis.exception.AlgorithmNotFound;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlgorighmService {

    private final Lock lock = new ReentrantLock();

    private final AlgorithmRepository algorithmRepository;

    public AlgorithmStatusResponse toggleAlgorithm(String algorithmName, boolean status) {
        try {
            log.debug("Waiting for lock {}", Thread.currentThread().getName());
            lock.lock();
            var algorithm = algorithmRepository.findById(algorithmName)
                .orElseThrow(() -> new AlgorithmNotFound(algorithmName));
            algorithm.setEnabled(status);
            algorithmRepository.save(algorithm);
            log.info("Algorithm {} enabled", algorithmName);
            return new AlgorithmStatusResponse(algorithmName, status);
        } finally {
            log.debug("Releasing for lock {}", Thread.currentThread().getName());
            lock.unlock();
        }
    }

    public List<AlgorithmStatusResponse> getAlgorithmsStatus() {
        try {
            lock.lock();
            return algorithmRepository.findAll()
                .stream()
                .map(algorithm -> new AlgorithmStatusResponse(algorithm.getId(), algorithm.isEnabled()))
                .collect(Collectors.toList());
        } finally {
            lock.unlock();
        }
    }

    public Optional<AlgorithmStatusResponse> getAlgorithmStatus(String algorithmName) {
        try {
            lock.lock();
            return algorithmRepository.findById(algorithmName)
                .map(algorithm -> new AlgorithmStatusResponse(algorithm.getId(), algorithm.isEnabled()));
        } finally {
            lock.unlock();
        }
    }
}
