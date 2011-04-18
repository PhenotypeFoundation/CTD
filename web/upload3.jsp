<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="ctd.services.exceptions.Exception307TemporaryRedirect"%>
<jsp:useBean id="login" scope="session" class="ctd.services.loginGSCF"/>
<%
try {
    login.setSessionToken(request.getSession().getId());
    login.setReturnScript("index.jsp?p=upload3");
    login.loginGSCF();
} catch (Exception307TemporaryRedirect e) {
    //Logger.getLogger(String.class.getName()).log(Level.SEVERE, "REDIRECT! "+e.getError());
    response.setHeader("RedirGSCF", e.getError());
    return;
}
%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">
<jsp:include page="home.jsp" />
        <link href="./uploadify/uploadify.css" type="text/css" rel="stylesheet" />
        <script type="text/javascript" src="./uploadscripts.js"></script>
        <script type="text/javascript" src="./uploadify/swfobject.js"></script>
        <script type="text/javascript" src="./uploadify/jquery.uploadify.v2.1.4.min.js"></script>
        <script type="text/javascript" src="./uploadify/redips-drag-min.js"></script>
        <script type="text/javascript">
          $(document).ready(function() {
            $('#file_upload').uploadify({
              'uploader'     : './uploadify/uploadify.swf',
              'script'       : './uploadHandler.jsp',
              'cancelImg'    : './uploadify/cancel.png',
              'displayData'  : 'percentage',
              'onSelectOnce' : function() {init_step2()},
              'onComplete'   : function(event, queueID, fileObj, response, data) {
                                  document.getElementById("filename").innerHTML=fileObj.name;
                                  document.getElementById("tempfolder").innerHTML=response.toString();
                                  upload_ready = true;
                                  init_step4()
                              },
              'fileExt'      : '*.zip',
              'fileDesc'     : '.cel or .zip files',
              'buttonText'   : 'Click here',
              'auto'         : true,
              'queueSizeLimit':1,
              'sizeLimit':2147483646
            });
          });
          
          /* onLoad */
          init_step1();
        </script>
        <h1>Upload Data</h1>
        <div id="step1" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF"><h2>1. Upload a .zip containing .cel files:</h2><span id="spanstep1"><input type='file' id='file_upload' name='file_upload' /></span></div>
        <div id="step2" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF"><h2>2. Select a study</h2><span id="spanstep2"><select id='selectStudy' name='selectStudy' onChange='study_selected();'></select></span></div>
        <div id="step3" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF"><h2>3. Select an assay:</h2><span id="spanstep2"><select id='selectAssay' name='selectAssay' onChange='assay_selected();'></select></span></div>
        <div id="step4" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF">
            <h2>4. Link files to samples</h2><br />
            You can drag and drop the samples in order to match with the files. Each file should have one sample assigned to it. You need to add the samplenames to GSCF before you can assign files to them.
            <div id="drag">
                ...
            </div>
            <a href="#" onClick="init_step5();">Click here to proceed to the final step.</a>
        </div>
        <div id="step5" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF"><h2>5. Save data</h2>If all of the above information is correct, hit this button and the data will be processed and stored in the database.<br /><input type="submit" id="submitdata" value="Save data" onClick="savedata();"/></div>
        <div id="filename" style="display: none"></div>
        <div id="tempfolder" style="display: none"></div>
