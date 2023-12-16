Feature: Instance process

  Scenario: task is created
    Given a new process instance is created
    Then a user task is created for the process instance
