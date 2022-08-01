@dynamicSearchSetup
Feature: Dynamic Search Setup

  Background:
    Given I navigate to the 'Dynamic Search with no results' page

#   No APIs setup
  Scenario: No APIs setup in the backend and click search button with no search keyword
    When I click on the button with id 'intCatSearchButton'
    Then No Search results are shown
    And Element with id 'check-all-words' exists with text 'Check all words are spelt correctly or try a different keyword.'

#   APIs setup and are shown in search results
  Scenario: Three APIs setup in the backend and search for one and it is returned
    When I enter the search keyword 'API' then click the search button
    Then All Api results are shown
