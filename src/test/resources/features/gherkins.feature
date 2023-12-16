Feature: Customer Onboarding Process

  Scenario: Successful service activation after credit approval and payment
    Given a new process instance is created
    And the user information is collected
    And the service availability check is passed
    When the customer selects a service package and agrees to terms
    And the credit check is approved
    And the customer completes the payment
    Then the installation is scheduled
    And the service is activated successfully
#
#  Scenario: Service not available for the customer's area
#    Given a new process instance is created
#    When the service availability check is failed
#    Then the customer is informed and the process is ended
#
#  Scenario: Customer disagrees with the service package
#    Given a new process instance is created
#    And the service availability check is passed
#    When the customer selects a service package but disagrees with terms
#    Then the process is ended without scheduling installation
#
#  Scenario: Credit check failure leads to process termination
#    Given a new process instance is created
#    And the service availability check is passed
#    And the customer selects a service package and agrees to terms
#    When the credit check is rejected
#    Then the customer is informed about credit rejection and the process is ended
#
#  Scenario: Payment failure after credit approval
#    Given a new process instance is created
#    And the service availability check is passed
#    And the customer selects a service package and agrees to terms
#    And the credit check is approved
#    When the payment fails
#    Then the customer is prompted to retry payment
#    And the process is ended if the payment fails again
#
#  Scenario: Successful service activation with contract preparation
#    Given a new process instance is created
#    And the service availability check is passed
#    And the customer selects a service package and agrees to terms
#    And the credit check is approved
#    And the contract and terms are prepared
#    When the customer completes the payment
#    Then the installation is scheduled
#    And the service is activated successfully
