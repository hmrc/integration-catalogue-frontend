
var ready = (callback) => {
    if (document.readyState != "loading") callback();
    else document.addEventListener("DOMContentLoaded", callback);
  }

  ready(() => { 
    /* Do things after DOM has fully loaded */

  });
