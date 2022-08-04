'use strict';

export class ApiList {


    constructor() {

    }

    init() {
        setUpOnClicks()
        loadData("", "", 1, true);
    }


}

function loadData(searchTerm, platformFilter, page, isInitialLoad) {
    console.log("loadData -  searchTerm:" + searchTerm)
    if (searchTerm.length < 2 && platformFilter.length === 0) {
        if (isInitialLoad) {
            getApis("", "", page, function () {
                drawResults(this.responseText);
            });
        } else {
            drawNoResults()
        }
    } else {
        getApis(searchTerm, platformFilter, page, function () {
            drawResults(this.responseText);
        });
    }
}


function getApis(searchTerm, platforms, page, callback) {

    console.log("getApis - searchTerm:" + searchTerm)
    console.log("getApis - platforms:" + platforms)
    var pageFilter = "&currentPage=" + page
    var url = "/api-catalogue/quicksearch?searchValue=" + searchTerm + platforms + pageFilter;
    console.log(url)
    var xhttp = new XMLHttpRequest();

    xhttp.addEventListener('load', callback);
    xhttp.addEventListener('error', () => console.log("Request to " + url + " failed"));

    xhttp.open("GET", url, true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send();
}

function drawNoResults() {
    clearApiList()

    const rootNode = document.getElementById('api-container');

    let mainPara = buildDomElement("p", "no-results", ["govuk-body", "govuk-apis-list-description"], "")
    let header = buildDomElement("h1", "govuk-body", ["govuk-body"], "Your search did not match any APIs.")

    let secondPara = buildDomElement("p", "check-all-words", ["govuk-body", "govuk-apis-list-description"], "Check all words are spelt correctly or try a different keyword.")

    let lastPara = document.createElement("p")
    lastPara.classList.add("govuk-body")
    lastPara.innerHTML = "If you can’t find the API you are looking for, email the API catalogue team at <a id='contact-link' href='mailto:api-catalogue-g@digital.hmrc.gov.uk' class='govuk-link'\>api-catalogue-g@digital.hmrc.gov.uk</a>."

    rootNode.appendChild(mainPara)
    rootNode.appendChild(header)
    rootNode.appendChild(secondPara)
    rootNode.appendChild(lastPara)

}


function drawResults(response) {
    clearApiList()
    let parsedResponse = JSON.parse(response);

    let apis = parsedResponse.results

    if (apis.length > 0) {
        const rootNode = document.getElementById('api-container');



        buildApiCount(rootNode, parsedResponse.totalCount)


        var apiList = document.createElement("ul")
        apiList.classList.add("govuk-apis-list")

        for (let i = 0; i < apis.length; i++) {
            drawApiRow(apiList, apis[i], i)

        }
        rootNode.appendChild(apiList)
        let nav = drawPagingNavigation(parsedResponse.itemsPerPage,
            parsedResponse.currentPage,
            parsedResponse.numberOfPages,
            parsedResponse.fromResults,
            parsedResponse.toResults,
            parsedResponse.totalCount,
            parsedResponse.firstPageLink,
            parsedResponse.lastPageLink)

        rootNode.appendChild(nav)

    } else {
        drawNoResults()
    }
}

function drawPagingNavigation(itemsPerPage,
                              currentPage,
                              numberOfPages,
                              fromResults,
                              toResults,
                              totalCount,
                              firstPageLink,
                              lastPageLink) {
    let nav = buildDomElement("nav", "pagination-label", ["moj-pagination"], "")

    let navLabel = buildDomElement("p", "", ["govuk-visually-hidden"], "Pagination navigation")
    navLabel.setAttribute("aria-labelledby", "pagination-label")
    let navButtons = buildDomElement("ul", "", ["moj-pagination__list"], "")

    if (numberOfPages > 1) {
        if (currentPage !== 1) {
            let prevButton = buildDomElement("li", "page-prev", ["moj-pagination__item", "moj-pagination__item--prev"], "")
            let prevLink = buildDomElement("a", "", ["moj-pagination__link"], 'Previous<span class="govuk-visually-hidden"> set of pages</span>')
            // addOnClickToPageElement(prevLink, currentPage-1)
            prevButton.appendChild(prevLink)
            navButtons.appendChild(prevButton)
        }

        let i = firstPageLink

        for (; i < lastPageLink+1; i++) {
            if(currentPage === i) {
                navButtons.appendChild(
                    buildDomElement("li", "pagenumber-"+i, ["moj-pagination__item", "moj-pagination__item--active"], i+"")
                )
            } else {
                let item =  buildDomElement("li", "pagenumber-"+i, ["moj-pagination__item"], "")
                let pageItemLink = buildDomElement("a", "pageLink-"+i, ["moj-pagination__link"], i+"")
                // addOnClickToPageElement(pageItemLink,i)
                item.appendChild(pageItemLink)
                navButtons.appendChild(item)
            }

        }


        if (currentPage < numberOfPages) {
            var pageVal = currentPage +1
            let nextButton = buildDomElement("li", "page-next", ["moj-pagination__item", "moj-pagination__item--next"], "")
            let nextLink = buildDomElement("a", "", ["moj-pagination__link"], 'Next<span class="govuk-visually-hidden"> set of pages</span>')
            // addOnClickToPageElement(nextButton, pageVal)
            nextButton.appendChild(nextLink)
            navButtons.appendChild(nextButton)
        }

    }
    let pageResultsText = ' Showing  <b>' + fromResults + '</b> to <b>' + foobar(toResults, totalCount) + '</b> of <b>' + totalCount + '</b> results';
    let pageResults = buildDomElement("p", "page-results", ["moj-pagination__results"], pageResultsText)


    nav.appendChild(navLabel)
    nav.appendChild(navButtons)
    nav.appendChild(pageResults)

    return nav

}

function foobar(toResults, totalCount) {
    if (toResults > totalCount) {
        return totalCount
    } else {
        return toResults
    }
}

function drawApiRow(listNode, api, rowNumber) {
    let li = buildDomElement("li", "", ["govuk-apis-list-item"], "")

    let apiH2 = buildDomElement("h2", "api-name-" + rowNumber, ["govuk-apis-list-name"], "")

    let apiLink = buildDomElement("a", "details-href-" + rowNumber, ["govuk-link", "link-nounderline"], api.title)
    apiLink.setAttribute("href", "/api-catalogue/integrations/" + api.id + "/" + api.encodedTitle)

    apiH2.appendChild(apiLink)

    li.appendChild(apiH2)
    li.appendChild(buildDomElement("p", "api-description-" + rowNumber, ["govuk-body-s", "govuk-apis-list-description"], api.description))

    listNode.appendChild(li);
}

function buildApiCount(rootNode, apiCountVal) {
    rootNode.appendChild(buildDomElement("h1", "page-heading", ["govuk-body"], apiCountVal + " APIs"))
}

function buildDomElement(tag, idValue, classes, innerHtml) {
    let element = document.createElement(tag)
    if (idValue.length > 0) {
        element.setAttribute("id", idValue);
    }
    element.classList.add(...classes)
    if (innerHtml.length > 0) {
        element.innerHTML = innerHtml
    }
    return element
}


function buildParams(key, items) {
    let params = ""
    for (let i = 0; i < items.length; i++) {
        params = params + "&" + key + "=" + items[i]
    }
    return params
}

function handleSearchBoxClick() {
    const searchBox = document.getElementById("intCatSearch")

    const platformBoxes = document.getElementById("platform-items").getElementsByClassName("govuk-checkboxes__input")


    let selectedPlatformRadios = []

    for (let x = 0; x < platformBoxes.length; x++) {
        if (platformBoxes[x].checked) {
            selectedPlatformRadios.push(platformBoxes[x].value)
            console.log(platformBoxes[x].value)
        }
    }
    console.log(selectedPlatformRadios)
    let platformFilter = buildParams("platformFilter", selectedPlatformRadios)
    loadData(searchBox.value, platformFilter, 1, false);


}

function clearApiList() {
    console.log("about to clean api list")

    const rootNode = document.getElementById('api-container');
    rootNode.innerHTML = '';
}

// function handlePageLinkClick(page){
//     const searchBox = document.getElementById("intCatSearch")
//
//     const platformBoxes = document.getElementById("platform-items").getElementsByClassName("govuk-checkboxes__input")
//
//
//     let selectedPlatformRadios = []
//
//     for (let x = 0; x < platformBoxes.length; x++) {
//         if (platformBoxes[x].checked) {
//             selectedPlatformRadios.push(platformBoxes[x].value)
//             console.log(platformBoxes[x].value)
//         }
//     }
//
//     loadData(searchBox.value, buildParams("platformFilter", selectedPlatformRadios), page);
//
//
// }

// function addOnClickToPageElement(element, page) {
//     if (element.addEventListener) {
//         element.addEventListener('click', handlePageLinkClick(page));
//     } else if (element.attachEvent) {
//         element.attachEvent('onclick', handlePageLinkClick(page));
//     }
// }

function addOnClickToElement(element) {
    if (element.addEventListener) {
        element.addEventListener('click', handleSearchBoxClick);
    } else if (element.attachEvent) {
        element.attachEvent('onclick', handleSearchBoxClick);
    }
}


function setUpOnClicks() {

console.log("setUpClicks")
    addOnClickToElement(document.getElementById("intCatSearch"))

    const platformRadios = document.getElementById("platform-items").getElementsByClassName("govuk-checkboxes__input")
    for(let i=0;i<platformRadios.length;i++) {
        console.log("platformItem"+i)
        addOnClickToElement(platformRadios[i])
    }
    addOnClickToElement(document.getElementById("intCatSearchButton"))
}

/**
 * Adds event listener on page load, gets the api list by its ID and draws table.
 * Initialises the base state of the MultiFileUpload object.
 */
document.addEventListener('DOMContentLoaded', function () {

    const apiList = new ApiList();
    apiList.init();

});

