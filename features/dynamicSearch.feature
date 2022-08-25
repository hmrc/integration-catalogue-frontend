@dynamicSearchSetup
Feature: Dynamic Search Setup

  Background:
    Given I land on the Dynamic Search Page for the first time

# TESTS
#
  Scenario: no results text is shown when no results are returned from search
    When no apis exist that match search keyword 'API'
    And I enter the search keyword 'API' then click the search button
    And I wait '100' milliSeconds for the api list to be redrawn
    Then The 'No Results' Content is shown
    And Search box is displayed with value 'API'
    And Navigation controls should not be visible

  Scenario: Ten APIs setup in the backend and search returns all ten
    When All 10 test apis are matched, items per page is '2' and requested page should be '1' and search keyword is 'API'
    And I enter the search keyword 'API' then click the search button
    And I wait '100' milliSeconds for the api list to be redrawn
    Then page '1' of all api results are shown
    And Search box is displayed with value 'API'
    And Page heading is displayed with the text '10 APIs'
    And Navigation should display Showing '1' to '2' of '10' results
    And Navigation controls should be visible on page '1' of '5' pages

  Scenario: One APIs setup in the backend and search for one and it is returned when clicking the search button
    When One api exists that match search keyword '1API'
    And I enter the search keyword '1API' then click the search button
    And I wait '100' milliSeconds for the api list to be redrawn
    Then One Api result is shown
    And Search box is displayed with value '1API'
    And Page heading is displayed with the text '1 API'
    And Navigation should display Showing '1' to '1' of '1' results
    And Navigation controls should not be visible

  Scenario: One APIs setup in the backend and search for one and it is returned when pressing Enter
    When One api exists that match search keyword '1API'
    And I enter the search keyword '1API' then press Enter
    And I wait '100' milliSeconds for the api list to be redrawn
    Then One Api result is shown
    And Search box is displayed with value '1API'
    And Page heading is displayed with the text '1 API'
    And Navigation should display Showing '1' to '1' of '1' results
    And Navigation controls should not be visible

  Scenario: Ten Apis setup in the backend and search returns all
    When All 10 test apis are matched, items per page is '2' and requested page should be '1' and search keyword is 'API1'
    And I enter the search keyword 'API1' then click the search button
    And I wait '100' milliSeconds for the api list to be redrawn
    Then page '1' of all api results are shown
    And Navigation should display Showing '1' to '2' of '10' results
    And Navigation controls should be visible on page '1' of '5' pages


  Scenario: Searching for an API by keyword, navigating to it, then navigating back, returns to the same search result
    When All 10 test apis are matched, items per page is '2' and requested page should be '2' and search keyword is 'API1'
    And An api exists with id '136791a6-2b1c-11eb-adc1-0242ac120003'
    And I enter the search keyword 'API1' then press Enter
    And I wait '100' milliSeconds for the api list to be redrawn
    And I click on the page link '2'
    And I wait '100' milliSeconds for the api list to be redrawn
    And I click on the element with id 'details-href-0'
    And I click on the 'Back' link
    And I wait '100' milliSeconds for the api list to be redrawn
    Then page '2' of all api results are shown
    And Search box is displayed with value 'API1'
