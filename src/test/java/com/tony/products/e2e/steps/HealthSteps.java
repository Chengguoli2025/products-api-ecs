package com.tony.products.e2e.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;

public class HealthSteps {

    @LocalServerPort
    private int port;

    private Response response;

    @Given("the health endpoint is available")
    public void theHealthEndpointIsAvailable() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @When("I check the health status")
    public void iCheckTheHealthStatus() {
        response = given()
                .when()
                .get("/health");
    }

    @Then("the application should be healthy")
    public void theApplicationShouldBeHealthy() {
        response.then()
                .statusCode(200);
    }
}