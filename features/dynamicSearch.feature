@dynamicSearchSetup
Feature: Dynamic Search Setup

  Background:
    Given All 10 test apis are matched, with no search filters, items per page is '2' and requested page should be '1'
    When I navigate to the 'Dynamic Search' page

# TESTS
#
  Scenario: no results text is shown when no results are returned from search
    When no apis exist that match search keyword 'API'
    And I enter the search keyword 'API' then click the search button
    Then The 'No Results' Content is shown
    And Navigation controls should not be visible

  Scenario: Ten APIs setup in the backend and search returns all ten
    When All 10 test apis are matched, items per page is '2' and requested page should be '1' and search keyword is 'API'
    And I enter the search keyword 'API' then click the search button
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '1' of all api results are shown
    And Element with id 'page-heading' exists with text '10 APIs'
    And Navigation should display Showing '1' to '2' of '10' results
    And Navigation controls should be visible on page '1' of '5' pages

  Scenario: One APIs setup in the backend and search for one and it is returned when clicking the search button
    When One api exists that match search keyword '1API'
    And I enter the search keyword '1API' then click the search button
    And I wait '500' milliSeconds for the api list to be redrawn
    Then One Api result is shown
    And Element with id 'page-heading' exists with text '1 APIs'
    And Navigation should display Showing '1' to '1' of '1' results
    And Navigation controls should not be visible

  Scenario: One APIs setup in the backend and search for one and it is returned when pressing Enter
    When One api exists that match search keyword '1API'
    And I enter the search keyword '1API' then press Enter
    And I wait '500' milliSeconds for the api list to be redrawn
    Then One Api result is shown
    And Element with id 'page-heading' exists with text '1 APIs'
    And Navigation should display Showing '1' to '1' of '1' results
    And Navigation controls should not be visible

  Scenario: Ten Apis setup in the backend and search returns all
    When All 10 test apis are matched, items per page is '2' and requested page should be '1' and search keyword is 'API1'
    And I enter the search keyword 'API1' then click the search button
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '1' of all api results are shown
    And Navigation should display Showing '1' to '2' of '10' results
    And Navigation controls should be visible on page '1' of '5' pages

  Scenario: Ten Apis setup in the backend and search returns all then we click on next
    When All 10 test apis are matched, with no search filters, items per page is '2' and requested page should be '2'
    And I click on the element with id 'page-next-link'
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '2' of all api results are shown
    And Navigation should display Showing '3' to '4' of '10' results
    And Navigation controls should be visible on page '2' of '5' pages
    And I wait '500' milliSeconds for the api list to be redrawn
    When I click on the element with id 'page-prev-link'
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '1' of all api results are shown
    And Navigation should display Showing '1' to '2' of '10' results
    And Navigation controls should be visible on page '1' of '5' pages

  Scenario: Ten Apis setup in the backend and search returns all then we click through page numbers 2, 3, 4 then 5
    When All 10 test apis are matched, with no search filters, items per page is '2' and requested page should be '2'
    And I click on the element with id 'pageLink-2'
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '2' of all api results are shown
    And Navigation should display Showing '3' to '4' of '10' results
    And Navigation controls should be visible on page '2' of '5' pages
    And I wait '500' milliSeconds for the api list to be redrawn
    When All 10 test apis are matched, with no search filters, items per page is '2' and requested page should be '3'
    Then I click on the element with id 'pageLink-3'
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '3' of all api results are shown
    And Navigation should display Showing '5' to '6' of '10' results
    And Navigation controls should be visible on page '3' of '5' pages
    And I wait '500' milliSeconds for the api list to be redrawn
    When All 10 test apis are matched, with no search filters, items per page is '2' and requested page should be '4'
    Then I click on the element with id 'pageLink-4'
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '4' of all api results are shown
    And Navigation should display Showing '7' to '8' of '10' results
    And Navigation controls should be visible on page '4' of '5' pages
    And I wait '500' milliSeconds for the api list to be redrawn
    When All 10 test apis are matched, with no search filters, items per page is '2' and requested page should be '5'
    Then I click on the element with id 'pageLink-5'
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '5' of all api results are shown
    And Navigation should display Showing '9' to '10' of '10' results
    And Navigation controls should be visible on page '5' of '5' pages
