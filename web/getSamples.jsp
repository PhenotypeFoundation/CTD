<%--
    Document   : getSamples
    Created on : apr 2011
    Author     : Tjeerd van Dijk and Taco Steemers
--%>
<%@page contentType="text/plain" pageEncoding="UTF-8"%>
<jsp:useBean id="samples" scope="page" class="ctd.services.getSamples"/>

<%
samples.setSessionToken(request.getSession().getId());
samples.setAssayToken(request.getParameter("assayToken"));
samples.setFilename(request.getParameter("filename"));
String message = samples.getSamples();
if(samples.getError()) {
    response.setHeader("ErrorInSamples", "true");
}
out.println(message);
%>