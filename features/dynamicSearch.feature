@dynamicSearchSetup
Feature: Dynamic Search Setup

  Background:
    Given All available apis are available with no search filters, items per page is '2' and current page is '1'
    When I navigate to the 'Dynamic Search' page

#  Scenario: initial page load returns all APIS, filters are reset and empty
#    Then page '1' of all api results are shown
#    And All filters and search box are empty

#   No APIs setup
#  Scenario: No APIs setup in the backend and click search button with no search keyword
#    When I click on the button with id 'intCatSearchButton'
#    Then Element with id 'page-heading' exists with text 'Your search did not match any APIs.'
#    And Element with id 'check-all-words' exists with text 'Check all words are spelt correctly or try a different keyword.'

#   APIs setup and are shown in search results

# TESTS

#  Scenario: no results text is shown when no results are returned from search
#    When no apis are exist that match search keyword 'API'
#    And I enter the search keyword 'API' then click the search button
#    Then The 'No Results' Content is shown
#
#  Scenario: Ten APIs setup in the backend and search returns all ten
#    When I enter the search keyword 'API' then click the search button
#    Then page '1' of all api results are shown
#    And Element with id 'page-heading' exists with text '10 APIs'

  Scenario: One APIs setup in the backend and search for one and it is returned
    When One api exists that match search keyword '1API'
    And I enter the search keyword '1API' then click the search button
    And I wait '5' seconds for the api list to be redrawn
    Then One Api result is shown
    And Element with id 'page-heading' exists with text '1 APIs'

 Scenario: Ten Apis setup in the backend and search returns all
  When All available apis are available items per page is '2' and current page is '1' and search keyword is 'API1'
  And I enter the search keyword 'API1' then click the search button
  And I wait '5' seconds for the api list to be redrawn
  Then page '1' of all api results are shown


#2). no results text is shown when search returns no results
#3). Apis are shown when results are returned -> probably tests returning different amount of apis than test 1
#4). test paging Next
#5). test Paging previous
#6). test paging page numbers
#7). test Paging component is drawn correctly i.e. current page is not a link, next is not shown on 1st page, previous on last.
#8). test the navigation section "showing x of y Results of z" is correct
#9). test paging component when only 1 page i.e. no navigation but still has "showing x of y Results of z"
