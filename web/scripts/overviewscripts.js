var overviewdetail = "";

function showOverviewDetails(assay) {
    nowopen = overviewdetail;
    closeOverviewDetails();
    if(nowopen!=assay) {
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
}

function closeOverviewDetails() {
    if(overviewdetail!="") {
        $("#"+overviewdetail+"HiddenRows").css({
            "visibility": "collapse"
        });
        overviewdetail = "";
    }
}