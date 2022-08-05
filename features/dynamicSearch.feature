@dynamicSearchSetup
Feature: Dynamic Search Setup

  Background:
    Given All available apis are available
    And I navigate to the 'Dynamic Search' page

#   No APIs setup
#  Scenario: No APIs setup in the backend and click search button with no search keyword
#    When I click on the button with id 'intCatSearchButton'
#    Then Element with id 'page-heading' exists with text 'Your search did not match any APIs.'
#    And Element with id 'check-all-words' exists with text 'Check all words are spelt correctly or try a different keyword.'

#   APIs setup and are shown in search results

# TESTS
#1). initial page load returns all APIS, filters are reset and empty
#2). no results text is shown when search returns no results
#3). Apis are shown when results are returned -> probably tests returning different amount of apis than test 1
#4). test paging Next
#5). test Paging previous
#6). test paging page numbers
#7). test Paging component is drawn correctly i.e. current page is not a link, next is not shown on 1st page, previous on last.
#8). test the navigation section "showing x of y Results of z" is correct
#9). test paging component when only 1 page i.e. no navigation but still has "showing x of y Results of z"

  Scenario: Three APIs setup in the backend and search for one and it is returned
    When I enter the search keyword 'API' then click the search button
    Then All Api results are shown
    And Element with id 'page-heading' exists with text '10 APIs'
