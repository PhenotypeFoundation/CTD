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
            url: "./samplesoverview.jsp?assayToken="+assay+"&noCacheVar=" + new Date().getTime(),
            success: function(data, textStatus, jqXHR){
                // Set the inner HTML of the tbody (a table with data)
                $("#"+assay+"Details").html(data);
                // Close blocking of the UI
                $.unblockUI();
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

/**
 * Function that deletes a sample from the database
 */
function delSampleOverview(sampleToken, assayToken) {
    // Does the users realy want to delete this sample
    if(confirm('This will remove the data and the CEL file linked to this sample. Do you wish to continue?')) {
        // Block UI
        $.blockUI({ message: '<h2><img src="./images/wait.gif" /><br />We are processing your request...</h2>' });
        $.ajax({
          url: "./delSample.jsp?sampleToken="+sampleToken+"&assayToken="+assayToken,
          context: document.body,
          success: function(data){
            // Change the content of the datails div
            overviewdetail = "";
            showOverviewDetails(assayToken);
            number = parseInt($("#numsamp"+assayToken).html())-1;
            if(number>0) {
                $("#numsamp"+assayToken).html(number);
            } else {
                loadPage('overview.jsp','content');
            }
          },
          error: function (xhr, ajaxOptions, thrownError) {
            // If something goes wrong, give an error
            $("#"+assayToken+"Details").html(xhr.status+" "+thrownError);
          }
        });
    }
}