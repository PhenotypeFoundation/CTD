<%--
    Document   : setData
    Created on : apr 2011
    Author     : Tjeerd van Dijk and Taco Steemers
--%>
<%@page contentType="text/plain" import="java.util.*" %>
<%@page contentType="text/plain" pageEncoding="UTF-8"%>
<jsp:useBean id="setData" scope="page" class="ctd.services.setData"/>

<%
setData.setCtdRef(request.getParameter("assayToken"));
setData.setFolder(request.getParameter("filename"));
setData.setStudytoken(request.getParameter("studyToken"));
setData.setSampletokens(request.getParameter("matches"));
setData.setPassword(""+new java.util.Date().getTime());
String message = setData.setData();
out.println(message);
%>