/* 
 * Uploadscripts.js
 * By Tjeerd van Dijk and Taco Steemers, thehyve.nl
 * apr 2001
 *
 * Functions in this file are used in upload3.jsp to
 * ensure a good user experience and to place diverse
 * AJAX-calls in order to execute the necessary scripts
 * and catch their response
 */

function init_step1() {
    /* The DIV for step 1 is slowly shown */
    $('#step1').show('slow');
}

function study_selected() {
    if(document.getElementById('selectStudy').value!="none") {
        /* If an option is selected in the study SELECT box then we procede to step 3 */
        $('#step3').show('slow');
        init_step3();
    }
}

function assay_selected() {
    if(document.getElementById('selectAssay').value!="none") {
        /* If an option is selected in the assay SELECT box then we
         * set a boolean to indicate that this step is finished and
         * we try to procede to step 4 */
        step_3_ready = true;
        init_step4();
    } else {
        step_3_ready = false;
    }
}

/* These 2 booleans are used to monitor if enough information is available in
 * order to procede to step 4 */
upload_ready = false;
step_3_ready = false;

function init_step2() {
    /* In the initiation of step 2 the study SELECT is filled via
     * an AJAX-call to getStudies.jsp */
    $.ajax({
      url: "./getStudies.jsp",
      context: document.body,
      success: function(data){
        $("#selectStudy").html(data);
      }
    });
    $('#step2').show('slow');
}

function init_step3() {
    /* In the initiation of step 3 the assay SELECT is filled via
     * an AJAX-call to getAssays.jsp */
    var st = document.getElementById("selectStudy").value;
    $.ajax({
      url: "./getAssays.jsp?studyToken="+st,
      context: document.body,
      success: function(data){
        $("#selectAssay").html(data);
        $('#step3').show('slow');
      }
    });
}

function init_step4() {
    /* In the initiation of step 4, if both step 3 and the upload are ready, the
     * filename-samplename table is loaded via an AJAX-call to getSamples.jsp */
    if(upload_ready && step_3_ready) {
        $('#step4').show('slow');
        var at = document.getElementById("selectAssay").value;
        var fn = document.getElementById("filename").innerHTML;
        var tf = document.getElementById("tempfolder").innerHTML;
        $.ajax({
          url: "./getSamples.jsp?assayToken="+at+"&filename="+tf+"/"+fn,
          context: document.body,
          success: function(data){
            document.getElementById("drag").innerHTML = data;

            /* Needed to make divs in the table dragable */
            REDIPS.drag.init();
          }
        });
    }
}

function init_step5() {
    //$("#spanstep1").html(document.getElementById('filename').innerHTML);
    //$("#spanstep2").html(document.getElementById('selectStudy').value);
    //$("#spanstep3").html(document.getElementById('selectAssay').value);
    /* TODO: grey out the content of the previous steps */
    $('#step5').show('slow');
}

function savedata()  {
    /* This function is called from the SUBMIT in step 5. It tries to save all
     * submitted data to the CTD database via an AJAX-call to setData.jsp */

    /* We need to get the final matches from the TABLE in step 4. Therefore we
     * parse the content in order to find the hidden INPUTs that contain the tokens */
    lstInputs = document.getElementsByTagName("input");
    res = "";
    for(i=0; i<lstInputs.length; i++) {
        if(lstInputs[i].getAttribute("type")=="hidden") {
            //alert(lstInputs[i].getAttribute("value"));
            if(res.length>0) res += ",";
            res += lstInputs[i].getAttribute("value");
        }
    }

    var st = document.getElementById("selectStudy").value;
    var at = document.getElementById("selectAssay").value;
    var tf = jQuery.trim(document.getElementById("tempfolder").innerHTML);
    var m = res;
    $.ajax({
      url: "./setData.jsp?studyToken="+st+"&assayToken="+at+"&filename="+tf+"&matches="+m,
      context: document.body,
      success: function(data){
          /* TODO: make more sense */
          alert("st: "+st+"\nat: "+st+"\ntf: ["+tf+"]\nres: "+res+"\ndata: "+data);
      }
    });
}