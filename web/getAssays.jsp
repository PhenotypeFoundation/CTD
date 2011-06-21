<%--
    Document   : getAssays
    Created on : apr 2011
    Author     : Tjeerd van Dijk and Taco Steemers
--%>
<%@page contentType="text/plain" pageEncoding="UTF-8"%>
<jsp:useBean id="assays" scope="page" class="ctd.services.getAssays"/>

<%
assays.setSessionToken(request.getSession().getId());
assays.setStudyToken(request.getParameter("studyToken"));
String message = assays.getAssays();
out.println(message);
%>