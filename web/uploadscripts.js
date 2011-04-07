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
        var tf = document.getElementById("tempfolder").innerHTML;
        $.ajax({
          url: "./getSamples.jsp?assayToken="+at+"&filename="+tf+"/"+fn,
          context: document.body,
          success: function(data){
            document.getElementById("drag").innerHTML = data;
            REDIPS.drag.init();
          }
        });
    }
}

function init_step5() {
    //$("#spanstep1").html(document.getElementById('filename').innerHTML);
    //$("#spanstep2").html(document.getElementById('selectStudy').value);
    //$("#spanstep3").html(document.getElementById('selectAssay').value);
    $('#step5').show('slow');
}

function savedata()  {
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
          alert("st: "+st+"\nat: "+st+"\ntf: ["+tf+"]\nres: "+res+"\ndata: "+data);
      }
    });
}