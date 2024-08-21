package com.chrzanowy.graphanalysis.rest.controller;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class RestIntegrationTest {

    @LocalServerPort
    private int port;

    public String getBaseUrl() {
        return "http://localhost:" + port + "/";
    }

    public RequestSpecification givenRequest() {
        return RestAssured.given()
            .port(port);
    }

}
