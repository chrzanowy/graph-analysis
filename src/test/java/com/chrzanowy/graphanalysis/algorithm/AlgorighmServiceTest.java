package com.chrzanowy.graphanalysis.algorithm;

import com.chrzanowy.graphanalysis.db.AlgorithmRepository;
import com.chrzanowy.graphanalysis.db.model.AlgorithmStatus;
import com.chrzanowy.graphanalysis.rest.model.AlgorithmStatusResponse;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AlgorighmServiceTest {

    @Mock
    private AlgorithmRepository algorithmRepository;

    @InjectMocks
    private AlgorighmService algorighmService;

    @BeforeEach
    void setUp() {
        Mockito.reset(algorithmRepository);
    }

    @Test
    void shouldGetAlgorithmStatus() {
        //given
        var algorithmName = "A1";
        var algorithmStatus = new AlgorithmStatus(algorithmName, true);
        Mockito.when(algorithmRepository.findById(algorithmName))
            .thenReturn(Optional.of(algorithmStatus));
        Mockito.when(algorithmRepository.findAll())
            .thenReturn(List.of(algorithmStatus));

        //when
        Optional<AlgorithmStatusResponse> algorithmStatusResponse = algorighmService.getAlgorithmStatus(algorithmName);
        List<AlgorithmStatusResponse> algorithmStatusResponseList = algorighmService.getAlgorithmsStatus();

        //then
        Assertions.assertThat(algorithmStatusResponseList)
            .hasSize(1)
            .first()
            .satisfies(response -> {
                Assertions.assertThat(algorithmStatusResponse).isPresent()
                    .get()
                    .isEqualTo(response);
            });
    }

    @Test
    void shouldSetAlgorithmStatus() {
        //given
        var algorithmName = "A1";
        var algorithmStatus = new AlgorithmStatus(algorithmName, true);
        Mockito.when(algorithmRepository.findById(algorithmName))
            .thenReturn(Optional.of(algorithmStatus));

        //when
        algorighmService.toggleAlgorithm(algorithmName, false);

        //then
        Mockito.verify(algorithmRepository).save(Mockito.argThat(status -> !status.isEnabled()));
    }

}