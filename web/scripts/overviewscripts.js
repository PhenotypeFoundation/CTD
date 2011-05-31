var overviewdetail = "";

function showOverviewDetails(assay) {
    var nowopen = overviewdetail;
    closeOverviewDetails();
    if(nowopen!=assay) {
        overviewdetail = assay;
        $("#"+overviewdetail+"HiddenRows").show();
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
        $("#"+overviewdetail+"HiddenRows").hide();
        overviewdetail = "";
    }
}