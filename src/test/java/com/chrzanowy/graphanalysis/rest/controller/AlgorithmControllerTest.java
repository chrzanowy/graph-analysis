package com.chrzanowy.graphanalysis.rest.controller;

import com.chrzanowy.graphanalysis.db.AlgorithmRepository;
import com.chrzanowy.graphanalysis.db.model.AlgorithmStatus;
import com.chrzanowy.graphanalysis.rest.model.AlgorithmStatusResponse;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AlgorithmControllerTest extends RestIntegrationTest {

    @Autowired
    private AlgorithmRepository algorithmRepository;

    private static final String API_V1_FEATURE_ALGORITHMS = "api/v1/algorithms";


    @AfterEach
    void cleanUp() {
        algorithmRepository.deleteAll();
    }

    @Test
    void shouldGetAllAlgorithmStatus() {
        //given
        algorithmRepository.save(new AlgorithmStatus("A1", true));
        algorithmRepository.save(new AlgorithmStatus("A2", true));
        algorithmRepository.save(new AlgorithmStatus("A3", true));
        //when
        List<AlgorithmStatusResponse> responses = givenRequest()
            .get(getBaseUrl() + API_V1_FEATURE_ALGORITHMS)
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<List<AlgorithmStatusResponse>>() {
            });

        //then
        Assertions.assertThat(responses)
            .isNotEmpty()
            .containsExactlyInAnyOrder(
                new AlgorithmStatusResponse("A1", true),
                new AlgorithmStatusResponse("A2", true),
                new AlgorithmStatusResponse("A3", true)
            );
    }

    @Test
    void shouldToggleAlgorithm() {
        //given
        algorithmRepository.save(new AlgorithmStatus("A3", true));
        //when
        AlgorithmStatusResponse response = givenRequest()
            .queryParam("status", false)
            .patch(getBaseUrl() + API_V1_FEATURE_ALGORITHMS + "/A3")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<AlgorithmStatusResponse>() {
            });

        //then
        Assertions.assertThat(response)
            .isEqualTo(new AlgorithmStatusResponse("A3", false));

        //when
        response = givenRequest()
            .queryParam("status", true)
            .patch(getBaseUrl() + API_V1_FEATURE_ALGORITHMS + "/A3")
            .then()
            .statusCode(200)
            .extract()
            .as(new TypeRef<AlgorithmStatusResponse>() {
            });

        //then
        Assertions.assertThat(response)
            .isEqualTo(new AlgorithmStatusResponse("A3", true));
    }

    @Test
    void shouldReturn400IfAlgorithmNotFound() {
        //given
        //when
        givenRequest()
            .queryParam("status", false)
            .patch(getBaseUrl() + API_V1_FEATURE_ALGORITHMS + "/A3")
            .then()
            .statusCode(400);
    }

}