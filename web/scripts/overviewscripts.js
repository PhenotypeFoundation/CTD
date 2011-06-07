// Keep track of the div that is expanded
var overviewdetail = "";

/**
 * Function that loads and shows a tbody containing details about an assay
 */
function showOverviewDetails(assay) {
    var nowopen = overviewdetail;
    
    // Close all open detail tbodies
    closeOverviewDetails();
    
    // If nowopen==assay, then the user wanted to close the tbody and not open
    // another one
    if(nowopen!=assay) {
        overviewdetail = assay;
        // Show the tbody
        $("#"+overviewdetail+"HiddenRows").show();
        $.ajax({
            url: "./samplesoverview.jsp?assayToken="+assay,
            success: function(data, textStatus, jqXHR){
                // Set the inner HTML of the tbody (a table with data)
                $("#"+assay+"Details").html(data);
            }
        });
    }
}

/**
 * Function that closes a tbody containing details about an assay
 */
function closeOverviewDetails() {
    // If there is a details tbody open
    if(overviewdetail!="") {
        // Hide the tbody
        $("#"+overviewdetail+"HiddenRows").hide();
        overviewdetail = "";
    }
}