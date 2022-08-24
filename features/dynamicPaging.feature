@dynamicPagingSetup
Feature: Dynamic Paging Setup

  Background:
    Given All 10 test apis are matched, with no search filters, items per page is '2' and requested page should be '1'
    When I navigate to the 'Dynamic Search' page

# TESTS
#
  Scenario: Testing next and previous buttons behave as expected
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

  Scenario: Testing the page number navigation items behave as expected
    When All 10 test apis are matched, with no search filters, items per page is '2' and requested page should be '2'
    Then I click on the page link '2'
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '2' of all api results are shown
    And Navigation should display Showing '3' to '4' of '10' results
    And Navigation controls should be visible on page '2' of '5' pages
    And I wait '500' milliSeconds for the api list to be redrawn
    When All 10 test apis are matched, with no search filters, items per page is '2' and requested page should be '3'
    Then I click on the page link '3'
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '3' of all api results are shown
    And Navigation should display Showing '5' to '6' of '10' results
    And Navigation controls should be visible on page '3' of '5' pages
    And I wait '500' milliSeconds for the api list to be redrawn
    When All 10 test apis are matched, with no search filters, items per page is '2' and requested page should be '4'
    Then I click on the page link '4'
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '4' of all api results are shown
    And Navigation should display Showing '7' to '8' of '10' results
    And Navigation controls should be visible on page '4' of '5' pages
    And I wait '500' milliSeconds for the api list to be redrawn
    When All 10 test apis are matched, with no search filters, items per page is '2' and requested page should be '5'
    Then I click on the page link '5'
    And I wait '500' milliSeconds for the api list to be redrawn
    Then page '5' of all api results are shown
    And Navigation should display Showing '9' to '10' of '10' results
    And Navigation controls should be visible on page '5' of '5' pages
