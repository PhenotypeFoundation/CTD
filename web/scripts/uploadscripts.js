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

/* This boolean is used to monitor if a file is available in
 * order to procede to step 4 */
var upload_ready = false;

/**
 * Function that is called by te initialization of step 1 (upload file)
 */
function init_step1() {
    // check if the client has a flash player available
    if(!swfobject.hasFlashPlayerVersion("1")) {
        $('#spanstep1').html("<b>No Flash plugin found</b><br/>Your browser needs to support Flash in order to be able to use this upload.");
    } else {
        // load the dialog that can be shown in step 4
        loaddiag();
        // add an upload to step 1
        $('#spanstep1').html("<input type='file' id='file_upload' name='file_upload' />");
        // show step 1
        $('#spanstep1').show();
        // hide the div that we will use to store the filename
        $('#filestep1').hide();
        // reset step 2, 3 and 4
        $('#step2').hide();
        $('#step3').hide();
        $('#step4').hide();
        $("#selectStudy").html('');
        $("#selectAssay").html('');
        $('#drag').html('...');
        // initiate the uploadify code
        $('#file_upload').uploadify({
          'uploader'     : './scripts/uploadify.swf',
          'script'       : './uploadHandler.jsp',
          'cancelImg'    : './scripts/cancel.png',
          // the displayData code was modified to include absolute and percetage
          'displayData'  : 'percentage',
          // when a file is selected, initiate step 2
          'onSelect'     : function() {init_step2()},
          'onComplete'   : function(event, queueID, fileObj, response, data) {
              // when the upload is complete, store the filename
              $('#filename').html(fileObj.name);
              // store the folder where the file is uploaded
              $('#tempfolder').html(response.toString());
              // indicate that the uploading is complete
              upload_ready = true;
              $('#filestep1').html('File uploaded: <i>'+fileObj.name+'</i> <a href="#" onClick="init_step1();"><img src="./scripts/cancel.png" alt="remove file" style="border:0;"/></a>');
              // show the filename
              $('#filestep1').show();
              // hide the upload button
              $('#spanstep1').hide();
              // try to initiate step 4
              init_step4();
          },
          'onCancel'    : function(event,ID,fileObj,data) {
              // If the upload is canceled, step 2 and 3 should disappear
              $('#step2').hide();
              $('#step3').hide();
              $("#selectStudy").html('');
              $("#selectAssay").html('');
          },
          // only .zip files are allowed
          'fileExt'      : '*.zip',
          'fileDesc'     : '.zip files',
          'buttonText'   : 'Select a file',
          'auto'         : true,
          'queueSizeLimit':1,
          // sizeLimit is maxInt
          'sizeLimit'    : 2147483646
        });
    }
    // show step 1
    $('#step1').show('slow');
    // in the initiation of step 1 the upload is not completed
    upload_ready = false;
    
    // show step 1
    $.scrollTo('#step1', 800);
}

/**
 * Function that is called by te initialization of step 2 (select study)
 * the study SELECT is filled via an AJAX-call to getStudies.jsp
 */
function init_step2() {

    // enable SELECTs in step 2 and 3 and set their values to the first option
    $('#selectAssay').attr('disabled', '');
    $('#selectStudy').attr('disabled', '');
    document.getElementById("selectAssay").selectedIndex = 0;
    document.getElementById("selectStudy").selectedIndex = 0;

    // if no options are present in the SELECT
    if($("#selectStudy option").size()==0) {
        // add a loading span to indicate to the user that the SELECT is being filled
        $('#spanstep2').append("<span id='loadingstep2'><img src='./images/icon_loading.gif' alt='loading' /></span>");
        
        $.ajax({
          url: "./getStudies.jsp",
          context: document.body,
          success: function(data){
            // fill the SELECT with options
            $("#selectStudy").html(data);
            // remove the loading span
            $('#loadingstep2').remove();
          }
        });
    }
    // show step 2
    $('#step2').show('slow');
    $.scrollTo('#step2', 800);
}

/**
 * Function that is called by te initialization of step 3 (select assay)
 * the assay SELECT is filled via an AJAX-call to getAssays.jsp
 */
function init_step3() {
    // if an option is selected in the study SELECT
    if($('#selectStudy').val()!="none") {
        
        // enable the editing of step 2 and 3 and reset step 3
        $('#selectAssay').attr('disabled', '');
        $('#selectStudy').attr('disabled', '');
        $("#selectAssay").html("");

        // add a loading span to indicate to the user that the SELECT is being filled
        $('#spanstep3').append("<span id='loadingstep3'><img src='./images/icon_loading.gif' alt='loading' /></span>");

        // get the token of the selected study (the value of the study SELECT)
        var st = $("#selectStudy").val();
        $.ajax({
          url: "./getAssays.jsp?studyToken="+st,
          context: document.body,
          success: function(data){
            // fill the SELECT with options
            $("#selectAssay").html(data);
            // remove the loading span
            $('#loadingstep3').remove();
          }
        });
        
        // show step 3
        $.scrollTo( '#step3', 800);
        $('#step3').show('slow');
    } else {
        // hide step 3
        $('#step3').hide();
        // initialize step 2
        init_step2();
    }
}

/**
 * Function that is called by te initialization of step 4 (link files to samples)
 * if both step 3 and the upload are ready, the filename-samplename table is
 * loaded via an AJAX-call to getSamples.jsp
 */
function init_step4() {
    // The upload needs to be finished and step 2 and 3 need to have an option selected
    if(upload_ready &&
        (document.getElementById("selectAssay").selectedIndex>0 &&
         document.getElementById("selectStudy").selectedIndex>0)) {

        // add some explainatory text to step 4 (this might have been removed by previous error messages)
        $("#spanstep4").html('You can drag and drop the samples in order to match with the files. Each file should have one sample assigned to it. You need to add the samplenames to GSCF before you can assign files to them.<div id="drag"></div>');
        
        // add a loading span to indicate to the user that the SELECT is being filled
        $('#spanstep4').append("<span id='loadingstep4'><img src='./images/icon_loading.gif' alt='loading' /></span>");
        
        // disable step 2 and 3 and enable the saving button
        $('#selectAssay').attr('disabled', 'disabled');
        $('#selectStudy').attr('disabled', 'disabled');
        $('#submitdata').attr('disabled', '');
        
        // get the values we need for getting the samples
        var at = $("#selectAssay").val();
        var fn = $('#filename').html();
        var tf = $('#tempfolder').html();
        $.ajax({
          url: "./getSamples.jsp?assayToken="+at+"&filename="+tf+"/"+fn,
          context: document.body,
          success: function(data, textStatus, jqXHR){
            // if an errormessage is returned (both checks are needed because of browserissues)
            if(jqXHR.getResponseHeader("ErrorInSamples") != null && jqXHR.getResponseHeader("ErrorInSamples") != "") {
                // the whole of step 4 is filled with this message and saving is disabled
                $("#spanstep4").html(data);
                $('#submitdata').attr('disabled', 'disabled');
            } else {
                // the table with files and samples is shown
                $("#drag").html(data);
            }
            // remove the loading
            $('#loadingstep4').remove();
            
            // scroll to step 4
            $.scrollTo('#step4', 800);
          }
        });
        // show step 4
        $('#step4').show('slow');
        $.scrollTo('#step4', 800);
    }
}

/**
 * Function that is used to load the jquery dialog that is show while the user
 * waits for the completion of the data processing
 */
function loaddiag() {
    $("#dialog").dialog({
      // we want to determine when the dialog shows
      autoOpen: false,
      // if the dialog opens, hide the close button
      open: function(event, ui) {
          $(this).closest('.ui-dialog').find('.ui-dialog-titlebar-close').hide();
      },
      // disable the possibilty to close with escape
      closeOnEscape: false,
      // disable dragging of the dialog
      draggable: false,
      // make the dialog modal (you can't click anything else if this dialog is shown)
      modal: true,
      // disable resizability of the dialog
      resizable: false
    });
}

/**
 * Function that validates all selects in step 4 (checks if there are no double values)
 */
function validateOptions() {
    // get the list of selects
    lstSelects = $('.select_file');
    var map = new Object();
    var strDisabled = '';
    var strPrev = "";
    for(i=0; i<lstSelects.length; i++) {
        // get the id of this select
        selectid = lstSelects[i].getAttribute("id");
        //if an option is selected
        if(lstSelects[i].selectedIndex>0) {
            // if this option is not selected by a previous SELECT
            if(map[lstSelects[i].selectedIndex]==null) {
                // add this option to the map and hide the error message
                map[lstSelects[i].selectedIndex] = i;
                document.getElementById("errorspan_"+selectid).style.visibility="hidden";
            } else {
                // show the error message
                document.getElementById("errorspan_"+selectid).style.visibility="visible";
                selectid = lstSelects[map[lstSelects[i].selectedIndex]].getAttribute("id");
                // show the error message at the SELECT that also has this option
                document.getElementById("errorspan_"+selectid).style.visibility="visible";
                // remember that the save button should be disabled
                strDisabled = 'disabled';
            }

            if(strPrev!="") {
                // if the previous select also has an option selected, hide it's autofill button
                document.getElementById("link_autofill_"+strPrev).style.visibility="hidden";
            }
            
            // remember the previous select
            strPrev = selectid;
        } else {
            // forget the previous select
            strPrev = "";
            // hide the error message of this select
            document.getElementById("errorspan_"+selectid).style.visibility="hidden";
        }
    }
    
    // if there is an error, we need to disable the save button
    $('#submitdata').attr('disabled', strDisabled);
}

/**
 * Function that shows a autofill button in order to enable the user to
 * autofill select-boxes in step 4
 */
function updateOptions(selectid) {
    
    // Get the selected option
    iSelected = document.getElementById(selectid).selectedIndex;
    
    strVisible = "hidden";
    // If neither the first or the last option is selected, show the autofill button
    if(iSelected>0 && iSelected<(document.getElementById(selectid).length-1)) {
        strVisible = "visible";
    }

document.getElementById("link_autofill_"+selectid).style.visibility=strVisible;

    // validate the options
    validateOptions();
}

/**
 * Function that autofills selects in step 4
 */
function autofill(selectid) {
    // get all selects
    lstSelects = $('.select_file');
    // this boolean is used to remember if we have found our SELECT in the list
    blnAutofill = false;
    for(i=0; i<lstSelects.length; i++) {
       if(!blnAutofill) {
           // check if the current SELECT is the one we search for
           if(lstSelects[i].getAttribute("id")==selectid && lstSelects[i].value!="none") {
               // start autofill and remember the option selected by this SELECT
               blnAutofill = true;
               iOption = lstSelects[i].selectedIndex;
           }
       } else {
           // Autofill fills until the next SELECT that is filled
           if(lstSelects[i].value=="none") {
               // Get the next option, and if it is still in the list set the SELECT to this option
               iOption = iOption + 1;
               if(iOption<lstSelects[i].length) {
                    lstSelects[i].selectedIndex = iOption;
               } else {
                   // Stop the loop, we are at the end of the options
                   break;
               }
           } else {
               // Stop the loop, we have reached a filled SELECT
               break;
           }
       }
    }
    // Hide the autofill button
    document.getElementById("link_autofill_"+selectid).style.visibility="hidden";
    // Validate the selected options
    validateOptions();
}

/**
 * Function that resets all selects in step 4
 */
function resetall() {
    // get all selects
    lstSelects = $('.select_file');
    for(i=0; i<lstSelects.length; i++) {
        // set selectindex to 0 (first option)
        lstSelects[i].selectedIndex = 0;
        selectid = lstSelects[i].getAttribute("id");
        // hide the autofill button
        document.getElementById("link_autofill_"+selectid).style.visibility="hidden";
        // hide the errormessage
        document.getElementById("errorspan_"+selectid).style.visibility="hidden";
    }
    // If all options are reset, no errors are present and saving should be permitted
    $('#submitdata').attr('disabled', '');
}

/**
 * Function that is called from the SUBMIT in step 5. It tries to save all
 * submitted data to the CTD database via an AJAX-call to setData.jsp
 */
function savedata()  {

    // Make sure the user can't submit twice
    $('#submitdata').val("Processing data...");
    $('#submitdata').attr('disabled', 'disabled');
    $('#correct').attr('disabled', 'disabled');
    
    // Open the dialog
    $("#dialog").dialog('open')

    /* We need to get the final matches from the SELECTs in step 4. */

    // get all selects
    lstSelects = $('.select_file');
    res = "";
    for(i=0; i<lstSelects.length; i++) {
        // if a select has some option
        if(lstSelects[i].value!="none") {
            if(res.length>0) res += ",";
            // All selects have a token as attribute and the value is the token of the sample
            // All these tokens are comma seperated
            res += lstSelects[i].getAttribute("id")+","+lstSelects[i].value;
        }
    }

    // Get the selected study, assay and folder where the file is
    var st = $("#selectStudy").val();
    var at = $("#selectAssay").val();
    var tf = jQuery.trim($("#tempfolder").html());
    var m = res;
    $.ajax({
      url: "./setData.jsp?studyToken="+st+"&assayToken="+at+"&filename="+tf+"&matches="+m,
      context: document.body,
      success: function(data){
        // Change the content of the dialog in order to enable the user to procede
        $("#dialog").dialog( "option", "title", 'Finished' );
        $("#dialog").html("Your data has been processed, normalized and stored.");
        $("#dialog").dialog( "option", "buttons", [{
          text: "Ok",
          click: function() {
            $(this).dialog('close')
            window.location = "./index.jsp?p=overview";
          }
        }]);
      },
      error: function (xhr, ajaxOptions, thrownError) {
        // If something goes wrong, give an error in the dialog
        $("#dialog").dialog( "option", "title", 'ERROR' );
        $("#dialog").html(xhr.status+" "+thrownError);
      }
    });
}