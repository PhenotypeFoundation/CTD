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
    $.ajax({
      url: "./getStudies.jsp",
      context: document.body,
      success: function(data){
        $("#selectAssay").html(data);
      }
    });
    $('#step3').show('slow');
}

function init_step4() {
    if(upload_ready && step_3_ready) {
        $('#step4').show('slow');
    }
}

function init_step5() {
    $("#spanstep1").html("bestandsnaam");
    $("#spanstep2").html(document.getElementById('selectStudy').value);
    $("#spanstep3").html(document.getElementById('selectAssay').value);
    $('#step5').show('slow');
}