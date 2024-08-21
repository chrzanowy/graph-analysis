package com.chrzanowy.graphanalysis.db;

import com.chrzanowy.graphanalysis.analysis.Algorithm;
import com.chrzanowy.graphanalysis.config.TimeProvider;
import com.chrzanowy.graphanalysis.db.model.Analysis;
import com.chrzanowy.graphanalysis.rest.controller.RestIntegrationTest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AnalysisRepositoryTest extends RestIntegrationTest {

    @Autowired
    AnalysisRepository analysisRepository;

    @AfterEach
    void cleanUp() {
        analysisRepository.deleteAll();
    }

    @Test
    void shouldSaveAnalysisAndFindIt() {
        //given
        var testStart = TimeProvider.getCurrentTime();
        var analysisToSave = new Analysis(1L, Algorithm.A1, Map.of("arg", List.of(1, 2, 3)), testStart, testStart);

        //when
        Analysis savedAnalysis = analysisRepository.save(analysisToSave);

        //then
        Assertions.assertThat(savedAnalysis)
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(analysisToSave);
        Assertions.assertThat(savedAnalysis.getClass())
            .isNotNull();

        //when
        Optional<Analysis> byId = analysisRepository.findById(savedAnalysis.getId());

        //then
        Assertions.assertThat(byId)
            .isPresent()
            .get()
            .isEqualTo(savedAnalysis);

        //when
        List<Analysis> byAlgorithmName = analysisRepository.findAllByAlgorithmName(Algorithm.A1);

        //then
        Assertions.assertThat(byAlgorithmName)
            .containsExactly(savedAnalysis);

        //when
        List<Analysis> all = (List<Analysis>) analysisRepository.findAll();

        //then
        Assertions.assertThat(all)
            .containsExactly(savedAnalysis);
    }

}