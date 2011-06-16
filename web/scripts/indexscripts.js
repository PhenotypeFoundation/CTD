 /**
  * Function that loads the content of the main pages
  * a noChacheVar is added in order to bypass the caching mechanism of some browsers
  */
 
 function loadPage(page, div) {
    // indicate in the div that a page is loading
    $("#"+div).block({ message: "<img src='./images/wait.gif' alt='loading page'/><br /><h2>Loading page...</h2>"});
    
    // add the noCacheVar to the end of the querystring
    var strSep = "?";
    if(page.indexOf("?") != -1) {
        strSep = "&";
    }
    page = page + strSep + "noCacheVar=" + new Date().getTime();
    
    // retrieve a page through the AJAX mechanism
    $.ajax({
        url: "./"+page,
        success: function(data, textStatus, jqXHR){
            // If the response indicates a redirect (both checks are needed because of browser issues)
            if(jqXHR.getResponseHeader("RedirGSCF") != null && jqXHR.getResponseHeader("RedirGSCF") != "") {
                // redirect the user
                window.location.replace(jqXHR.getResponseHeader("RedirGSCF"));
            } else {
                // show the data
                $("#"+div).html(data);
            }
            $("#"+div).unblock();
        },
        error: function (xhr, ajaxOptions, thrownError) {
            // if there is an error, show it's code and message
            $("#"+div).html(xhr.status+" "+thrownError);
            $("#"+div).unblock();
        }
    });
}