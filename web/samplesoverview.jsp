<%--
    Document   : getSamples
    Created on : apr 2011
    Author     : Tjeerd van Dijk and Taco Steemers
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="samples" scope="page" class="ctd.services.getSamples"/>

<%
samples.setSessionToken(request.getSession().getId());
samples.setAssayToken(request.getParameter("assayToken"));
String message = samples.getSamplesOverview();
out.println(message);
%>