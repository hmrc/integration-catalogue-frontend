@dynamicSearchSetup
Feature: Dynamic Search Setup

  Background:
    Given I navigate to the 'Dynamic Search' page

#   No APIs setup
  Scenario: No APIs setup in backend and click search button with no search keyword
    When I click on the button with id 'intCatSearchButton'

