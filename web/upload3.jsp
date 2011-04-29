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
<h1>Upload Data</h1>
<div id="step1" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF"><h2>1. Upload a .zip containing .cel files:</h2><span id="spanstep1">...</span></div>
<div id="step2" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF"><h2>2. Select a study</h2><span id="spanstep2"><select id='selectStudy' name='selectStudy' onChange='study_selected();'></select></span></div>
<div id="step3" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF"><h2>3. Select an assay:</h2><span id="spanstep2"><select id='selectAssay' name='selectAssay' onChange='assay_selected();'></select></span></div>
<div id="step4" style="display: none; padding:2px; margin: 3px; background-color: #DDEFFF">
    <h2>4. Link files to samples</h2><br /><span id="spanstep4">
    You can drag and drop the samples in order to match with the files. Each file should have one sample assigned to it. You need to add the samplenames to GSCF before you can assign files to them.
    <div id="drag">
        <img src="./images/wait.gif" alt="loading page content..." />
    </div>
    <br /><input type="submit" id="submitdata" value="Save data" onClick="savedata();"/>&nbsp;<input type="submit" id="correct" value="Correct step 2 or step 3" onClick="$('#step4').hide(); init_step2();"/></span>
</div>
<div id="filename" style="display: none"></div>
<div id="tempfolder" style="display: none"></div>
<script type="text/javascript">init_step1();</script>