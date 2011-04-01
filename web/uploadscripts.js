/* 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

function init_step1() {
    $('#step1').show('slow');
}

function study_selected() {
    if(document.getElementById('selectStudy').value!="none") {
        $('#step3').show('slow');
        init_step3();
    }
}

function assay_selected() {
    if(document.getElementById('selectAssay').value!="none") {
        step_3_ready = true;
        init_step4();
    }
}

upload_ready = false;
step_3_ready = false;

function init_step2() {
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
    if(upload_ready && step_3_ready) {
        $('#step4').show('slow');
        var at = document.getElementById("selectAssay").value;
        var fn = document.getElementById("filename").innerHTML;
        $.ajax({
          url: "./getSamples.jsp?assayToken="+at+"&filename="+fn,
          context: document.body,
          success: function(data){
            document.getElementById("drag").innerHTML = data;
            REDIPS.drag.init();
          }
        });
    }
}

function init_step5() {
    $("#spanstep1").html(document.getElementById('filename').innerHTML);
    $("#spanstep2").html(document.getElementById('selectStudy').value);
    $("#spanstep3").html(document.getElementById('selectAssay').value);
    $('#step5').show('slow');
}

function savedata()  {
    var m = document.getElementById("drag").innerHTML;
    var spl1 = m.split("<input type='hidden' value='");
    $.ajax({
      url: "./retrieveMatches.jsp?matches="+m,
      context: document.body,
      success: function(data){
        document.getElementById("matches").innerHTML = data;
        REDIPS.drag.init();
      }
    });
}

function savedatasend()  {
    var st = document.getElementById("selectStudy").value;
    var at = document.getElementById("selectAssay").value;
    var fn = document.getElementById("filename").innerHTML;
    var m = document.getElementById("filename").innerHTML;
    $.ajax({
      url: "./setData.jsp?studyToken="+st+"&assayToken="+at+"&filename="+fn+"&matches="+m,
      context: document.body,
      success: function(data){
      }
    });
}