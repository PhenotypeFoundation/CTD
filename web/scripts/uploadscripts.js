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
        loaddiag();
        $('#spanstep1').html("<input type='file' id='file_upload' name='file_upload' />");
        $('#file_upload').uploadify({
          'uploader'     : './scripts/uploadify.swf',
          'script'       : './uploadHandler.jsp',
          'cancelImg'    : './scripts/cancel.png',
          'displayData'  : 'percentage',
          'onSelect'     : function() {init_step2()},
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

function loaddiag() {
    $("#dialog").dialog({
      autoOpen: false,
      open: function(event, ui) {
          $(this).closest('.ui-dialog').find('.ui-dialog-titlebar-close').hide();
      },
      closeOnEscape: false,
      draggable: false,
      modal: true,
      resizable: false
    });
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
var upload_ready = false;
var step_3_ready = false;

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

function validateOptions() {
    var lstSelects = document.getElementById("spanstep4").getElementsByTagName("select");
    var map = new Object();
    var blnError = false;
    var strPrev = "";
    for(i=0; i<lstSelects.length; i++) {
        if(lstSelects[i].getAttribute("class")=="select_file") {
            selectid = lstSelects[i].getAttribute("id");
            if(lstSelects[i].selectedIndex>0) {
                if(map[lstSelects[i].selectedIndex]==null) {
                    map[lstSelects[i].selectedIndex] = i;
                    document.getElementById("errorspan_"+selectid).style.visibility="hidden";
                } else {
                    document.getElementById("errorspan_"+selectid).style.visibility="visible";
                    selectid = lstSelects[map[lstSelects[i].selectedIndex]].getAttribute("id");
                    document.getElementById("errorspan_"+selectid).style.visibility="visible";
                    blnError = true;
                }

                if(strPrev!="") {
                    document.getElementById("link_autofill_"+strPrev).style.visibility="hidden";
                }
                strPrev = selectid;
            } else {
                strPrev = "";
                document.getElementById("errorspan_"+selectid).style.visibility="hidden";
            }
        }
    }
    document.getElementById("submitdata").disabled = blnError;

    return blnError;
}

function updateOptions(selectid) {

    iSelected = document.getElementById(selectid).selectedIndex;

    strVisible = "hidden";
    if(iSelected>0 && iSelected<(document.getElementById(selectid).length-1)) {
        strVisible = "visible";
    }

    document.getElementById("link_autofill_"+selectid).style.visibility=strVisible;

    validateOptions();
}

function autofill(selectid) {
    //alert(selectid);
    lstSelects = document.getElementById("spanstep4").getElementsByTagName("select");
    blnAutofill = false;
    for(i=0; i<lstSelects.length; i++) {
        if(lstSelects[i].getAttribute("class")=="select_file") {
           if(!blnAutofill) {
               if(lstSelects[i].getAttribute("id")==selectid && lstSelects[i].value!="none") {
                   blnAutofill = true;
                   iOption = lstSelects[i].selectedIndex;
                  //alert("FOUND "+selectid+" "+iOption);
               }
           } else {
               if(lstSelects[i].value=="none") {
                   //alert("changing "+lstSelects[i].getAttribute("name"));
                   iOption = iOption + 1;
                   if(iOption<lstSelects[i].length) {
                        lstSelects[i].selectedIndex = iOption;
                   }
                   //alert(lstSelects[i].getAttribute("name")+" "+iOption);
               } else {
                   //alert(lstSelects[i].getAttribute("name")+" break!! ["+lstSelects[i].getAttribute("value")+"]");
                   break;
               }
           }
        }
    }
    document.getElementById("link_autofill_"+selectid).style.visibility="hidden";
    validateOptions();
}

function resetall() {
    lstSelects = document.getElementById("spanstep4").getElementsByTagName("select");
    for(i=0; i<lstSelects.length; i++) {
        if(lstSelects[i].getAttribute("class")=="select_file") {
            lstSelects[i].selectedIndex = 0;
            selectid = lstSelects[i].getAttribute("id");
            document.getElementById("link_autofill_"+selectid).style.visibility="hidden";
            document.getElementById("errorspan_"+selectid).style.visibility="hidden";
        }
    }
    document.getElementById("submitdata").disabled = false;
}

function savedata()  {
    /* This function is called from the SUBMIT in step 5. It tries to save all
     * submitted data to the CTD database via an AJAX-call to setData.jsp */

    /* Make sure the user can't submit twice */
    document.getElementById("submitdata").value = "Processing data...";
    document.getElementById("submitdata").disabled = true;
    document.getElementById("correct").disabled = true;
    $("#dialog").dialog('open')
    $("#spanstep1").html('File uploaded: <i>'+document.getElementById("filename").innerHTML+'</i>');

    /* We need to get the final matches from the TABLE in step 4. Therefore we
     * parse the content in order to find the hidden INPUTs that contain the tokens */
    lstSelects = document.getElementById("spanstep4").getElementsByTagName("select");
    res = "";
    for(i=0; i<lstSelects.length; i++) {
        if(lstSelects[i].getAttribute("class")=="select_file") {
            if(lstSelects[i].value!="none") {
                if(res.length>0) res += ",";
                res += lstSelects[i].getAttribute("id")+","+lstSelects[i].value;
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
        $("#dialog").dialog( "option", "title", 'Finished' );
        $("#dialog").html("Your data has been processed, normalized and stored.");
        $("#dialog").dialog( "option", "buttons", [
            {
                text: "Ok",
                click: function() {
                    $(this).dialog('close')
                    window.location = "./index.jsp?p=overview";
                }
            }
        ] );
      }
    });
}