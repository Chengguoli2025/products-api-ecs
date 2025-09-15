package com.tony.products.e2e.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductSteps {

    @LocalServerPort
    private int port;

    private Response response;
    private String productId;

    @Given("the application is running")
    public void theApplicationIsRunning() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @When("I create a product with name {string} and price {string}")
    public void iCreateAProductWithNameAndPrice(String name, String price) {
        String requestBody = String.format("""
            {
                "name": "%s",
                "description": "Test product",
                "price": %s,
                "quantity": 10
            }
            """, name, price);

        response = given()
                .contentType("application/json")
                .body(requestBody)
                .queryParam("product_type", "SOFTWARE")
                .when()
                .post("/products");
    }

    @Then("the product is created successfully")
    public void theProductIsCreatedSuccessfully() {
        response.then()
                .statusCode(201)
                .body("name", notNullValue())
                .body("price", notNullValue());
        
        productId = response.jsonPath().getString("id");
    }

    @When("I get all products")
    public void iGetAllProducts() {
        response = given()
                .queryParam("product_type", "SOFTWARE")
                .when()
                .get("/products");
    }

    @Then("I should see the created product in the list")
    public void iShouldSeeTheCreatedProductInTheList() {
        response.then()
                .statusCode(200)
                .body("size()", greaterThan(0));
    }

    @When("I get the product by id")
    public void iGetTheProductById() {
        response = given()
                .queryParam("product_type", "SOFTWARE")
                .when()
                .get("/products/" + productId);
    }

    @Then("I should get the product details")
    public void iShouldGetTheProductDetails() {
        response.then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(productId)));
    }

    @When("I delete the product")
    public void iDeleteTheProduct() {
        response = given()
                .queryParam("product_type", "SOFTWARE")
                .when()
                .delete("/products/" + productId);
    }

    @Then("the product is deleted successfully")
    public void theProductIsDeletedSuccessfully() {
        response.then()
                .statusCode(204);
    }
}