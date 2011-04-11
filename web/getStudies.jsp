<%--
    Document   : getStudies
    Created on : apr 2011
    Author     : Tjeerd van Dijk and Taco Steemers
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<jsp:useBean id="studies" scope="page" class="ctd.services.getStudies"/>

<%
studies.setSessionToken(request.getSession().getId());
String message = studies.getStudies();
out.println(message);
%>