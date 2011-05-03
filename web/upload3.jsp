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
<div id="info" style="display: block; padding:5px; margin: 3px; background-color: #DDEFFF">
<span class="text_normal">This wizard wil enable you to upload your transcriptomics data to this Clean
Transcriptome Database. In the first step you will be asked to upload a zip-archive that contains
the cel-files you wish to add. In the second step you will need to indicate to which GSCF-study
you want to link this new data, and in the next step you will need to indicate the assay. The last
step will ask you to link the cel-files to specific measurements. These measurements should be
added to the study in GSCF which you would select in step 2, before you start the wizard.</span></div>
<div id="step1" style="display: none; padding:5px; margin: 3px; background-color: #DDEFFF"><h2>1. Upload a zip-archive containing celfiles:</h2><span id="spanstep1">...</span></div>
<div id="step2" style="display: none; padding:5px; margin: 3px; background-color: #DDEFFF"><h2>2. Select a study</h2><span id="spanstep2"><select id='selectStudy' name='selectStudy' onChange='study_selected();'></select></span></div>
<div id="step3" style="display: none; padding:5px; margin: 3px; background-color: #DDEFFF"><h2>3. Select an assay:</h2><span id="spanstep2"><select id='selectAssay' name='selectAssay' onChange='assay_selected();'></select></span></div>
<div id="step4" style="display: none; padding:5px; margin: 3px; background-color: #DDEFFF">
    <h2>4. Link files to samples</h2><br /><span id="spanstep4"></span>&nbsp;
    <input type="submit" id="correct" value="Select a different assay" onClick="$('#step4').hide(); init_step3();"/>

</div>
<div id="filename" style="display: none"></div>
<div id="tempfolder" style="display: none"></div>
<script type="text/javascript">init_step1();</script>