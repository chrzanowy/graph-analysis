package com.chrzanowy.graphanalysis.loader;

import com.chrzanowy.graphanalysis.algorithm.AlgorighmService;
import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.db.AlgorithmRepository;
import com.chrzanowy.graphanalysis.db.model.AlgorithmStatus;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile({"local", "test"})
class FeaturesLoaderService {

    private final AlgorighmService algorighmService;

    private final AlgorithmRepository algorithmRepository;

    @EventListener
    public void loadAlgorithms(ApplicationReadyEvent event) {
        Arrays.stream(Algorithm.values())
            .map(algorithm -> {
                AlgorithmStatus entity = new AlgorithmStatus(algorithm.name(), true);
                return algorithmRepository.save(entity);
            })
            .forEach(algorithm -> algorighmService.toggleAlgorithm(algorithm.getId(), algorithm.isEnabled()));
    }
}