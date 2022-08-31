@dynamicSearchFilterSetup
Feature: Dynamic Search Filter Setup

  Background:
    Given I land on the Dynamic Search Page for the first time

# TESTS

  Scenario: no results text is shown when no results are returned from search
    When no apis exist that match platforms 'API_PLATFORM'
    And I select the platforms 'api-platform'
    And I wait '100' milliSeconds for the api list to be redrawn
    Then The 'No Results' Content is shown
    And Search box is displayed with value ''
    And Platform checkboxes 'api-platform' are selected
    And Navigation controls should not be visible

  Scenario: Ten APIs setup in the backend and search returns all ten
    When All Apis will be returned for platforms 'API_PLATFORM'
    And I select the platforms 'api-platform'
    And I wait '100' milliSeconds for the api list to be redrawn
    Then page '1' of all api results are shown
    And Search box is displayed with value ''
    And Platform checkboxes 'api-platform' are selected

  Scenario: Searching for an API by platform, navigating to it, then navigating back, returns to the same search result
    When All Apis will be returned for platforms 'API_PLATFORM,CMA'
    And An api exists with id '2f0c9fc4-7773-433b-b4cf-15d4cb932e36'
    And I select the platforms 'api-platform,cma'
    And I wait '100' milliSeconds for the api list to be redrawn
    And I click on the element with id 'details-href-0'
    And I click on the 'Back' link
    And I wait '100' milliSeconds for the api list to be redrawn
    Then page '1' of all api results are shown
    And Platform checkboxes 'api-platform,cma' are selected

