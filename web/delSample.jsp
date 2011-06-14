<%--
    Document   : delSample
    Created on : jun 2011
    Author     : Tjeerd van Dijk
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="delSample" scope="page" class="ctd.services.delSample"/>

<%
delSample.setSessionToken(request.getSession().getId());
delSample.setSampleToken(request.getParameter("sampleToken"));
delSample.setAssayToken(request.getParameter("assayToken"));
String message = delSample.delSample();
out.println(message);
%>