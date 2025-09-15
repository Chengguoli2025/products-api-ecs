@e2e
Feature: Product Management
  As a user
  I want to manage products
  So that I can perform CRUD operations

  Scenario: Create and retrieve a product
    Given the application is running
    When I create a product with name "Test Product" and price "99.99"
    Then the product is created successfully
    When I get all products
    Then I should see the created product in the list
    When I get the product by id
    Then I should get the product details
    When I delete the product
    Then the product is deleted successfully