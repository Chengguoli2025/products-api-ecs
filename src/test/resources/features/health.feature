@e2e
Feature: Health Check
  As a system administrator
  I want to check the application health
  So that I can monitor the system status

  Scenario: Check application health
    Given the health endpoint is available
    When I check the health status
    Then the application should be healthy