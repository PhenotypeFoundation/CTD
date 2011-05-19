var overviewdetail = "";

function showOverviewDetails(assay) {
    closeOverviewDetails();
    overviewdetail = assay;
    $("#"+overviewdetail+"HiddenRows").css({
        "visibility": "visible"
    });
    $.ajax({
        url: "./samplesoverview.jsp?assayToken="+assay,
        success: function(data, textStatus, jqXHR){
            $("#"+overviewdetail+"Details").html(data);
        }
    });
}

function closeOverviewDetails() {
    if(overviewdetail!="") {
        $("#"+overviewdetail+"HiddenRows").css({
            "visibility": "collapse"
        });
        overviewdetail = "";
    }
}