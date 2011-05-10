/* 
 * Uploadscripts.js
 * By Tjeerd van Dijk and Taco Steemers, thehyve.nl
 * apr 2011
 *
 * Functions in this file are used in upload3.jsp to
 * ensure a good user experience and to place diverse
 * AJAX-calls in order to execute the necessary scripts
 * and catch their response
 */

function init_step1() {
    /* The DIV for step 1 is slowly shown */
    $('#spanstep1').html("<input type='file' id='file_upload' name='file_upload' />");
    $('#file_upload').uploadify({
      'uploader'     : './uploadify/uploadify.swf',
      'script'       : './uploadHandler.jsp',
      'cancelImg'    : './uploadify/cancel.png',
      'displayData'  : 'percentage',
      'onSelectOnce' : function() {init_step2()},
      'onComplete'   : function(event, queueID, fileObj, response, data) {
                          $('#filename').html(fileObj.name);
                          $('#tempfolder').html(response.toString());
                          upload_ready = true;
                          $('#spanstep1').html('File uploaded: <i>'+fileObj.name+'</i> <a href="#" onClick="init_step1();"><img src="./uploadify/cancel.png" alt="remove file" style="border:0;"/></a>');
                          init_step4();
                      },
      'fileExt'      : '*.zip',
      'fileDesc'     : '.zip files',
      'buttonText'   : 'Select a file',
      'auto'         : true,
      'queueSizeLimit':1,
      'sizeLimit':2147483646
    });
    $('#step1').show('slow');
    upload_ready = false;
    $('#drag').html('...');
    $('#step4').hide();
    $.scrollTo('#step1', 800);
}

function study_selected() {
    if(document.getElementById('selectStudy').value!="none") {
        /* If an option is selected in the study SELECT box then we procede to step 3 */
        $('#step3').show('slow');
        init_step3();
    } else {
        $('#step3').hide();
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
    document.getElementById("selectAssay").disabled = false;
    document.getElementById("selectStudy").disabled = false;
    document.getElementById("selectAssay").selectedIndex = 0;
    document.getElementById("selectStudy").selectedIndex = 0;

    if(document.getElementById('selectStudy').value=="") {
        $.ajax({
          url: "./getStudies.jsp",
          context: document.body,
          success: function(data){
            $("#selectStudy").html(data);
          }
        });
    }
    $('#step2').show('slow');
    $.scrollTo('#step2', 800);
}

function init_step3() {
    /* In the initiation of step 3 the assay SELECT is filled via
     * an AJAX-call to getAssays.jsp */
    document.getElementById("selectAssay").disabled = false;
    document.getElementById("selectStudy").disabled = false;
    document.getElementById("selectAssay").selectedIndex = 0;
    step_3_ready = false;

    var st = document.getElementById("selectStudy").value;
    $.ajax({
      url: "./getAssays.jsp?studyToken="+st,
      context: document.body,
      success: function(data){
        $("#selectAssay").html(data);
        $('#step3').show('slow');
        $.scrollTo( '#step3', 800);
      }
    });
}

function init_step4() {
    /* In the initiation of step 4, if both step 3 and the upload are ready, the
     * filename-samplename table is loaded via an AJAX-call to getSamples.jsp */
    document.getElementById("spanstep4").innerHTML = 'You can drag and drop the samples in order to match with the files. Each file should have one sample assigned to it. You need to add the samplenames to GSCF before you can assign files to them.<div id="drag"></div><br /><input type="submit" id="submitdata" value="Save data" onClick="savedata();"/>';
    if(upload_ready && step_3_ready) {
        var at = document.getElementById("selectAssay").value;
        document.getElementById("selectAssay").disabled = true;
        document.getElementById("selectStudy").disabled = true;
        var fn = document.getElementById("filename").innerHTML;
        var tf = document.getElementById("tempfolder").innerHTML;
        $.ajax({
          url: "./getSamples.jsp?assayToken="+at+"&filename="+tf+"/"+fn,
          context: document.body,
          success: function(data, textStatus, jqXHR){
            if(jqXHR.getResponseHeader("ErrorInSamples") != null && jqXHR.getResponseHeader("ErrorInSamples") != "") {
                $("#spanstep4").html(data);
            } else {
                $("#drag").html(data);
                REDIPS.drag.init();
            }
            $('#step4').show('slow');
            $.scrollTo('#step4', 800);
          }
        });
    }
}

function savedata()  {
    /* This function is called from the SUBMIT in step 5. It tries to save all
     * submitted data to the CTD database via an AJAX-call to setData.jsp */

    /* Make sure the user can't submit twice */
    document.getElementById("submitdata").value = "Processing data...";
    document.getElementById("submitdata").disabled = true;
    document.getElementById("correct").disabled = true;
    $("#drag").html('File uploaded: <i>'+document.getElementById("filename").innerHTML+'</i>');
    REDIPS.drag.enable_drag(false);

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
          alert("Your data has been processed, normalized and stored.");
          //Now reload the page from the server.
          window.location = "./index.jsp?p=overview";

      }
    });
}