
var ready = (callback) => {
    if (document.readyState != "loading") callback();
    else document.addEventListener("DOMContentLoaded", callback);
  }

  ready(() => { 
    /* Do things after DOM has fully loaded */ 
    


    document.querySelector("#show-survey").addEventListener("click", (e) => { 
        document.querySelector("#feedback").style.display = "none";
        document.querySelector("#survey").style.display = "block";
    });

    document.querySelector("#close-survey").addEventListener("click", (e) => { 
        document.querySelector("#survey").style.display = "none";
        document.querySelector("#feedback").style.display = "block";
    });
  });

// var showHide = function () {
//     var showHideContent = new GOVUK.ShowHideContent();
//     showHideContent.init();
// };

// if (document.addEventListener) {
//     document.addEventListener("DOMContentLoaded", function () {
//         showHide();
//     });
// } else {
//     window.attachEvent("onload", function () {
//         showHide();
//     });
// }