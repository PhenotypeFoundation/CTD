 function loadPage(page, div) {
    var strSep = "?";
    if(page.indexOf("?") != -1) {
        strSep = "&";
    }
    page = page + strSep + "noCacheVar=" + new Date().getTime();
    $.ajax({
        url: "./"+page,
        success: function(data, textStatus, jqXHR){
            if(jqXHR.getResponseHeader("RedirGSCF") != null && jqXHR.getResponseHeader("RedirGSCF") != "") {
                window.location.replace(jqXHR.getResponseHeader("RedirGSCF"));
            } else {
                $("#"+div).html(data);
            }
        }
    });
}