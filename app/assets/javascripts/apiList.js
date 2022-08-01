'use strict';

export class ApiList {


    constructor() {

    }



   callbackFunction(xmlhttp)
    {
        //alert(xmlhttp.responseXML);
    }

    init() {
        setUpOnClicks()
        loadData("", [], []);
    }



}
function  loadData(searchTerm, backends, platforms) {
    console.log("loadData -  searchTerm:" + searchTerm)
    if(searchTerm.length < 2  && platforms.length===0) {
        drawNoResults()
    } else {
        getApis(searchTerm, backends, platforms, function () {
            drawResults(this.responseText);
        });
    }
    //
    // URL = "/quicksearch?searchValue=api"
    //
    // var xmlhttp = new XMLHttpRequest();
    // xmlhttp.onreadystatechange = this.callbackFunction(xmlhttp);
    // xmlhttp.open("GET", URL, false);
    //
    //
    // alert(xmlhttp.responseText);
    // document.getElementById("api-list").innerHTML = xmlhttp.statusText + ":" + xmlhttp.status + "<BR><textarea rows='100' cols='100'>" + xmlhttp.responseText + "</textarea>";
}


function getApis(searchTerm, backEnds, platforms, callback) {

    console.log("getApis - searchTerm:"+searchTerm)
    var url = "/api-catalogue/quicksearch?searchValue="+searchTerm+backEnds+platforms;
 console.log(url)
    var xhttp = new XMLHttpRequest();

    xhttp.addEventListener('load', callback);
    xhttp.addEventListener('error', () => console.log("Request to "+url+" failed"));

    xhttp.open("GET", url , true);
    xhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xhttp.send();
}

function drawNoResults(){
    clearApiList()

    const rootNode = document.getElementById('api-container');

    let mainPara = document.createElement("p")
    mainPara.classList.add("govuk-body", "govuk-apis-list-description")
    mainPara.setAttribute("id", "no-results")

    let header = document.createElement("h1")
    header.classList.add("govuk-body")
    header.setAttribute("id", "page-heading")
    header.innerHTML = "Your search did not match any APIs."

    let secondPara =  document.createElement("p")
    secondPara.classList.add("govuk-body", "govuk-apis-list-description")
    secondPara.setAttribute("id", "check-all-words")
    secondPara.innerHTML= "Check all words are spelt correctly or try a different keyword."

    let lastPara = document.createElement("p")
    lastPara.classList.add("govuk-body")
    lastPara.innerHtml = "If you can’t find the API you are looking for, email the API catalogue team at <a id='contact-link' href='mailto:api-catalogue-g@digital.hmrc.gov.uk' class='govuk-link'\>api-catalogue-g@digital.hmrc.gov.uk</a>."

    rootNode.appendChild(mainPara)
    rootNode.appendChild(header)
    rootNode.appendChild(secondPara)
    rootNode.appendChild(lastPara)
}


function drawResults(response) {
    clearApiList()
    let apis = JSON.parse(response);
    if(apis.length>0) {
        const rootNode = document.getElementById('api-container');


        buildApiCount(rootNode, apis.length)


        var apiList = document.createElement("ul")
        apiList.classList.add("govuk-apis-list")

        for (let i = 0; i < apis.length; i++) {
            drawApiRow(apiList, apis[i], i)

        }
        rootNode.appendChild(apiList)
    } else{
        drawNoResults()
    }
}




function drawApiRow(listNode, api, rowNumber) {
    let li = document.createElement("li");
    li.classList.add("govuk-apis-list-item")

    let apiH2= document.createElement("h2")
    apiH2.setAttribute("id", "api-name-"+rowNumber)
    apiH2.classList.add("govuk-apis-list-name")

    let apiLink = document.createElement("a")
    apiLink.setAttribute("id", "details-href-"+rowNumber)
    apiLink.setAttribute("href", "/api-catalogue/integrations/"+api.id+"/"+api.encodedTitle)
    apiLink.classList.add("govuk-link", "link-nounderline")
    apiLink.innerHTML = api.title


    apiH2.appendChild(apiLink)

    let para= document.createElement("p")
    para.setAttribute("id", "api-description-"+rowNumber)
    para.classList.add("govuk-body-s", "govuk-apis-list-description")
    para.innerHTML = api.description

    li.appendChild(apiH2)
    li.appendChild(para)

    listNode.appendChild(li);
}
function buildApiCount(rootNode, apiCountVal) {
    let apiCount= document.createElement("h1")
    apiCount.setAttribute("id", "page-heading");
    apiCount.classList.add("govuk-body")
    apiCount.innerHTML = apiCountVal+ " APIs"
   rootNode.appendChild(apiCount)
}


function buildParams(key, hods){
    let params = ""
    for(let i=0;i<hods.length;i++) {
        params = params + "&"+key+"="+hods[i]
    }
    return params
}
function handleSearchBoxClick(){
    const searchBox = document.getElementById("intCatSearch")
    const hodRadios = document.getElementById("backend-items").getElementsByClassName("govuk-checkboxes__input")
    const platformBoxes = document.getElementById("platform-items").getElementsByClassName("govuk-checkboxes__input")

    let selectedHodRadios = []
    let selectedPlatformRadios = []
    for(let i=0;i<hodRadios.length;i++) {
        if(hodRadios[i].checked){
            selectedHodRadios.push(hodRadios[i].value)
            console.log(hodRadios[i].value)
        }

    }
    for(let x=0;x<platformBoxes.length;x++) {
        if(platformBoxes[x].checked){
            selectedPlatformRadios.push(platformBoxes[x].value)
            console.log(platformBoxes[x].value)
        }
    }


        loadData(searchBox.value, buildParams("backendsFilter", selectedHodRadios), buildParams("platformFilter", selectedPlatformRadios));
        console.log(searchBox.value)


}

function clearApiList(){
    console.log("about to clean api list")
    const rootNode = document.getElementById('api-container');
    rootNode.innerHTML ='';
}

function addOnClickToElement(element){
    if(element.addEventListener){
        element.addEventListener('click', handleSearchBoxClick);
    }else if(element.attachEvent){
        element.attachEvent('onclick', handleSearchBoxClick());
    }
}

function setUpOnClicks() {


    addOnClickToElement(document.getElementById("intCatSearch"))


    const hodRadios = document.getElementById("backend-items").getElementsByClassName("govuk-checkboxes__input")
    for(let i=0;i<hodRadios.length;i++) {
        addOnClickToElement(hodRadios[i])
    }
    const platformRadios = document.getElementById("platform-items").getElementsByClassName("govuk-checkboxes__input")
    for(let i=0;i<platformRadios.length;i++) {
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

