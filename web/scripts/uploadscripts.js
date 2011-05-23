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
    if(!swfobject.hasFlashPlayerVersion("1")) {
        $('#spanstep1').html("<b>No Flash plugin found</b><br/>Your browser needs to support Flash in order to be able to use this upload.");
    } else {
        $('#spanstep1').html("<input type='file' id='file_upload' name='file_upload' />");
        $('#file_upload').uploadify({
          'uploader'     : './scripts/uploadify.swf',
          'script'       : './uploadHandler.jsp',
          'cancelImg'    : './scripts/cancel.png',
          'displayData'  : 'percentage',
          'onSelectOnce' : function() {init_step2()},
          'onComplete'   : function(event, queueID, fileObj, response, data) {
                              $('#filename').html(fileObj.name);
                              $('#tempfolder').html(response.toString());
                              upload_ready = true;
                              $('#spanstep1').html('File uploaded: <i>'+fileObj.name+'</i> <a href="#" onClick="init_step1();"><img src="./scripts/cancel.png" alt="remove file" style="border:0;"/></a>');
                              init_step4();
                          },
          'fileExt'      : '*.zip',
          'fileDesc'     : '.zip files',
          'buttonText'   : 'Select a file',
          'auto'         : true,
          'queueSizeLimit':1,
          'sizeLimit':2147483646
        });
    }
    $('#step1').show('slow');
    upload_ready = false;
    $('#drag').html('...');
    $('#step4').hide();
    $.scrollTo('#step1', 800);
}

function showdiag() {
  launchWindow("#dialog");
}

function closediag() {
    $('#mask').hide();
    $('.window').hide();
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
    if(upload_ready && step_3_ready) {

        document.getElementById("spanstep4").innerHTML = 'You can drag and drop the samples in order to match with the files. Each file should have one sample assigned to it. You need to add the samplenames to GSCF before you can assign files to them.<div id="drag"></div>';
        var at = document.getElementById("selectAssay").value;
        document.getElementById("selectAssay").disabled = true;
        document.getElementById("selectStudy").disabled = true;
        document.getElementById("submitdata").disabled = false;
        var fn = document.getElementById("filename").innerHTML;
        var tf = document.getElementById("tempfolder").innerHTML;
        $.ajax({
          url: "./getSamples.jsp?assayToken="+at+"&filename="+tf+"/"+fn,
          context: document.body,
          success: function(data, textStatus, jqXHR){
            if(jqXHR.getResponseHeader("ErrorInSamples") != null && jqXHR.getResponseHeader("ErrorInSamples") != "") {
                $("#spanstep4").html(data);
                document.getElementById("submitdata").disabled = true;
            } else {
                $("#drag").html(data);
            }
            $('#step4').show('slow');
            $.scrollTo('#step4', 800);
          }
        });
    }
}

function autofill(selectid) {
    //alert(selectid);
    lstSelects = document.getElementById("spanstep4").getElementsByTagName("select");
    blnAutofill = false;
    for(i=0; i<lstSelects.length; i++) {
        if(lstSelects[i].getAttribute("class")=="select_file") {
           if(!blnAutofill) {
               if(lstSelects[i].getAttribute("name")==selectid && lstSelects[i].value!="none") {
                   blnAutofill = true;
                   iOption = lstSelects[i].selectedIndex;
                  //alert("FOUND "+selectid+" "+iOption);
               }
           } else {
               if(lstSelects[i].value=="none") {
                   //alert("changing "+lstSelects[i].getAttribute("name"));
                   iOption = iOption + 1;
                   lstSelects[i].selectedIndex = iOption;
                   //alert(lstSelects[i].getAttribute("name")+" "+iOption);
               } else {
                   //alert(lstSelects[i].getAttribute("name")+" break!! ["+lstSelects[i].getAttribute("value")+"]");
                   break;
               }
           }
        }
    }
}

function savedata()  {
    /* This function is called from the SUBMIT in step 5. It tries to save all
     * submitted data to the CTD database via an AJAX-call to setData.jsp */

    /* Make sure the user can't submit twice */
    document.getElementById("submitdata").value = "Processing data...";
    document.getElementById("submitdata").disabled = true;
    document.getElementById("correct").disabled = true;
    centerPopup();
    loadPopup();
    $("#spanstep1").html('File uploaded: <i>'+document.getElementById("filename").innerHTML+'</i>');

    /* We need to get the final matches from the TABLE in step 4. Therefore we
     * parse the content in order to find the hidden INPUTs that contain the tokens */
    lstSelects = document.getElementById("spanstep4").getElementsByTagName("select");
    res = "";
    for(i=0; i<lstSelects.length; i++) {
        if(lstSelects[i].getAttribute("class")=="select_file") {
            if(lstSelects[i].value!="none") {
                if(res.length>0) res += ",";
                res += lstSelects[i].getAttribute("name")+","+lstSelects[i].value;
            }
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
        $("#diagprocessing").html("<b>Finished</b><br />Your data has been processed, normalized and stored.<br /><a href='#' onClick='closePopup()'>Ok</a>")
      }
    });
}

/* POP UP */

//SETTING UP OUR POPUP
//0 means disabled; 1 means enabled;
var popupStatus = 0;


//loading popup with jQuery magic!
function loadPopup(){
    //loads popup only if it is disabled
    if(popupStatus==0){
        $("#mask").css({
        "opacity": "0.7"
        });
        $("#mask").fadeIn("slow");
        $("#diagprocessing").fadeIn("slow");
        popupStatus = 1;
    }
}

//disabling popup with jQuery magic!
function closePopup(){
    //disables popup only if it is enabled
    if(popupStatus==1){
        $("#mask").fadeOut("slow");
        $("#diagprocessing").fadeOut("slow");
        popupStatus = 0;
        window.location = "./index.jsp?p=overview";
    }
}

//centering popup
function centerPopup(){
    //request data for centering
    var windowWidth = document.documentElement.clientWidth;
    var windowHeight = document.documentElement.clientHeight;
    var popupHeight = $("#diagprocessing").height();
    var popupWidth = $("#diagprocessing").width();
    //centering
    $("#diagprocessing").css({
        "position": "absolute",
        "top": windowHeight/2-popupHeight/2,
        "left": windowWidth/2-popupWidth/2
    });
    //only need force for IE6

    $("#mask").css({
     "height": windowHeight
    });
}
